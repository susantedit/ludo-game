# Phase 10: Polish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 10 Polish system as defined in `docs/19_ROADMAP.md`, `docs/15_ANIMATION_SPEC.md`, and `docs/16_ACCESSIBILITY.md`:
1. Haptic & Procedural Audio Engine: Tactile multi-pattern vibration engine and zero-asset procedural audio generator (dice roll, wooden step, capture strike, ladder chime, snake slide, victory fanfare).
2. Accessibility & Color-Blind System: WCAG AA contrast, color-blind mode palettes (Deuteranopia, Protanopia, Tritanopia), geometric shape markers, and full Reduced Motion compliance.
3. Purposeful Motion & Micro-Interactions: Parabolic token hopping arcs, 3D dice rotation with spring bounce, interactive confetti celebration burst, and breathing turn ring indicators.
4. Accessibility Settings UI: Interactive settings sheet allowing players to configure sound, haptics, color-blind modes, and motion preferences.

**Architecture:** Pure Kotlin engines & models in `:core:designsystem` / `:core:common`, with Compose Canvas and interactive views in `:app`.

---

### Task 1: Haptic & Procedural Audio Feedback Engine

**Files:**
- Create: `core/common/src/main/kotlin/game/ludora/core/common/feedback/AudioSoundManager.kt`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/feedback/HapticFeedbackManager.kt`
- Test: `core/common/src/test/kotlin/game/ludora/core/common/feedback/FeedbackEngineTest.kt`

- [x] **Step 1: Define `SoundEffect` and `AudioSoundManager` with procedural waveform synthesis**
- [x] **Step 2: Define `HapticPattern`, `HapticIntensity`, and `HapticFeedbackManager`**
- [x] **Step 3: Unit test audio synthesis parameter generation and haptic dispatch patterns**

---

### Task 2: Accessibility Architecture & Palette Profiles

**Files:**
- Create: `core/model/src/main/kotlin/game/ludora/core/model/AccessibilityConfig.kt`
- Modify: `core/designsystem/src/main/kotlin/game/ludora/core/designsystem/theme/Color.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/settings/AccessibilitySettingsSheet.kt`

- [x] **Step 1: Define `AccessibilityConfig`, `ColorBlindMode`, and `HapticIntensity` in `:core:model`**
- [x] **Step 2: Add color-blind palette transformations in `Color.kt`**
- [x] **Step 3: Implement `AccessibilitySettingsSheet` with live toggles and sliders**

---

### Task 3: Motion & Micro-Interactions

**Files:**
- Create: `app/src/main/kotlin/game/ludora/ui/animation/ConfettiCelebration.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/animation/AnimatedDiceView.kt`
- Modify: `app/src/main/kotlin/game/ludora/ui/common/MatchRewardDialog.kt`
- Modify: `app/src/main/kotlin/game/ludora/MainActivity.kt`

- [x] **Step 1: Implement `ConfettiCelebration` Canvas particle explosion for victory screens**
- [x] **Step 2: Implement `AnimatedDiceView` with 3D rotation, scale bounce, and pip flash**
- [x] **Step 3: Integrate victory confetti into `MatchRewardDialog`**
- [x] **Step 4: Wire accessibility settings, sound, and haptics into `MainActivity`**

---

### Task 4: Documentation and Decisions Record

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- [x] **Step 1: Record Phase 10 decisions and update changelog**
