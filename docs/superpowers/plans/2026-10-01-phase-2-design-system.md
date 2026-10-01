# Phase 2: Design System & Core UI Primitives Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the complete Android Compose design system in `:core:designsystem` conforming to `docs/14_DESIGN_SYSTEM.md`, including deep obsidian dark theme tokens, typography, shapes, core UI components, player color-blind accessible badges, and Canvas board coordinate mappers.

**Architecture:** Android library module `:core:designsystem` using Jetpack Compose and Material 3. Custom design tokens (`LudoraColors`, `LudoraTypography`, `LudoraShapes`, `LudoraDimensions`) injected via `CompositionLocalProvider`. Canvas rendering primitives map mathematical grid coordinates to pixel offsets without coupling to Android Activity or ViewModel layers.

**Tech Stack:** Kotlin 1.9.22, Android Compose BOM, Compose Material 3, Compose UI, Compose Foundation, JUnit 4.

**Spec:** [docs/14_DESIGN_SYSTEM.md](file:///d:/ludo/docs/14_DESIGN_SYSTEM.md) and [docs/13_UI_UX_SPEC.md](file:///d:/ludo/docs/13_UI_UX_SPEC.md).

## Global Constraints

- Android library module `:core:designsystem` with Compose enabled.
- Default theme: Deep Obsidian Dark Mode (`#0B0F19` background, `#0F172A` surface, `#1E293B` elevated cards).
- Player palette: Red (`#E53935`), Green (`#00C853`), Yellow (`#FFD600`), Blue (`#2979FF`).
- Accessibility: Color-blind geometric symbols on tokens (Red: Circle, Green: Triangle, Yellow: Diamond, Blue: Square).
- Minimum touch targets: 48x48dp for all interactive elements.
- Clean separation: Canvas coordinate math is decoupled from presentation rendering logic.

---

### Task 1: Module Build Configuration and Theme Tokens

**Files:**
- Create: `core/designsystem/build.gradle.kts`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/theme/Color.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/theme/Typography.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/theme/Shape.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/theme/Theme.kt`
- Test: `core/designsystem/src/test/kotlin/game/ludora/core/designsystem/theme/ColorContrastTest.kt`

**Interfaces:**
- Consumes: `PlayerColor` from `:core:model`
- Produces: `LudoraColors`, `LudoraTheme`, `LocalLudoraColors`, player color mapping `PlayerColor.toColor(): Color`

- [x] **Step 1: Create `core/designsystem/build.gradle.kts`**
  Configure Android Library with Compose compiler and Material 3 dependencies.

- [x] **Step 2: Write tests in `ColorContrastTest.kt`**
  Assert that text colors against surface/background satisfy WCAG 2.1 AA ratios (>= 4.5:1 for body, >= 3.0:1 for large titles) and player tokens have sufficient contrast against dark slate surfaces.

- [x] **Step 3: Implement `Color.kt`, `Typography.kt`, `Shape.kt`, and `Theme.kt`**
  Implement colors, typography, shapes, and `LudoraTheme` provider.

- [x] **Step 4: Run unit tests**
  Execute tests to verify contrast calculations.

---

### Task 2: Core Components (Buttons, Cards, Badges, Turn Pills)

**Files:**
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/component/LudoraButton.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/component/LudoraCard.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/component/PlayerAvatarBadge.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/component/TurnStatusPill.kt`
- Test: `core/designsystem/src/test/kotlin/game/ludora/core/designsystem/component/PlayerSymbolTest.kt`

**Interfaces:**
- Consumes: `PlayerColor`, `LudoraTheme`
- Produces: `LudoraPrimaryButton`, `LudoraOutlinedButton`, `LudoraIconButton`, `LudoraCard`, `PlayerAvatarBadge`, `TurnStatusPill`

- [x] **Step 1: Write unit tests in `PlayerSymbolTest.kt`**
  Verify each `PlayerColor` maps to its defined accessible geometric symbol (Red -> Circle, Green -> Triangle, Yellow -> Diamond, Blue -> Square).

- [x] **Step 2: Implement `LudoraButton.kt` and `LudoraCard.kt`**
  Implement primary, secondary, and icon buttons with 48dp touch targets, and rounded elevated cards.

- [x] **Step 3: Implement `PlayerAvatarBadge.kt` and `TurnStatusPill.kt`**
  Implement player badge with color ring, initials, and geometric symbol, plus turn status chip.

- [x] **Step 4: Verify test suite**
  Execute tests to verify symbol mapping and component logic.

---

### Task 3: Board Canvas Coordinate Mappers and Drawing Primitives

**Files:**
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/canvas/LudoGridCoordinateMapper.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/canvas/SnakeGridCoordinateMapper.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/canvas/TokenRenderer.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/canvas/DiceRenderer.kt`
- Test: `core/designsystem/src/test/kotlin/game/ludora/core/designsystem/canvas/GridCoordinateMapperTest.kt`

**Interfaces:**
- Consumes: `PlayerColor`, `LudoBoard`, `SnakeBoard`
- Produces: `LudoGridCoordinateMapper.getCellOffset(...)`, `SnakeGridCoordinateMapper.getSquareOffset(...)`, `DrawScope.drawLudoraToken(...)`, `DrawScope.drawDiceFace(...)`

- [x] **Step 1: Write tests in `GridCoordinateMapperTest.kt`**
  Verify Ludo 15x15 grid cell calculation (start tiles, stars, bases, home paths) and Snake 10x10 boustrophedon cell centers (e.g. square 1 bottom-left, square 100 top-left).

- [x] **Step 2: Implement `LudoGridCoordinateMapper.kt` and `SnakeGridCoordinateMapper.kt`**
  Implement pure mathematical mappers converting board indices to Canvas `Offset` for any given board size.

- [x] **Step 3: Implement `TokenRenderer.kt` and `DiceRenderer.kt`**
  Implement 2.5D Canvas token drawing with rim, symbol, and shadow, and cubic dice face with 1..6 pip dot coordinates.

- [x] **Step 4: Run unit tests**
  Execute coordinate tests to verify alignment and boundary safety.

---

### Task 4: Documentation, Decisions, and Sync

**Files:**
- Modify: `docs/21_DECISIONS.md` (Log Phase 2 design system decisions)
- Modify: `CHANGELOG.md` (Add Phase 2 design system entry)
- Modify: `docs/superpowers/plans/2026-10-01-phase-2-design-system.md` (Mark tasks complete)

- [x] **Step 1: Audit all module files and test coverage**
  Ensure code matches specifications in `docs/14_DESIGN_SYSTEM.md`.

- [x] **Step 2: Update changelog and architectural decisions**
  Document the completion of Phase 2 Compose Design System & Canvas primitives.

