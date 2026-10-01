# Phase 6: Progression Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 6 Progression system as defined in `docs/19_ROADMAP.md`:
1. Local Profile Management & Stats: Stats persistence for matches played, matches won, tokens captured, win rate, coins, and XP.
2. XP Engine & Level Milestones: Match outcome rewards (placement, captures), level calculation with scaling XP thresholds, and milestone rewards.
3. Offline Cosmetic Inventory: Dice skins, token styles, and board themes with equipped cosmetic state.
4. Daily Quest Engine: Quest tracking based on gameplay events (wins, captures, rolls) and offline reward claiming.
5. Interactive UI: Comprehensive Profile, Progression, Cosmetics, and Quest views integrated into the dashboard, plus post-match reward dialogs.

**Architecture:** Pure domain logic in `:core:model` and `:core:database`, with UI components in `:core:designsystem` and `:app`.

---

### Task 1: Domain Models for Progression, Cosmetics, and Quests

**Files:**
- Create: `core/model/src/main/kotlin/game/ludora/core/model/CosmeticItem.kt`
- Create: `core/model/src/main/kotlin/game/ludora/core/model/DailyQuest.kt`
- Create: `core/model/src/main/kotlin/game/ludora/core/model/MatchReward.kt`
- Modify: `core/model/src/main/kotlin/game/ludora/core/model/LocalProfile.kt`

- [x] **Step 1: Define `CosmeticItem` categories (DiceSkin, TokenStyle, BoardTheme)**
- [x] **Step 2: Define `DailyQuest` model with progress, target, and claim state**
- [x] **Step 3: Define `MatchReward` calculation model**
- [x] **Step 4: Update `LocalProfile` with equipped cosmetics and quest IDs**

---

### Task 2: Pure Progression & Quest Engine

**Files:**
- Create: `core/common/src/main/kotlin/game/ludora/core/common/progression/ProgressionEngine.kt`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/progression/DailyQuestEngine.kt`
- Test: `core/common/src/test/kotlin/game/ludora/core/common/progression/ProgressionEngineTest.kt`

- [x] **Step 1: Implement `ProgressionEngine` for XP, level scaling, and rewards**
- [x] **Step 2: Implement `DailyQuestEngine` for quest generation, progress update, and claiming**
- [x] **Step 3: Write comprehensive unit tests for XP scaling, level-up milestones, and quest claiming**

---

### Task 3: Profile & Progression UI in App

**Files:**
- Create: `app/src/main/kotlin/game/ludora/ui/profile/ProfileProgressionSheet.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/common/MatchRewardDialog.kt`
- Modify: `app/src/main/kotlin/game/ludora/MainActivity.kt`
- Modify: `app/src/main/kotlin/game/ludora/ui/ludo/LudoGameScreen.kt`
- Modify: `app/src/main/kotlin/game/ludora/ui/snake/SnakeGameScreen.kt`
- Modify: `app/src/main/kotlin/game/ludora/ui/remix/RemixGameScreen.kt`

- [x] **Step 1: Implement `ProfileProgressionSheet` with Stats, XP Bar, Cosmetics, and Daily Quests**
- [x] **Step 2: Implement `MatchRewardDialog` showing earned XP, coins, and level progress**
- [x] **Step 3: Wire profile sheet and post-match reward triggers into MainActivity and Game Screens**

---

### Task 4: Documentation and Decisions Record

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- [x] **Step 1: Log Phase 6 decision and changelog updates**
