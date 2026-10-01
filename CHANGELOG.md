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
