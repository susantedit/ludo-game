# Changelog

## Unreleased

### Added
- Initial Ludora documentation structure
- Product planning documents
- Architecture planning documents
- Security planning document
- UI/UX planning document
- Testing and performance planning documents
- Phase 1 Foundation: Root Gradle configuration, settings, version catalog (`libs.versions.toml`)
- Phase 1 Foundation: Pure Kotlin domain models (`PlayerColor`, `TokenState`, `GameType`, `Player`, `LocalProfile`)
- Phase 1 Foundation: Game engine contracts (`EngineAction`, `EngineResult`, `GameEvent`, `GameEngine`)
- Phase 1 Foundation: CSPRNG `DiceRoller` implementation and unit tests
- Phase 1 Foundation: Core common utilities (`Result`, `AppDispatchers`)
- Phase 1 Foundation: Room database entities (`LocalProfileEntity`, `MatchHistoryEntity`), DAOs, and `LudoraDatabase`
- Phase 1 Foundation: Application entry point (`LudoraApplication`, `MainActivity`, `AndroidManifest.xml`, resources)
- Phase 3 Ludo Engine: Pure Kotlin JVM module `engine:ludo`
- Phase 3 Ludo Engine: `LudoBoard` track navigation, 52-step perimeter coordinates, safe squares, home path transitions
- Phase 3 Ludo Engine: `LudoPosition`, `LudoToken`, `LudoPlayerState`, and `LudoGameState` immutable data models
- Phase 3 Ludo Engine: `LudoMoveValidator` evaluating legal moves, friendly non-safe collisions, and opponent captures
- Phase 3 Ludo Engine: `LudoStateReducer` pure state reducer with bonus rolls, 3-consecutive-sixes penalty, and 200-round cutoff
- Phase 3 Ludo Engine: `LudoGameEngine` conforming to `GameEngine<LudoGameState, EngineAction>` with unit and 100-match headless simulation tests
- Phase 2 Design System: Android Compose library module `:core:designsystem`
- Phase 2 Design System: Deep Obsidian Dark Mode tokens (`LudoraColors`, `LudoraTypography`, `LudoraShapes`, `LudoraTheme`)
- Phase 2 Design System: WCAG AA contrast ratio compliance tests
- Phase 2 Design System: Core UI components (`LudoraButton`, `LudoraCard`, `PlayerAvatarBadge`, `TurnStatusPill`)
- Phase 2 Design System: Color-blind accessible geometric shape symbols (Circle, Triangle, Diamond, Square)
- Phase 2 Design System: Canvas mathematical mappers (`LudoGridCoordinateMapper`, `SnakeGridCoordinateMapper`)
- Phase 2 Design System: 2.5D tactile `TokenRenderer` and cubic `DiceRenderer` with recessed pip dots
- Page-Mascot Integration: 56 interactive page-mascots bundled locally with gaze-tracking physics and MascotSelectorSheet
- Phase 4 Snake & Ladder Engine: Pure Kotlin JVM module `engine:snake` with 100-tile boustrophedon grid, 7 ladders, 8 snakes, exact finish rule, and 100-match headless simulation
- Phase 5 Remix Mode: Pure Kotlin JVM module `engine:remix` with hybrid track hazards, 5 tactical power cards (`SHIELD`, `SPEED_BOOST`, `REROLL`, `SWAP`, `BOMB`), and round Chaos Modifiers
- Interactive Gameplay UI: Interactive Compose Canvas screens for Ludo, Snake & Ladder, and Remix Mode connected to MainActivity dashboard
- Phase 6 Progression: Offline XP engine with level scaling, match outcome rewards, cosmetic store, and daily quests
- Phase 6 Progression: `ProfileProgressionSheet` and `MatchRewardDialog` integrated with level-up celebrations and offline coin/XP claiming
- Phase 7 Online Infrastructure: Pure Kotlin networking library `:core:network` with serialization and coroutine support
- Phase 7 Online Infrastructure: High-entropy 6-character Crockford base-32 `RoomCodeGenerator` (32^6 permutations, zero character ambiguity)
- Phase 7 Online Infrastructure: Guest authentication models, token sessions, and account linking contracts
- Phase 7 Online Infrastructure: Realtime WebSocket protocol packets (`ClientPacket`, `ServerPacket`), `RealtimeGateway`, and `InMemoryRealtimeGateway` test harness
- Phase 8 Online Multiplayer: `MatchmakingTicket` and `Matchmaker` queuing engine with MMR-based pairing and 45-second timeout AI autofill
- Phase 8 Online Multiplayer: Server-authoritative `OnlineMatchCoordinator` with 15s turn countdown timer, timeout forfeiture, 60s reconnection grace period, and snapshot recovery
- Phase 8 Online Multiplayer: Interactive Compose UI components (`OnlineLobbyScreen`, `JoinRoomDialog`, `QuickMatchSearchingOverlay`) and MainActivity hub navigation
