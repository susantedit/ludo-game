# Phase 8: Online Multiplayer Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 8 Online Multiplayer system as defined in `docs/19_ROADMAP.md` and `docs/06_ONLINE_ARCHITECTURE.md`:
1. Private Rooms: Host lobby creation, 6-character room codes (`K7X92P`), clipboard copy, guest join flow, player seating, and host start verification.
2. Casual & Ranked Matchmaking: Queue ticketing, skill-based pairing (MMR), latency clustering, and 45-second timeout fallback offering AI autofill.
3. Server-Authoritative Loop with Turn Timers: Turn countdown timer (10s/15s/30s), automated timeout actions (forfeiture or bot move), and authoritative event fan-out.
4. Reconnection & Reconciliation: 60-second disconnect grace periods and full state catch-up snapshot transmission.
5. Interactive UI: Online lobby screen, room code entry dialog, quick match search radar, and live online match views.

**Architecture:** Pure Kotlin logic in `:core:network`, with interactive Compose screens in `:app`.

---

### Task 1: Matchmaking Engine and Queue Models

**Files:**
- Create: `core/network/src/main/kotlin/game/ludora/core/network/matchmaking/MatchmakingTicket.kt`
- Create: `core/network/src/main/kotlin/game/ludora/core/network/matchmaking/Matchmaker.kt`
- Test: `core/network/src/test/kotlin/game/ludora/core/network/matchmaking/MatchmakerTest.kt`

- [x] **Step 1: Define `MatchmakingTicket` with player ID, game type, MMR, and timestamp**
- [x] **Step 2: Implement `Matchmaker` pairing compatible tickets within MMR thresholds**
- [x] **Step 3: Implement 45-second timeout handling and AI filler option**
- [x] **Step 4: Unit test matchmaking ticket pooling and pairing**

---

### Task 2: Server-Authoritative Online Match Coordinator

**Files:**
- Create: `core/network/src/main/kotlin/game/ludora/core/network/match/OnlineMatchCoordinator.kt`
- Test: `core/network/src/test/kotlin/game/ludora/core/network/match/OnlineMatchCoordinatorTest.kt`

- [x] **Step 1: Implement `OnlineMatchCoordinator` driving authoritative game engine steps**
- [x] **Step 2: Implement turn countdown timer and automatic timeout actions**
- [x] **Step 3: Implement 60-second disconnect grace timer and snapshot state reconciliation**
- [x] **Step 4: Unit test turn timeout forfeiture and reconnection snapshot recovery**

---

### Task 3: Interactive Online UI in App

**Files:**
- Create: `app/src/main/kotlin/game/ludora/ui/online/OnlineLobbyScreen.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/online/JoinRoomDialog.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/online/QuickMatchSearchingOverlay.kt`
- Modify: `app/src/main/kotlin/game/ludora/MainActivity.kt`

- [x] **Step 1: Implement `OnlineLobbyScreen` showing room code, copy button, player roster, and start button**
- [x] **Step 2: Implement `JoinRoomDialog` with 6-char input and uppercase formatting**
- [x] **Step 3: Implement `QuickMatchSearchingOverlay` with search radar and elapsed time**
- [x] **Step 4: Connect Online Multiplayer mode into MainActivity navigation**

---

### Task 4: Documentation and Decisions Record

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- [x] **Step 1: Log Phase 8 decision and changelog updates**
