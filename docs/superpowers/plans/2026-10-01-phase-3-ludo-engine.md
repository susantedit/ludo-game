# Phase 3: Ludo Game Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the pure Kotlin, headless, deterministic Ludo Game Engine (`engine:ludo`) adhering to `docs/04_GAME_RULES.md`, complete with track coordinate math, state transitions, captures, safe squares, three-sixes penalty, and full unit test coverage.

**Architecture:** Pure Kotlin JVM library module (`engine:ludo`) with zero Android SDK dependencies. Immutable data classes define game state (`LudoGameState`, `LudoToken`, `LudoPosition`). Pure functions compute legal moves and state transitions. Deterministic `DiceRoller` injects randomness, emitting discrete `GameEvent` logs for presentation and replay parity.

**Tech Stack:** Kotlin 1.9.22 (JVM), Kotlinx Coroutines, Kotlin Test, JUnit 4.

**Spec:** [docs/04_GAME_RULES.md](file:///d:/ludo/docs/04_GAME_RULES.md) (Sections 1 through 15) and [docs/07_TECH_ARCHITECTURE.md](file:///d:/ludo/docs/07_TECH_ARCHITECTURE.md).

## Global Constraints

- Pure Kotlin JVM module (`engine:ludo`); zero `android.*` or Compose dependencies.
- Absolute determinism: identical initial state + identical actions produce identical result states.
- 52-step common perimeter circuit, 8 safe squares (0, 8, 13, 21, 26, 34, 39, 47).
- Start steps: Red=0, Green=13, Yellow=26, Blue=39.
- Home entrance steps: Red=50, Green=11, Yellow=24, Blue=37.
- Home path: 5 steps (`H1` to `H5`). Center goal requires exact roll.
- Three consecutive sixes penalty: third 6 forfeits turn immediately; previous two moves stand.
- Safe squares: capture immunity and multi-token co-existence allowed.
- Non-safe squares: landing on friendly token is illegal.
- Hard cutoff: maximum 200 rounds to prevent griefing or deadlock.

---

### Task 1: Module Build Configuration and Track Navigation Constants

**Files:**
- Create: `engine/ludo/build.gradle.kts`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoPosition.kt`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoBoard.kt`
- Test: `engine/ludo/src/test/kotlin/game/ludora/engine/ludo/model/LudoBoardTest.kt`

**Interfaces:**
- Consumes: `PlayerColor` from `:core:model`
- Produces: `LudoPosition` sealed class (`InBase`, `OnTrack`, `InHomePath`, `Finished`), `LudoBoard` constant object with coordinate utilities:
  - `startStep(color: PlayerColor): Int`
  - `homeEntranceStep(color: PlayerColor): Int`
  - `isSafeSquare(step: Int): Boolean`
  - `calculateNextPosition(current: LudoPosition, roll: Int, color: PlayerColor): LudoPosition?`

- [x] **Step 1: Create `engine/ludo/build.gradle.kts`**
  Configure pure Kotlin JVM library targeting Java 17, importing `projects.engine.core`, `projects.core.model`, `libs.kotlinx.coroutines.core`, `libs.kotlin.test`, and `libs.junit`.

- [x] **Step 2: Write tests for board coordinates in `LudoBoardTest.kt`**
  Write tests verifying start squares (0, 13, 26, 39), home entrance steps (50, 11, 24, 37), safe squares (8 total), track wrap-around `(step + roll) % 52`, transition into home path `H1..H5`, and exact roll entry into `Finished`.

- [x] **Step 3: Implement `LudoPosition.kt` and `LudoBoard.kt`**
  Implement sealed hierarchy for `LudoPosition` and pure math helper functions in `LudoBoard` to satisfy `LudoBoardTest`.

- [x] **Step 4: Run unit tests**
  Execute tests to verify track math and position navigation pass.

---

### Task 2: State Models and Move Validator

**Files:**
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoToken.kt`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoPlayerState.kt`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoTurnPhase.kt`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoGameState.kt`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/logic/LudoMoveValidator.kt`
- Test: `engine/ludo/src/test/kotlin/game/ludora/engine/ludo/logic/LudoMoveValidatorTest.kt`

**Interfaces:**
- Consumes: `LudoBoard`, `LudoPosition`, `PlayerColor`, `TokenState`, `Player`
- Produces: `LudoGameState`, `LudoPlayerState`, `LudoToken`, `LudoMoveValidator.getLegalMoves(state: LudoGameState, roll: Int): List<LegalMove>`

- [x] **Step 1: Write tests for `LudoMoveValidatorTest.kt`**
  Write unit tests validating:
  - Token in base requires roll 6 to be legal.
  - Token on track cannot land on friendly token on non-safe square.
  - Token on track can land on opponent token on non-safe square (capture move).
  - Multiple tokens can co-exist on safe square.
  - Overshoot beyond goal on home path is illegal.
  - Returns empty list when no legal moves are available.

- [x] **Step 2: Implement state models**
  Create immutable data classes `LudoToken`, `LudoPlayerState`, enum `LudoTurnPhase`, and `LudoGameState`.

- [x] **Step 3: Implement `LudoMoveValidator.kt`**
  Write pure function evaluating all 4 tokens for the active player against the rolled value, yielding legal move destinations.

- [x] **Step 4: Run unit tests**
  Execute `LudoMoveValidatorTest` to verify legal move evaluation.

---

### Task 3: Turn State Transitions and Action Resolution

**Files:**
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoAction.kt`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/model/LudoEvent.kt`
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/logic/LudoStateReducer.kt`
- Test: `engine/ludo/src/test/kotlin/game/ludora/engine/ludo/logic/LudoStateReducerTest.kt`

**Interfaces:**
- Consumes: `LudoGameState`, `LudoAction`, `LudoMoveValidator`, `LudoBoard`
- Produces: `LudoStateReducer.reduce(state: LudoGameState, action: LudoAction): Pair<LudoGameState, List<LudoEvent>>`

- [x] **Step 1: Write tests for `LudoStateReducerTest.kt`**
  Write tests verifying:
  - `RollDice`: updates `currentRoll`, transitions phase to `WAITING_FOR_MOVE` or passes turn if no legal moves.
  - 3 consecutive sixes: forfeits 3rd roll and advances turn clockwise.
  - `MoveToken`: advances token, applies capture (opponent sent to base, bonus roll granted).
  - `MoveToken` into goal: grants bonus roll (unless 4th token that wins match).
  - Roll 6 grants bonus roll.
  - Multiple player turns cycle clockwise skipping finished players.

- [x] **Step 2: Implement `LudoAction.kt` and `LudoEvent.kt`**
  Define sealed classes `LudoAction` (`RollDice`, `SelectMove(tokenId)`, `PassTurn`) and `LudoEvent` (`DiceRolled`, `TokenMoved`, `TokenCaptured`, `BonusRollGranted`, `TurnPassed`, `PlayerFinished`, `MatchCompleted`).

- [x] **Step 3: Implement `LudoStateReducer.kt`**
  Implement pure state reducer handling all actions, transitions, capture checks, bonus rolls, turn advancement, and win determination.

- [x] **Step 4: Run unit tests**
  Execute tests to verify all turn reduction logic and events.

---

### Task 4: Complete Headless Ludo Engine and End-to-End Simulation

**Files:**
- Create: `engine/ludo/src/main/kotlin/game/ludora/engine/ludo/LudoGameEngine.kt`
- Test: `engine/ludo/src/test/kotlin/game/ludora/engine/ludo/LudoGameEngineTest.kt`
- Test: `engine/ludo/src/test/kotlin/game/ludora/engine/ludo/LudoSimulationTest.kt`

**Interfaces:**
- Consumes: `GameEngine<LudoGameState, LudoAction>`, `DiceRoller`, `LudoStateReducer`
- Produces: Full `LudoGameEngine` implementation conforming to `GameEngine` contract in `:engine:core`.

- [x] **Step 1: Write integration tests in `LudoGameEngineTest.kt`**
  Verify complete turn-by-turn game flow using a deterministic `TestDiceRoller` (canned sequence of dice rolls).

- [x] **Step 2: Write 100-match headless fuzz/simulation test in `LudoSimulationTest.kt`**
  Run simulated games with automated random legal move choices, ensuring zero crashes, invariant checks (never >4 tokens per player, tokens stay in bounds), and all games terminate within 200 rounds cutoff.

- [x] **Step 3: Implement `LudoGameEngine.kt`**
  Wire up `GameEngine` contract, `StateFlow<LudoGameState>`, `SharedFlow<GameEvent>`, and coroutine synchronization.

- [x] **Step 4: Execute test suite**
  Verify all unit and simulation tests pass.

---

### Task 5: Documentation and Ledger Update

**Files:**
- Modify: `docs/21_DECISIONS.md` (Log Phase 3 Ludo Engine implementation decisions)
- Modify: `CHANGELOG.md` (Add Phase 3 Ludo Engine entry)
- Modify: `docs/superpowers/plans/2026-10-01-phase-3-ludo-engine.md` (Update task completion status)

- [x] **Step 1: Audit all module files and test coverage**
  Ensure code matches specifications in `docs/04_GAME_RULES.md`.

- [x] **Step 2: Update changelog and architectural decisions**
  Document the completion of Phase 3 pure Kotlin Ludo engine.

