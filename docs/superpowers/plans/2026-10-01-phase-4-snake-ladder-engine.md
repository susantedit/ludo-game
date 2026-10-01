# Phase 4: Snake & Ladder Game Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the pure Kotlin, headless, deterministic Snake & Ladder Game Engine (`engine:snake`) conforming to `docs/04_GAME_RULES.md`, complete with 100-square boustrophedon math, ladder ascents, snake drops, exact finish stalls, 3-consecutive-sixes penalty, and unit/simulation tests.

**Architecture:** Pure Kotlin JVM library module (`engine:snake`) with zero Android SDK dependencies. Immutable data classes define board configuration, player tokens, and game state (`SnakeGameState`). Pure reducer handles movement, hazard checks, bonus rolls, and win conditions. Conforms to `GameEngine<SnakeGameState, EngineAction>` in `:engine:core`.

**Tech Stack:** Kotlin 1.9.22 (JVM), Kotlinx Coroutines, Kotlin Test, JUnit 4.

**Spec:** [docs/04_GAME_RULES.md](file:///d:/ludo/docs/04_GAME_RULES.md) (Sections 17 through 19) and [docs/07_TECH_ARCHITECTURE.md](file:///d:/ludo/docs/07_TECH_ARCHITECTURE.md).

## Global Constraints

- Pure Kotlin JVM module (`engine:snake`); zero Android dependencies.
- 100-square board (`1..100`), starting at virtual square `0`.
- 7 classic ladders: `4->14`, `9->31`, `20->38`, `28->84`, `40->59`, `51->67`, `63->81`.
- 8 classic snakes: `17->7`, `54->34`, `62->19`, `64->60`, `87->24`, `93->73`, `95->75`, `99->78`.
- Roll of any value (1..6) enters board from square 0.
- Exact roll required for square 100; overshoot stalls in place.
- Rolling 6 grants bonus roll; third consecutive 6 forfeits turn.
- Multiple tokens can occupy the same tile without captures.
- Maximum 200 rounds cutoff for anti-stall defense.

---

### Task 1: Module Build Configuration and Board Matrix Constants

**Files:**
- Create: `engine/snake/build.gradle.kts`
- Create: `engine/snake/src/main/kotlin/game/ludora/engine/snake/model/SnakeBoard.kt`
- Test: `engine/snake/src/test/kotlin/game/ludora/engine/snake/model/SnakeBoardTest.kt`

**Interfaces:**
- Consumes: `PlayerColor` from `:core:model`
- Produces: `SnakeBoard` object with:
  - `TOTAL_SQUARES = 100`
  - `LADDERS: Map<Int, Int>` (7 classic ladders)
  - `SNAKES: Map<Int, Int>` (8 classic snakes)
  - `calculateDestination(currentSquare: Int, roll: Int): SquareTransition`
  - `boustrophedonCoordinates(square: Int): Pair<Int, Int>` (row 0..9, col 0..9)

- [ ] **Step 1: Create `engine/snake/build.gradle.kts`**
  Configure pure Kotlin JVM library targeting Java 17, importing `projects.engine.core`, `projects.core.model`, `libs.kotlinx.coroutines.core`, `libs.kotlin.test`, and `libs.junit`.

- [ ] **Step 2: Write tests in `SnakeBoardTest.kt`**
  Verify all 7 ladders, 8 snakes, starting from square 0, overshoot stall at 100 (e.g. 98 + 4 = 98), exact win (98 + 2 = 100), ladder ascent (e.g. 4 -> 14), snake drop (e.g. 17 -> 7), and boustrophedon grid coordinate math.

- [ ] **Step 3: Implement `SnakeBoard.kt`**
  Implement board constants and pure navigation functions to pass `SnakeBoardTest`.

- [ ] **Step 4: Run tests**
  Execute tests to verify board math.

---

### Task 2: State Models and Turn Reducer

**Files:**
- Create: `engine/snake/src/main/kotlin/game/ludora/engine/snake/model/SnakePlayerState.kt`
- Create: `engine/snake/src/main/kotlin/game/ludora/engine/snake/model/SnakeGameState.kt`
- Create: `engine/snake/src/main/kotlin/game/ludora/engine/snake/model/SnakeAction.kt`
- Create: `engine/snake/src/main/kotlin/game/ludora/engine/snake/model/SnakeEvent.kt`
- Create: `engine/snake/src/main/kotlin/game/ludora/engine/snake/logic/SnakeStateReducer.kt`
- Test: `engine/snake/src/test/kotlin/game/ludora/engine/snake/logic/SnakeStateReducerTest.kt`

**Interfaces:**
- Consumes: `SnakeBoard`, `Player`, `PlayerColor`
- Produces: `SnakeGameState`, `SnakePlayerState`, `SnakeAction`, `SnakeEvent`, `SnakeStateReducer.reduce(...)`

- [ ] **Step 1: Write tests in `SnakeStateReducerTest.kt`**
  Write tests verifying:
  - Normal roll forward advancement.
  - Ladder climbed emits event and updates position to ladder top.
  - Snake drop emits event and updates position to snake tail.
  - Roll 6 grants bonus roll.
  - 3 consecutive sixes forfeits turn immediately.
  - Exact roll to 100 wins game.
  - Overshoot stalls in place and turn passes.
  - Anti-stall 200-round cutoff ranking.

- [ ] **Step 2: Implement state models, actions, and events**
  Create `SnakePlayerState`, `SnakeGameState`, `SnakeAction`, and `SnakeEvent`.

- [ ] **Step 3: Implement `SnakeStateReducer.kt`**
  Implement pure state reducer handling movement, hazard triggers, consecutive sixes, turn rotations, and win conditions.

- [ ] **Step 4: Run unit tests**
  Execute tests to verify all state transitions.

---

### Task 3: Complete Snake & Ladder Engine and Simulation Suite

**Files:**
- Create: `engine/snake/src/main/kotlin/game/ludora/engine/snake/SnakeGameEngine.kt`
- Test: `engine/snake/src/test/kotlin/game/ludora/engine/snake/SnakeGameEngineTest.kt`
- Test: `engine/snake/src/test/kotlin/game/ludora/engine/snake/SnakeSimulationTest.kt`

**Interfaces:**
- Consumes: `GameEngine<SnakeGameState, EngineAction>`, `DiceRoller`, `SnakeStateReducer`
- Produces: `SnakeGameEngine` implementation conforming to `GameEngine` contract in `:engine:core`.

- [ ] **Step 1: Write integration tests in `SnakeGameEngineTest.kt`**
  Test complete game flow with deterministic `FixedDiceRoller`.

- [ ] **Step 2: Write 100-match headless simulation test in `SnakeSimulationTest.kt`**
  Run 100 complete simulated matches, asserting zero crashes, valid invariants (square in 0..100), and all matches terminating.

- [ ] **Step 3: Implement `SnakeGameEngine.kt`**
  Implement engine facade with `rulesetId = "SNAKE_LADDER_CLASSIC_V1"`.

- [ ] **Step 4: Run test suite**
  Execute all tests and simulations.

---

### Task 4: Documentation and Ledger Update

**Files:**
- Modify: `docs/21_DECISIONS.md` (Log Phase 4 Snake & Ladder engine decision)
- Modify: `CHANGELOG.md` (Add Phase 4 entry)
- Modify: `docs/superpowers/plans/2026-10-01-phase-4-snake-ladder-engine.md` (Check off tasks)
