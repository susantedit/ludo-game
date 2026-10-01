# Realistic 3D Models, Animation Physics, High-Fidelity Audio & Security Audit Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement realistic 3D dice physics and rendering, authentic undulating 3D animated snakes, the Kathmandu cultural board theme, a dual-mode high-fidelity audio engine, a comprehensive Android/Kotlin security audit, and an interactive WebGL/Canvas 3D preview harness verified via Playwright.

**Architecture:** Pure Kotlin 3D mathematical projection and animation models in `:core:designsystem`, `:core:model`, and `:core:common`, paired with an interactive 3D WebGL/Canvas preview harness under `preview/` audited with Playwright.

**Tech Stack:** Kotlin 2.0, Android Jetpack Compose Canvas, WebGL / HTML5 Canvas 3D, JUnit 4, Python 3.14 verification scripts, Playwright browser automation.

**Spec:** `docs/superpowers/specs/2026-10-01-real-3d-visuals-and-security-design.md`

## Global Constraints
- Pure Kotlin JVM modules for non-UI domain engines and models.
- Zero external native binary dependencies for rendering; use mathematical 3D projection, Phong shading, and spline geometry.
- Offline-first: zero blocking network dependencies for board rendering, dice physics, or audio playback.
- WCAG AA contrast compliance across all color-blind palettes.
- All tasks must have automated tests with zero placeholders.

---

### Task 1: Realistic 3D Isometric Tumbling Dice Renderer

**Files:**
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/dice/Realistic3DDiceRenderer.kt`
- Create: `core/designsystem/src/test/kotlin/game/ludora/core/designsystem/dice/Realistic3DDiceRendererTest.kt`

**Interfaces:**
- Produces: `Realistic3DDiceRenderer` with methods:
  - `computeFacetVertices(eulerX: Float, eulerY: Float, eulerZ: Float, size: Float): List<DiceFacet>`
  - `computeLighting(normal: Vector3D, lightDir: Vector3D): Float`
  - `calculatePips(value: Int, facet: DiceFacet): List<PipPoint>`

- [ ] **Step 1: Write unit tests in `Realistic3DDiceRendererTest.kt` for isometric projection, normal vectors, and lighting calculations**
- [ ] **Step 2: Run test to verify it fails**
- [ ] **Step 3: Implement `Realistic3DDiceRenderer` with 3D cube math, beveled corner offsets, recessed gold pip positions, and lighting vectors**
- [ ] **Step 4: Run test to verify it passes**
- [ ] **Step 5: Commit**

---

### Task 2: Realistic Animated 3D Snake Engine & Spine Undulation

**Files:**
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/board/Realistic3DSnakeRenderer.kt`
- Create: `core/designsystem/src/test/kotlin/game/ludora/core/designsystem/board/Realistic3DSnakeRendererTest.kt`

**Interfaces:**
- Produces: `Realistic3DSnakeRenderer` with methods:
  - `generateSpinePoints(start: Point2D, end: Point2D, controlPoints: List<Point2D>, samples: Int): List<Point2D>`
  - `applyUndulation(spine: List<Point2D>, timeSeconds: Float, amplitude: Float, frequency: Float): List<Point2D>`
  - `generateCrossSectionMesh(undulatedSpine: List<Point2D>, baseRadius: Float): List<SnakeSegment>`

- [ ] **Step 1: Write unit tests in `Realistic3DSnakeRendererTest.kt` verifying Bézier spline generation, sine wave undulation, and mesh tapering**
- [ ] **Step 2: Run test to verify it fails**
- [ ] **Step 3: Implement `Realistic3DSnakeRenderer` with spine wave propagation, diamond scale geometry, viper head vertices, and eye blink timers**
- [ ] **Step 4: Run test to verify it passes**
- [ ] **Step 5: Commit**

---

### Task 3: Kathmandu World Board Theme & Custom Rule Config

**Files:**
- Create: `core/model/src/main/kotlin/game/ludora/core/model/CustomRuleConfig.kt`
- Create: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/board/KathmanduBoardRenderer.kt`
- Create: `core/model/src/test/kotlin/game/ludora/core/model/CustomRuleConfigTest.kt`

**Interfaces:**
- Produces: `CustomRuleConfig` with customizable turn timers, double-six rules, card limits, and theme selections.
- Produces: `KathmanduBoardRenderer` with mandala geometry calculation, stupa coordinates, and Swayambhunath center eye medallion.

- [ ] **Step 1: Write unit tests in `CustomRuleConfigTest.kt` for rule validation and default presets**
- [ ] **Step 2: Run test to verify it fails**
- [ ] **Step 3: Implement `CustomRuleConfig` and `KathmanduBoardRenderer` with cultural Newari mandala geometry and brass inlay layout**
- [ ] **Step 4: Run test to verify it passes**
- [ ] **Step 5: Commit**

---

### Task 4: High-Fidelity Audio Engine & Resource Fallback

**Files:**
- Create: `core/common/src/main/kotlin/game/ludora/core/common/feedback/HighFidelitySoundManager.kt`
- Create: `core/common/src/test/kotlin/game/ludora/core/common/feedback/HighFidelitySoundManagerTest.kt`

**Interfaces:**
- Produces: `HighFidelitySoundManager` routing audio events (DICE_RATTLE, TOKEN_TAP, LADDER_CHIME, SNAKE_HISS, VICTORY_HORN) to acoustic resource players or falling back to `AudioSoundManager`.

- [ ] **Step 1: Write unit tests in `HighFidelitySoundManagerTest.kt` for sound dispatch, volume attenuation, and fallback selection**
- [ ] **Step 2: Run test to verify it fails**
- [ ] **Step 3: Implement `HighFidelitySoundManager`**
- [ ] **Step 4: Run test to verify it passes**
- [ ] **Step 5: Commit**

---

### Task 5: Deep Android & Kotlin Security Audit & Verification Report

**Files:**
- Create: `docs/24_SECURITY_AUDIT_REPORT.md`
- Create: `core/common/src/test/kotlin/game/ludora/core/common/security/CodebaseSecurityAuditTest.kt`

- [ ] **Step 1: Perform static security audit across all 12 modules for CWE-89, CWE-926, CWE-330, CWE-312, and CWE-400**
- [ ] **Step 2: Document findings, threat mitigations, and compliance verification in `docs/24_SECURITY_AUDIT_REPORT.md`**
- [ ] **Step 3: Implement automated test `CodebaseSecurityAuditTest.kt` verifying absence of raw SQL interpolation, unexported intent leakage, and non-CSPRNG random instances**
- [ ] **Step 4: Verify all security tests pass**
- [ ] **Step 5: Commit**

---

### Task 6: Interactive 3D Web Preview Harness & Playwright Visual Verification

**Files:**
- Create: `preview/index.html`
- Create: `preview/styles.css`
- Create: `preview/app.js`
- Create: `scripts/verify_visual_3d.py`

- [ ] **Step 1: Build the interactive HTML5 / WebGL 3D preview harness containing the 3D tumbling dice, slithering snake, and Kathmandu board**
- [ ] **Step 2: Implement interactive controls to roll dice, toggle snake swallowing, and change lighting**
- [ ] **Step 3: Run automated headless browser verification using `playwright-cli` / browser runner to capture screenshots and audit layout aesthetics**
- [ ] **Step 4: Verify zero AI-slop design and confirm 60fps animation smoothness**
- [ ] **Step 5: Commit and push all deliverables to `origin/main`**
