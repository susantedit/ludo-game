# Architecture and Design Decision Log

## Decision Record Format
Every significant architectural, rule, or technical decision must be logged using this structure:

```text
## Decision: [Short Title]

Date: [YYYY-MM-DD]
Decision: [What was decided]
Reason: [Why this decision was made]
Alternatives: [What alternatives were evaluated and why they were rejected]
Status: [Proposed | Accepted | Superseded | Deprecated]
```

## Active Decisions

## Decision: Native Android Stack with Kotlin and Jetpack Compose

Date: 2026-10-01
Decision: Build the mobile client using native Kotlin, Jetpack Compose, and Android Architecture Components without a heavy third-party 2D/3D game engine.
Reason: Classic board games require crisp 2D/2.5D rendering, high text legibility, small binary size, and fast startup times. Jetpack Compose provides reactive UI rendering, low battery consumption, and tight integration with native Android accessibility and storage services.
Alternatives:
- Unity: High binary overhead (>50MB APK), slow cold startup, higher battery consumption, and poor native accessibility integration.
- Flutter: Good cross-platform capability, but adds a separate rendering pipeline and engine overhead when Android is the initial primary target.
Status: Accepted

## Decision: Offline-First Architecture with Separate Engine Core

Date: 2026-10-01
Decision: Isolate core game rules and state evaluation into a pure Kotlin engine with zero platform or network dependencies.
Reason: Ensures 100% of game mechanics can execute locally without internet connectivity or mock servers, while enabling headless unit testing on the JVM.
Alternatives:
- Server-dependent rules: Forces continuous connectivity and degrades offline playability.
- Coupling rules to Android UI: Prevents running fast headless unit tests without an emulator.
Status: Accepted

## Decision: Online-Only Advertising Placed Outside Core Game Loops

Date: 2026-10-01
Decision: Completely disable ad SDKs and placements during offline gameplay, and restrict online ads to non-interruptive natural boundaries (home dashboard banner and post-game interstitial).
Reason: Respects user playtime, preserves offline autonomy, and prevents mid-turn gameplay friction.
Alternatives:
- Mid-game interstitial ads: Causes player frustration and abandons the offline-first product vision.
- Cached offline ads: Requires complex local storage tracking, increases APK size, and conflicts with ad network policies.
Status: Accepted

## Decision: Ludo Three-Sixes Penalty Forfeits Third Roll Only

Date: 2026-10-01
Decision: When a player rolls three consecutive 6s in Ludo, only the third roll is forfeited. The player cannot move on the third roll, but all valid moves made on Roll 1 and Roll 2 remain intact. The turn terminates immediately.
Reason: Voids-all-three rules cause extreme player frustration and prolong matches unnaturally. Forfeiting the 3rd roll maintains competitive pacing while penalizing excessive luck.
Alternatives:
- Voids all three moves: Punishes the player excessively and causes game engine rollback complexity.
- No penalty: Allows runaway turns that unbalance casual play.
Status: Accepted

## Decision: Safe Square Multi-Occupancy and No Regular Stacking in MVP

Date: 2026-10-01
Decision: 
1. The 8 safe squares (4 start squares, 4 star squares) allow unlimited friendly and opponent tokens to co-exist simultaneously with zero captures.
2. On regular (non-safe) track squares, tokens cannot form impassable blocks or barriers. Friendly tokens cannot land on each other on regular squares in MVP (landing on friendly token is an illegal move).
Reason: Eliminates physical deadlock scenarios where two pieces form an impassable barrier on a 1-lane track, ensuring deterministic forward movement.
Alternatives:
- Impassable blocks: Leads to board stalemates and requires complex jump-blocking engine algorithms.
- Stacking on regular squares: Causes visual ambiguity on mobile touchscreens without zoom controls.
Status: Accepted

## Decision: Snake & Ladder Square 100 Overshoot Stalls

Date: 2026-10-01
Decision: In Snake & Ladder, reaching square 100 requires an exact roll. If a player rolls a value exceeding the distance to square 100, the token cannot move and remains at its current position.
Reason: Standard modern digital board game convention that is immediately intuitive to casual players.
Alternatives:
- Bounce-back rule: Causes confusion and extends match duration unpredictably. (Deferred as an optional Remix house rule).
Status: Accepted

## Decision: Bonus Roll on Token Finish Excluding Game-Winning Token

Date: 2026-10-01
Decision: Moving a token into the Center Goal (`FINISHED`) grants a bonus roll, unless that token is the 4th and final token that triggers the player's 1st place victory.
Reason: A victory terminates the active turn and initiates match conclusion routines; granting a bonus roll after winning is invalid and causes state machine errors.
Alternatives:
- No bonus roll on finish: Disincentivizes finishing over hunting opponent tokens.
Status: Accepted

## Decision: 200-Round Ceiling for Deadlock and Anti-Griefing Defense

Date: 2026-10-01
Decision: A hard limit of 200 rounds is enforced on all matches. If round 200 finishes without a complete winner, the match ends and ranks remaining players by total distance progressed.
Reason: Prevents runaway memory consumption, rogue AI loops, or griefing players who intentionally stall online rooms.
Alternatives:
- Infinite play: Vulnerable to griefing and device battery drain.
Status: Accepted

## Decision: Phase 1 Foundation Scaffolding and Multi-Module Structure

Date: 2026-10-01
Decision: Scaffold the Android project using Gradle Version Catalog (`gradle/libs.versions.toml`), pure Kotlin JVM library modules for `engine:core` and `core:model`, Android library for `core:database`, and Android application shell for `app`.
Reason: Decouples domain models and game engine state machines from Android UI packages, enabling instantaneous deterministic unit testing while adhering to modern Android standards.
Alternatives:
- Monolithic single-module app: Leads to circular dependencies and couples game engine code to Android SDK.
Status: Accepted

## Decision: Phase 3 Deterministic Headless Ludo Engine Architecture

Date: 2026-10-01
Decision: Implement `engine:ludo` as a pure Kotlin state reducer (`LudoStateReducer`) coupled with coordinate calculations (`LudoBoard`) and discrete event generation (`LudoEvent`), conforming to `GameEngine<LudoGameState, EngineAction>`.
Reason: Enforces absolute determinism, guarantees UI/presentation independence, eliminates runtime dependencies on the Android framework, and enables automated testing and simulation of thousands of matches in milliseconds.
Alternatives:
- State coupled to Android ViewModels: Breaks offline/online engine code sharing and slows down headless test suites.
Status: Accepted

## Decision: Phase 2 Compose Design System & Canvas Architecture

Date: 2026-10-01
Decision: Isolate design tokens (`LudoraColors`, `LudoraTypography`, `LudoraShapes`) in `:core:designsystem` using `CompositionLocalProvider`, and decouple board drawing using pure mathematical grid coordinate mappers (`LudoGridCoordinateMapper`, `SnakeGridCoordinateMapper`) directly onto Canvas. Enforce color-blind accessibility by pairing each player color with an intrinsic geometric shape symbol.
Reason: Decouples rendering math from Activity and ViewModel state, ensures WCAG AA contrast compliance, and provides 60fps hardware-accelerated Canvas graphics with zero UI frame drops.
Alternatives:
- Rendering tokens with individual Android Views: High layout overhead and sluggish frame rates on budget mobile hardware.
Status: Accepted

## Standardized Game Terminology

The following standard terms must be used consistently across all documents and code:
- **Tokens**: The 4 playable physical markers per player (avoid "Pawns", "Game Pieces").
- **Private Room**: Host-created multiplayer lobby with a 6-character room code (avoid "Private Lobby", "Match Room").
- **Quick Match**: Casual public matchmaking queue.
- **Coins**: Primary in-game soft currency (avoid "Gold", "Points").
- **Remix Mode**: The hybrid variant game mode (avoid "Game Remix", "Chaos Mode" as mode title).
