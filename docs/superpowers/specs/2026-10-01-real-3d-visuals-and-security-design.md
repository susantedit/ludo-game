# Realistic 3D Models, Animation Physics, High-Fidelity Audio & Security Audit Specification

## 1. Executive Summary

This specification upgrades Ludora's visual fidelity, audio realism, and security posture to match the product vision outlined in `idea.md` and `2.md`. It introduces real 3D model rendering for dice and snakes, authenticates the board with the Kathmandu cultural world theme, establishes a high-fidelity audio pipeline, conducts a comprehensive Android/Kotlin security audit, and provides a WebGL/Canvas 3D preview harness verifiable via `playwright-cli`.

---

## 2. Realistic 3D Model Geometry & Animation Architecture

### 2.1 Real 3D Tumbling Dice Engine
- **Geometry & Mesh**:
  - True 6-sided cubic geometry with beveled edge bevels ($r = 0.08$ relative to unit cube width).
  - Pips modeled as spherical concave recesses with gold specular metallic reflections.
  - Ivory resin base material featuring subtle subsurface scattering and ambient occlusion contact shadows.
- **Physics & Motion**:
  - Quaternion-based 3D tumbling dynamics: $\mathbf{q}(t + \Delta t) = \mathbf{q}(t) \cdot \exp\left(\frac{1}{2} \boldsymbol{\omega}(t) \Delta t\right)$.
  - Rotational friction and restitution bouncing ($e = 0.62$).
  - Dynamic cast drop shadow expanding, softening, and shifting based on the virtual light source $\vec{L} = (-0.4, -0.6, 0.7)$.

### 2.2 Realistic 3D Animated Snake Engine
- **Parametric Tubular Spine**:
  - Evaluates an $N$-segment cubic Bézier spline stretching from head to tail.
  - Generates cross-sectional circular rings of $M = 16$ vertices perpendicular to the spine tangent vector $\vec{T}(s)$.
  - Sinusoidal wave undulation propagated along the spine:
    $$d_\perp(s, t) = A(s) \cdot \sin\left(\frac{2\pi s}{\lambda} - \omega t\right)$$
- **Scale Texturing & Viper Head**:
  - Procedural diamond scales with iridescent emerald and gold normal-mapped highlights.
  - Distinct 3D viper head featuring predatory amber elliptical eyes with blink timers.
  - Articulated forked red tongue flicking when opponent tokens enter danger proximity.
  - Peristaltic token ingestion and descent animation when a player lands on a snake head.

### 2.3 Kathmandu Cultural World Board
- **Materials & Accents**:
  - Hand-carved dark walnut wood border with brass corner brackets.
  - Stone board tiles etched with traditional Newari and Tibetan mandala geometry.
  - Corner prayer flag tassels in the five sacred colors (Blue, White, Red, Green, Yellow).
  - Central golden medallion featuring the Swayambhunath wisdom eyes with ambient candlelight reflections.

---

## 3. High-Fidelity Audio Architecture

- **Dual-Mode Sound Engine (`HighFidelitySoundManager`)**:
  - Primary: High-resolution acoustic audio samples loaded from `res/raw/` (authentic leather cup dice rattle, dense rosewood token taps, singing bowl chime, victory horns).
  - Fallback: Zero-asset procedural 16-bit PCM waveform synthesizer (`AudioSoundManager`) for low-memory or offline asset-constrained environments.
- **Audio Routing**:
  - Volume scaling, stereo panning relative to board coordinates, and concurrency caps preventing audio clipping.

---

## 4. Deep Android & Kotlin Security Audit

- **CWE-89 (SQL Injection)**: Audit all Room queries in DAOs ensuring parameterized SQLite compilation with zero string interpolation.
- **CWE-926 (Component Exposure)**: Audit `AndroidManifest.xml` ensuring only `MainActivity` has `android:exported="true"` with explicit `MAIN`/`LAUNCHER` intent filters.
- **CWE-330 (Insufficient Randomness)**: Verify all dice rolls, sequence tokens, and room codes use `java.security.SecureRandom`.
- **CWE-312 (Cleartext Sensitive Storage)**: Ensure user IDs and guest tokens are stored within app-private SQLite databases protected by Android OS sandboxing.
- **CWE-400 (Resource Exhaustion)**: Audit coroutine WebSocket flows to enforce bounded queues (`extraBufferCapacity = 64`) and strict 15s turn timeout forfeiture.

---

## 5. WebGL/Canvas 3D Preview Harness & Playwright Verification

- **Preview Harness (`preview/index.html` & `preview/app.js`)**:
  - Full-fidelity WebGL / 3D Canvas rendering implementation displaying the realistic 3D dice tumbling, the animated slithering 3D viper, and the Kathmandu mandala board.
  - Interactive controls to trigger rolls, snake swallow animations, and camera rotations.
- **Playwright Test Suite (`tests/visual-3d.spec.js`)**:
  - Uses `playwright-cli` to launch the preview, interact with 3D elements, capture high-resolution frame screenshots, and verify visual contrast and anti-slop aesthetic standards.
