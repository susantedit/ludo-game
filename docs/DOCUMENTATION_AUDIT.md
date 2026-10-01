# Documentation Audit Report - Ludora

Audit Date: 2026-10-01  
Auditor: Antigravity Agent  
Scope: All 21 documents under `/docs`

---

## 1. Executive Summary

A comprehensive audit was performed across all 21 specification and architecture documents in the `docs/` directory. The documentation provides a strong, coherent offline-first baseline with modern architecture principles (Kotlin, Jetpack Compose, server-authoritative online multiplayer, zero-ad offline guarantees). 

However, before implementation begins, several critical rule divergences, scope alignments, and unconfirmed technical decisions must be resolved to prevent costly refactors during engine and backend development.

---

## 2. Document Readiness Classification

| Document | Status | Main Issue |
| :--- | :--- | :--- |
| `01_PRD.md` | NEEDS CLARIFICATION | Room/Key-Value marked TBD while decided in Tech Architecture; Three-sixes rule ambiguity. |
| `02_PRODUCT_VISION.md` | READY | Vision, mission, and brand principles are clear and aligned with offline-first goals. |
| `03_GAME_DESIGN.md` | NEEDS CLARIFICATION | Safe square multi-occupancy marked TBD while defined as standard in Game Rules. |
| `04_GAME_RULES.md` | CONTRADICTORY | Three-sixes penalty voids "all movement from those rolls" vs PRD "forfeits third roll only". |
| `05_OFFLINE_ARCHITECTURE.md` | READY | Offline autonomy, state serialization, save/resume, and zero-network guarantees are well defined. |
| `06_ONLINE_ARCHITECTURE.md` | NEEDS CLARIFICATION | Transport protocol marked TBD (WebSockets vs gRPC) while 07 and 10 assume WebSockets. |
| `07_TECH_ARCHITECTURE.md` | READY | Multi-module breakdown, Compose UI architecture, and dependency hygiene are explicit. |
| `08_DATA_MODEL.md` | NEEDS CLARIFICATION | Missing Spectator, Room Host migration field, and multi-token Snake & Ladder coordinates. |
| `09_API_SPEC.md` | NEEDS CLARIFICATION | Missing endpoint for account linking conflict resolution and token refreshing. |
| `10_MULTIPLAYER_PROTOCOL.md` | READY | Bidirectional command/event framing, sequence numbers, and timeout handling are solid. |
| `11_SECURITY_PLAN.md` | READY | Threat model, trust boundaries, secure RNG, SSV ad callbacks, and anti-cheat policies are detailed. |
| `12_ADS_MONETIZATION.md` | READY | Clear offline ad suppression, frequency caps (8-min cooldown), and clean provider interface. |
| `13_UI_UX_SPEC.md` | READY | Information architecture, screen flows, HUD layouts, and error/empty states are complete. |
| `14_DESIGN_SYSTEM.md` | READY | Color tokens, typography, 48dp touch targets, and color-blind shape markers are well specified. |
| `15_ANIMATION_SPEC.md` | READY | Snappy motion principles, step timings (150ms), and state-driven animations are defined. |
| `16_ACCESSIBILITY.md` | READY | WCAG 2.1 AA standards, TalkBack semantics, high-contrast modes, and reduced motion documented. |
| `17_TESTING_PLAN.md` | NEEDS CLARIFICATION | Missing test cases for Snake & Ladder bounce-back rule and Ludo 3-sixes penalty edge cases. |
| `18_PERFORMANCE_PLAN.md` | READY | 60fps frame budgets, 1500ms startup target, 120MB heap limit, and low-end device profiles set. |
| `19_ROADMAP.md` | NEEDS CLARIFICATION | MVP boundary vs Roadmap Phase mapping needs explicit definition (Phases 1-6 vs MVP). |
| `20_DEVELOPMENT_RULES.md` | READY | Engineering constraints, skill integration rules, and test-first policies are explicit. |
| `21_DECISIONS.md` | NEEDS CLARIFICATION | Needs official recording of resolved rule choices and standardized terminology glossary. |

---

## 3. Contradictions Identified

### Contradiction 1: Ludo Three-Sixes Penalty Effect
- **Document 1:** `01_PRD.md`, Section 8: "By default, rolling three consecutive 6s forfeits the third roll and advances the turn immediately."
- **Document 2:** `04_GAME_RULES.md`, Section "Optional and Custom Rules": "Rolling three 6s in a row voids all movement from those rolls and passes the turn."
- **Why they conflict:** The PRD states that only the third roll is forfeited (tokens moved on the 1st and 2nd rolls remain at their new positions). The Game Rules specification states that all movement resulting from all three rolls is revoked.
- **Recommended resolution:** Align on the standard competitive rule: rolling three consecutive 6s voids only the third roll and immediately forfeits the turn, preserving valid moves made on rolls 1 and 2. Voids-all-three should be an optional house rule toggle.

### Contradiction 2: Ludo Safe Square Multi-Occupancy Authority
- **Document 1:** `03_GAME_DESIGN.md`, Section "Ludo": "Multiple tokens from different players may occupy safe tiles concurrently: TBD - requires a later architectural decision."
- **Document 2:** `04_GAME_RULES.md`, Section "Safe Zones": "Multiple tokens from different players can co-exist on a safe square without conflict."
- **Document 3:** `01_PRD.md`, Section 8: "Multiple tokens can occupy safe squares simultaneously."
- **Why they conflict:** Game Design treats multi-piece occupancy on star/start squares as an unmade decision, whereas PRD and Game Rules specify it as standard behavior.
- **Recommended resolution:** Formally accept that multiple tokens from different players can co-exist on safe squares simultaneously. Update `03_GAME_DESIGN.md` to remove the TBD flag.

### Contradiction 3: Snake & Ladder Finish Condition
- **Document 1:** `01_PRD.md`, Section 9: "Exact roll required on square 100. Configurable: Excess roll bounces backward from square 100 or causes the token to remain stationary: TBD - requires architectural decision."
- **Document 2:** `04_GAME_RULES.md`, Section "Finish Line": "Standard rule: Exact roll required to land on square 100. If the roll exceeds the remaining steps, the token remains stationary, or bounces back depending on rule configuration."
- **Document 3:** `17_TESTING_PLAN.md`, Section "Game Engine Rule Verification": Only tests `test_square_100_exact_win`. No bounce-back test case exists.
- **Why they conflict:** Testing plan lacks test coverage for the bounce-back configuration mentioned in PRD and Game Rules.
- **Recommended resolution:** Establish that the default rule is "Overshoot Stalls" (piece stays stationary if roll exceeds distance to 100). "Overshoot Bounces Back" is a configurable rule. Add `test_square_100_overshoot_stalls` and `test_square_100_overshoot_bounces` to `17_TESTING_PLAN.md`.

### Contradiction 4: Transport Layer Decision Status
- **Document 1:** `06_ONLINE_ARCHITECTURE.md`, Section "Realtime Communication": "Transport Layer: Secure WebSockets (WSS) or gRPC streaming: TBD - requires a later architectural decision."
- **Document 2:** `07_TECH_ARCHITECTURE.md`, Section "Networking Layer": "Realtime Streaming: WebSocket client built on OkHttp WebSocket with exponential backoff reconnection."
- **Document 3:** `10_MULTIPLAYER_PROTOCOL.md`, Section "Protocol Overview": "It operates over full-duplex WebSocket connections with binary Protobuf or structured JSON framing."
- **Why they conflict:** 06_ONLINE_ARCHITECTURE leaves transport open to gRPC, while 07_TECH_ARCHITECTURE and 10_MULTIPLAYER_PROTOCOL have already written concrete WebSocket specifications.
- **Recommended resolution:** Formally record in `21_DECISIONS.md` that WebSocket over TLS (WSS) is the chosen transport protocol for Android due to standard OkHttp support and low proxy friction. Update `06_ONLINE_ARCHITECTURE.md` to reference the decision.

### Contradiction 5: Local Storage Engine Decision Status
- **Document 1:** `01_PRD.md`, Section 7: "Local Persistence Layer (Room Database / Key-Value Store: TBD - requires architectural decision)."
- **Document 2:** `05_OFFLINE_ARCHITECTURE.md` & `07_TECH_ARCHITECTURE.md`: Both mandate Room (SQLite) for relational entities and Jetpack DataStore for preferences.
- **Document 3:** `21_DECISIONS.md`: Logs Room as part of the accepted stack.
- **Why they conflict:** The PRD retains an outdated TBD tag on local persistence architecture.
- **Recommended resolution:** Update `01_PRD.md` Section 7 to state Room (SQLite) and Jetpack DataStore as the accepted local persistence baseline.

---

## 4. Missing Requirements Identified

### Product & Scope
1. **MVP Boundary Definition in Roadmap:** `01_PRD.md` defines MVP as complete offline Ludo, Snake & Ladder, foundation Remix, local multiplayer, and AI. `19_ROADMAP.md` sequences this across Phase 1 (Foundation), Phase 2 (Design System), Phase 3 (Ludo), Phase 4 (Snake), Phase 5 (Remix), and Phase 6 (Progression). The documentation must explicitly state that **Roadmap Phases 1 through 6 collectively constitute the MVP**.
2. **Account Linking Conflict Merge Logic:** `01_PRD.md` and `06_ONLINE_ARCHITECTURE.md` state that guest accounts can be linked to Google Play Games or Apple Game Center. However, neither specifies what happens if the cloud account already has existing progress:
   - Does cloud overwrite local?
   - Does local overwrite cloud?
   - Do coins and XP sum together?
   *Recommendation:* Use "Highest Level Wins" for profile level and max XP, with currency balances summed.

### Gameplay
1. **Draw and Stalemate Handling:** Neither `03_GAME_DESIGN.md` nor `04_GAME_RULES.md` specifies what happens if a match reaches an unresolvable stalemate (e.g., both remaining players have tokens blocked or match exceeds 500 turns).
   *Recommendation:* Introduce a maximum turn limit (e.g., 200 rounds). If reached without a winner, the match concludes as a Draw, or awards placement based on closest distance to home.
2. **Final Token Home Entry Bonus:** `04_GAME_RULES.md` states moving a token home grants a bonus roll. It must explicitly clarify: *If the token entering home is the player's 4th and final token, the match is won immediately and no bonus roll occurs.*
3. **Turn Timeout Disconnect Threshold:** `10_MULTIPLAYER_PROTOCOL.md` states two consecutive timeouts switch the player to `AUTO_BOT_ACTIVE`. It does not specify when an inactive player is completely dropped/forfeited from the room.
   *Recommendation:* After 3 consecutive bot turns, the player is formally marked as Forfeited, freeing resources.

### Offline Experience
1. **Default Asset Packaging:** The documentation does not explicitly mandate that all default board textures, piece models, dice models, and audio files must be bundled inside the local APK assets directory rather than streamed on first launch.
   *Recommendation:* Mandate 100% of baseline visual and audio assets are bundled in the APK to guarantee zero network requirement on first launch.
2. **Local Match Save Expiration:** `05_OFFLINE_ARCHITECTURE.md` states paused matches can be resumed. It must specify an expiration window (e.g., incomplete matches older than 7 days auto-archive to match history as Abandoned).

### Ads & Monetization
1. **Ad Load Timeout:** `12_ADS_MONETIZATION.md` states ads fail gracefully, but does not define an explicit load timeout.
   *Recommendation:* Enforce a strict 5-second timeout on ad load requests. If an ad does not load within 5 seconds, the transition immediately proceeds without showing an ad.

---

## 5. Terminology Consistency Review

The following terms represent identical concepts across documents but use varying nomenclature:

| Inconsistent Usages | Recommended Standard Term | Rationale |
| :--- | :--- | :--- |
| `Private Lobby`, `Private Room`, `Game Room`, `Match Room` | **Private Room** | Accurately describes host-created custom lobbies with 6-character room codes. |
| `Quick Match`, `Quick Matchmaking`, `Public Matchmaking` | **Quick Match** | Clean, user-facing label for casual public queue. |
| `Tokens`, `Pawns`, `Game Pieces`, `Pieces` | **Tokens** | Consistent with classical board game and Ludo terminology. |
| `Coins`, `Soft Currency`, `Gold Coins` | **Coins** | In-game earned currency name. |
| `Remix Mode`, `Ludora Remix`, `Game Remix` | **Remix Mode** | Standard game mode identifier alongside Ludo and Snake & Ladder. |

---

## 6. Source-of-Truth Hierarchy Audit

The documents conform to the required hierarchy:
- `01_PRD.md`: Defines overarching product requirements and feature scope.
- `03_GAME_DESIGN.md` & `04_GAME_RULES.md`: Authoritative sources for game loops, mechanics, and exact engine logic.
- `05_OFFLINE_ARCHITECTURE.md` - `07_TECH_ARCHITECTURE.md`: Technical infrastructure and module separation.
- `08_DATA_MODEL.md` - `10_MULTIPLAYER_PROTOCOL.md`: Concrete contracts, schemas, and wire events.
- `11_SECURITY_PLAN.md` & `12_ADS_MONETIZATION.md`: Security controls and monetization constraints.
- `13_UI_UX_SPEC.md` - `16_ACCESSIBILITY.md`: Presentation, design system tokens, animations, and WCAG AA standards.
- `17_TESTING_PLAN.md` & `18_PERFORMANCE_PLAN.md`: Quality gates and resource budgets.
- `19_ROADMAP.md` - `21_DECISIONS.md`: Delivery phasing, development rules, and architecture decision log.

*Hierarchy Note:* Where `01_PRD.md` or `03_GAME_DESIGN.md` held TBD flags that were subsequently resolved in `07_TECH_ARCHITECTURE.md` or `21_DECISIONS.md`, the higher-level documents must be updated to maintain strict alignment.

---

## 7. Security & Architecture Concerns

1. **Local Save State Tampering:** While offline progression is cosmetic-only, players can easily edit local SQLite or SharedPreferences on rooted Android devices.
   *Mitigation:* Use SHA-256 HMAC payload signing on saved game states and coin balances using an app-specific key stored in Android Keystore.
2. **Replay Attacks on SSV Ad Callbacks:** Rewarded ad callbacks must require both a unique transaction UUID and a cryptographic server signature to prevent credential replaying.
3. **Engine UI Decoupling:** In `07_TECH_ARCHITECTURE.md`, ensure the `engine/` modules have zero Android imports (`android.*`). This is vital for fast headless JVM unit testing.

---

## 8. Top 10 Decisions Requiring Resolution Before Implementation

1. **Ludo Consecutive Sixes:** Confirm whether rolling three consecutive 6s cancels only the third roll or voids all three rolls made that turn.
2. **Snake & Ladder 100 Overshoot:** Confirm default behavior when rolling past square 100: does the piece stall in place, or bounce backward?
3. **Safe Square Multi-Occupancy:** Formally confirm that multiple tokens of different colors can occupy star/start squares simultaneously.
4. **Offline Save State Cryptography:** Decide whether local SQLite storage requires SQLCipher full database encryption or if Android Keystore HMAC signature verification is sufficient for MVP.
5. **Realtime Transport Protocol:** Formally confirm WebSockets (WSS) over OkHttp as the standard protocol, deprecating gRPC from online architecture docs.
6. **Account Link Conflict Policy:** Define the exact data reconciliation rule when an offline guest profile links to an existing cloud account that already holds progress.
7. **Draw / Stalemate Rule:** Define maximum round cutoff and draw resolution rules for both games.
8. **MVP Phase Boundary:** Officially ratify that Phases 1 through 6 of the Roadmap represent the MVP release.
9. **Spectator Mode Scope:** Decide whether Spectator Mode is Post-MVP or rejected as a Non-Goal.
10. **Terminology Standardization:** Adopt the standardized glossary (Tokens, Private Room, Coins, Remix Mode) across all 21 documents.

---

## 9. Implementation Readiness Verdict

**Verdict: NOT READY FOR IMPLEMENTATION**

**Reasoning:** While the documentation is structured, comprehensive, and adheres to stop-slop rules, the 5 identified contradictions and the 10 open architectural/rule decisions must be resolved in the documentation first. Starting implementation before resolving engine rules (such as three-sixes penalty and square 100 overshoot) will lead to rework in the core game engine.
