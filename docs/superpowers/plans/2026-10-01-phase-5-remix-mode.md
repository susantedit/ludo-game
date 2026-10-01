# Phase 5: Remix Mode Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 5 Remix Mode as defined in `docs/19_ROADMAP.md`:
1. Hybrid Rules Engine: Ludo 52-step track infused with Snake & Ladder hazard tiles (ladders catapult tokens forward, snakes swallow tokens backward).
2. Power Card System: Tactile cards including `SHIELD` (immunity), `SPEED_BOOST` (+2 roll), `REROLL` (second chance dice roll), `SWAP` (exchange positions with opponent), and `BOMB` (radial blast).
3. Round Chaos Modifiers: Dynamic round modifiers such as `DOUBLE_ROLL`, `REVERSE_DIRECTION`, and `HAZARD_RUSH`.
4. Interactive UI: 15x15 Canvas Ludo board rendering visual hazard ladders and snakes directly on track tiles, an interactive power card inventory carousel, and live card activation animations.

**Architecture:** Pure Kotlin JVM engine (`engine:remix`) layered atop `:engine:ludo` and `:engine:core`, with Compose Canvas rendering in `:app`.

---

### Task 1: Hybrid Hazards and Chaos Modifiers Models

**Files:**
- Modify: `engine/remix/src/main/kotlin/game/ludora/engine/remix/model/PowerUp.kt`
- Create: `engine/remix/src/main/kotlin/game/ludora/engine/remix/model/RemixHazard.kt`
- Create: `engine/remix/src/main/kotlin/game/ludora/engine/remix/model/ChaosModifier.kt`

- [x] **Step 1: Expand PowerUpType with `REROLL`**
- [x] **Step 2: Define `RemixHazard` with track ladders and snakes**
  - Track Ladders: `6 -> 18`, `22 -> 32`, `36 -> 44`
  - Track Snakes: `16 -> 4`, `28 -> 14`, `42 -> 24`
- [x] **Step 3: Define `ChaosModifier` enum (`NONE`, `DOUBLE_ROLL`, `HAZARD_RUSH`, `SHIELD_FRENZY`)**

---

### Task 2: Pure Remix State Reducer and Engine Expansion

**Files:**
- Modify: `engine/remix/src/main/kotlin/game/ludora/engine/remix/RemixGameEngine.kt`
- Test: `engine/remix/src/test/kotlin/game/ludora/engine/remix/RemixGameEngineTest.kt`

- [x] **Step 1: Add hazard resolution logic when landing on track**
  - If token lands on ladder base, climb to top (unless shielded).
  - If token lands on snake head, drop to tail (unless shielded).
- [x] **Step 2: Add `REROLL` power-up action and state handling**
- [x] **Step 3: Add Chaos Modifier round rotation**
- [x] **Step 4: Update unit tests for all hazards and cards**

---

### Task 3: Interactive UI Enhancement with Visual Track Hazards

**Files:**
- Modify: `app/src/main/kotlin/game/ludora/ui/remix/RemixGameScreen.kt`

- [x] **Step 1: Render visual hazard icons (ladders and snakes) on Ludo track tiles**
- [x] **Step 2: Add Reroll button and inventory count indicators**
- [x] **Step 3: Display active Chaos Modifier banner**

---

### Task 4: Documentation and Decisions Record

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- [x] **Step 1: Log Phase 5 decision and changelog updates**
