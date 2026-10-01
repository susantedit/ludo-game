# Phase 1: Foundation and Scaffolding Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Establish the native Android multi-module project baseline for Ludora, including Gradle version catalog, root and module build scripts, core domain models, engine interfaces, and base configuration.

**Architecture:** Modern Android clean multi-module architecture with Gradle Kotlin DSL (`build.gradle.kts`) and Version Catalog (`gradle/libs.versions.toml`). Domain models and game engine modules are pure Kotlin without Android dependencies for deterministic execution and headless JVM testing.

**Tech Stack:** Kotlin 2.0, Android Gradle Plugin 8.4+, Jetpack Compose, Gradle Version Catalog, Kotlinx Coroutines, Kotlinx Serialization.

**Spec:** [docs/07_TECH_ARCHITECTURE.md](file:///d:/ludo/docs/07_TECH_ARCHITECTURE.md) and [docs/08_DATA_MODEL.md](file:///d:/ludo/docs/08_DATA_MODEL.md)

## Global Constraints
- Target Android SDK: API 34+; Minimum SDK: API 26 (Android 8.0).
- Pure Kotlin for `engine/` modules (zero `android.*` or `androidx.compose.*` imports).
- Standardized terminology: `Tokens`, `Private Room`, `Quick Match`, `Coins`, `Remix Mode`.
- No hardcoded secrets, no fake functionality, no placeholder components.
- No em dashes in generated documentation and comments.

---

### Task 1: Gradle Build Baseline & Version Catalog

**Files:**
- Create: `gradle/libs.versions.toml`
- Create: `build.gradle.kts`
- Create: `settings.gradle.kts`
- Create: `gradle.properties`

**Interfaces:**
- Produces: Centralized dependency catalog definitions (`libs.versions.toml`) and project inclusion configuration for `app`, `core:*`, `engine:*`, and `feature:*`.

- [x] **Step 1: Create `gradle/libs.versions.toml`**
  Define version numbers, plugins (Android Application, Android Library, Kotlin JVM, Kotlin Android, Compose Compiler, Kotlinx Serialization), and core libraries (Coroutines, Compose BOM, Material 3, Room, DataStore).

- [x] **Step 2: Create root `settings.gradle.kts`**
  Configure pluginManagement, dependencyResolutionManagement with mavenCentral() and google(), rootProject.name = "ludora", and include submodules:
  - `:app`
  - `:core:common`, `:core:model`, `:core:designsystem`, `:core:database`, `:core:datastore`, `:core:network`
  - `:engine:core`, `:engine:ludo`, `:engine:snake`, `:engine:ai`, `:engine:remix`
  - `:feature:home`, `:feature:ludo`, `:feature:snake`, `:feature:remix`, `:feature:profile`, `:feature:rooms`, `:feature:settings`

- [x] **Step 3: Create root `build.gradle.kts` and `gradle.properties`**
  Configure root buildscript with alias plugins apply false, JVM target configuration, and Android build properties (`android.useAndroidX=true`, `kotlin.code.style=official`).

- [x] **Step 4: Verify syntax and file existence**
  Ensure all Gradle configuration files are syntactically valid and have matching module paths.

---

### Task 2: Pure Kotlin Engine Core & Domain Models (`engine:core`, `core:model`)

**Files:**
- Create: `engine/core/build.gradle.kts`
- Create: `core/model/build.gradle.kts`
- Create: `core/model/src/main/kotlin/game/ludora/core/model/PlayerColor.kt`
- Create: `core/model/src/main/kotlin/game/ludora/core/model/TokenState.kt`
- Create: `core/model/src/main/kotlin/game/ludora/core/model/GameType.kt`
- Create: `core/model/src/main/kotlin/game/ludora/core/model/Player.kt`
- Create: `core/model/src/main/kotlin/game/ludora/core/model/LocalProfile.kt`
- Create: `engine/core/src/main/kotlin/game/ludora/engine/core/EngineContract.kt`
- Create: `engine/core/src/main/kotlin/game/ludora/engine/core/DiceRoller.kt`
- Create: `engine/core/src/test/kotlin/game/ludora/engine/core/DiceRollerTest.kt`

**Interfaces:**
- Produces: `PlayerColor`, `TokenState`, `GameType`, `PlayerSeat`, `EngineAction`, `EngineResult`, `GameEvent`, and deterministic `DiceRoller` interface with default CSPRNG implementation.

- [x] **Step 1: Create module build scripts**
  Configure `engine/core/build.gradle.kts` and `core/model/build.gradle.kts` as pure Kotlin JVM library modules.

- [x] **Step 2: Implement domain models in `core:model`**
  Implement immutable data classes: `PlayerColor` (RED, GREEN, YELLOW, BLUE with associated starting/star indices), `TokenState` (IN_BASE, ON_TRACK, IN_HOME_PATH, FINISHED), `GameType` (LUDO, SNAKE_AND_LADDER, REMIX), and `LocalProfile`.

- [x] **Step 3: Implement EngineContract & GameEvent in `engine:core`**
  Define `EngineAction`, `EngineResult`, `GameEvent`, and `GameEngine<State, Action>` interfaces.

- [x] **Step 4: Implement deterministic `DiceRoller` and test**
  Implement `DiceRoller` interface and `SecureDiceRoller` using `java.security.SecureRandom`. Write `DiceRollerTest` asserting values strictly fall in range [1, 6] across 10,000 iterations.

---

### Task 3: Core Common & Database Models (`core:common`, `core:database`)

**Files:**
- Create: `core/common/build.gradle.kts`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/Result.kt`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/Dispatchers.kt`
- Create: `core/database/build.gradle.kts`
- Create: `core/database/src/main/kotlin/game/ludora/core/database/entity/LocalProfileEntity.kt`
- Create: `core/database/src/main/kotlin/game/ludora/core/database/entity/MatchHistoryEntity.kt`
- Create: `core/database/src/main/kotlin/game/ludora/core/database/dao/LocalProfileDao.kt`
- Create: `core/database/src/main/kotlin/game/ludora/core/database/dao/MatchHistoryDao.kt`
- Create: `core/database/src/main/kotlin/game/ludora/core/database/LudoraDatabase.kt`

**Interfaces:**
- Produces: `Result<T>`, Room database entities, DAOs, and database class definition conforming to [docs/08_DATA_MODEL.md](file:///d:/ludo/docs/08_DATA_MODEL.md).

- [x] **Step 1: Create `core:common` utilities**
  Implement `Result<T>` sealed interface (`Success`, `Error`, `Loading`) and coroutine dispatchers wrapper.

- [x] **Step 2: Create `core:database` Room definitions**
  Define `LocalProfileEntity`, `MatchHistoryEntity`, corresponding DAOs (`LocalProfileDao`, `MatchHistoryDao`), and `LudoraDatabase` abstract class.

---

### Task 4: Application Shell & Android Manifest (`app`)

**Files:**
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/kotlin/game/ludora/LudoraApplication.kt`
- Create: `app/src/main/kotlin/game/ludora/MainActivity.kt`

**Interfaces:**
- Produces: Main Android application entry point, launcher Activity, permissions (strictly non-intrusive), and basic theme setup.

- [x] **Step 1: Create `app/build.gradle.kts`**
  Configure Android Application plugin, compileSdk 34, minSdk 26, applicationId "game.ludora", version code 1, version name "0.1.0", and module dependencies.

- [x] **Step 2: Configure `AndroidManifest.xml`**
  Define `LudoraApplication`, `MainActivity` with portrait orientation and launcher intent filter. Zero intrusive permissions (network state only).

- [x] **Step 3: Create `LudoraApplication.kt` and `MainActivity.kt`**
  Implement base Application class and single Activity with `enableEdgeToEdge()` and basic Compose content host.

---

### Task 5: Verification & Status Update

**Files:**
- Modify: `docs/21_DECISIONS.md` (Log Phase 1 scaffolding completion)
- Modify: `CHANGELOG.md` (Add Phase 1 scaffolding entry)

- [x] **Step 1: Verify all module directories and files**
  Audit directory tree against [docs/07_TECH_ARCHITECTURE.md](file:///d:/ludo/docs/07_TECH_ARCHITECTURE.md).

- [x] **Step 2: Update project status and changelog**
  Record Phase 1 foundation artifacts in `CHANGELOG.md`.
