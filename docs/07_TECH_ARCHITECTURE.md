# Technical Architecture

## Technology Stack Overview
Ludora targets Android as its primary platform using modern native technologies: Kotlin, Jetpack Compose, Kotlin Coroutines, and Android Architecture Components. The codebase follows clean architecture principles with unidirectional data flow (UDF).

## Android and Kotlin Baseline
- Language: Kotlin 1.9+ / 2.0+ with strict type checking and coroutine support.
- Minimum SDK: API 26 (Android 8.0 Oreo) for broad hardware compatibility.
- Target SDK: API 34+ (Android 14+).
- Concurrency: Kotlin Coroutines with Flow / StateFlow for reactive, non-blocking asynchronous state handling.

## Jetpack Compose UI Architecture
- Declarative UI: Single-Activity architecture (`MainActivity`) with 100% Jetpack Compose for screens and components.
- State Flow: ViewModels expose immutable `StateFlow<UiState>` to composables. Composables emit UI events back to ViewModels.
- Design System: Custom Material 3 theme incorporating Ludora color tokens, typography scales, and shape components.
- Graphics and Rendering: Compose Canvas and custom DrawModifiers handle board drawing, path rendering, and piece coordinates. Avoids third-party heavy game engines for the initial release.

## Project Module Structure
The codebase uses a multi-module architecture to isolate business logic from presentation and transport layers:
```text
ludora/
├── app/                  # Application entry point, dependency injection graphs, navigation root
├── core/
│   ├── common/           # Shared utilities, dispatchers, extensions, result wrappers
│   ├── designsystem/     # Compose theme, colors, typography, icons, reusable components
│   ├── model/            # Domain models, game types, player entities
│   ├── database/         # Room database, DAOs, local entities, migrations
│   ├── datastore/        # Preferences DataStore for settings and flags
│   └── network/          # HTTP client, WebSocket manager, network interceptors
├── feature/
│   ├── home/             # Home dashboard, game mode selection
│   ├── ludo/             # Ludo game screen, board composable, interaction handlers
│   ├── snake/            # Snake & Ladder game screen and board composables
│   ├── remix/            # Remix mode rules, hazard board overlays
│   ├── profile/          # Player stats, cosmetic inventory, achievements
│   ├── rooms/            # Private room lobby, room code entry, matchmaking queue
│   └── settings/         # Audio, graphics, accessibility configurations
└── engine/
    ├── core/             # Pure Kotlin game engine interfaces, turn coordinator, dice roll models
    ├── ludo/             # Deterministic Ludo rule engine and move validator
    ├── snake/            # Deterministic Snake & Ladder rule engine
    └── ai/               # Local heuristic AI decision engine
```

## Game Engine Architecture
- Pure Kotlin engine without Android framework dependencies, enabling pure JVM unit testing.
- Input/Output Contract:
  - Input: `EngineAction` (e.g. `RollDiceAction`, `MoveTokenAction`).
  - Output: `EngineResult` containing updated `GameState`, emitted `GameEvent` list (e.g. `CapturedEvent`, `BonusTurnEvent`), and valid next actions.
- Determinism: Engine state transitions execute predictably. Given the same state and action, the resulting state is identical.

## Client and Server Boundaries
- Offline Mode: The local game engine acts as the authoritative coordinator.
- Online Mode: The remote server runs the authoritative engine. The client engine serves as a local prediction and interpolation layer.
- Contracts: Client and server share domain models and command structures to ensure strict parity.

## Local Persistence Strategy
- Android Room (SQLite): Persists local profile data, match history records, and achievement progress.
- Jetpack DataStore: Stores user settings, audio preferences, and theme selections using type-safe preference keys.
- Cache Strategy: Remote profile and cosmetics cache locally to enable instant offline boot.

## Networking Layer
- HTTP Client: OkHttp and Retrofit for RESTful API communication (authentication, room metadata, profiles).
- Realtime Streaming: WebSocket client built on OkHttp WebSocket with exponential backoff reconnection.
- Serialization: Kotlinx Serialization for fast, reflection-free JSON and binary decoding.

## Dependency Strategy
- Zero Unnecessary Dependencies: Avoid heavy monolithic libraries. Every dependency must have a direct operational requirement.
- Core libraries: AndroidX Core, Lifecycle, Compose BOM, Navigation Compose, Room, DataStore, OkHttp, Retrofit, Kotlinx Coroutines, Kotlinx Serialization.
- Ad SDKs: Encapsulated behind an interface (`AdProvider`) to prevent SDK coupling to feature code.

## Environment Configuration
- Build Flavors:
  - `dev`: Points to local or staging backend; verbose logging enabled; test ads enabled.
  - `prod`: Points to production backend; ProGuard/R8 optimizations enabled; production ad placements.
- Secrets Management: API keys and ad unit IDs inject via `local.properties` and BuildConfig fields at build time. Never commit secrets to source control.

## Build Configuration
- Gradle Version Catalogs (`libs.versions.toml`) to standardize dependencies and plugin versions across all submodules.
- Kotlin DSL (`build.gradle.kts`) across all modules.
- ProGuard / R8 rules configured to strip unused code, optimize Compose bytecode, and obfuscate release builds.
