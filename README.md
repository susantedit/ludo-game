# Ludora

<p align="center">
  <img src="logo.png" width="220" alt="Ludora Logo" />
</p>

<p align="center">
  <strong>Where every roll matters.</strong><br>
  Offline-first mobile board game platform featuring 3D physics dice, procedural animations, authentic cultural boards, and modular game engines.
</p>

---

## Visual Highlights & 3D Interactive Assets

### 1. Realistic 3D Isometric Tumbling Dice
The 3D dice engine uses isometric projection with quaternion rotation matrices. When a roll finishes, the engine aligns the rolled face normal $(1..6)$ directly to the top isometric facet $(0, \sqrt{2/3}, 1/\sqrt{3})$ so the rolled number lands directly on the top surface.

<p align="center">
  <img src="assets/realistic_3d_dice.jpg" width="480" alt="Realistic 3D Tumbling Dice" />
</p>

* **Implementation:** [`Realistic3DDiceRenderer.kt`](file:///d:/ludo/core/designsystem/src/main/kotlin/game/ludora/core/designsystem/dice/Realistic3DDiceRenderer.kt) and [`preview/app.js`](file:///d:/ludo/preview/app.js)
* **Math:** Camera matrix $M = R_{\text{cam}} \cdot R_{\text{face}}$ mapping face orientations $[1..6]$ with specular lighting and round pips.

---

### 2. Authentic 15×15 Kathmandu Cultural Board
An authentic $15 \times 15$ tournament-regulation Ludo board themed after the Kathmandu Valley, Newari woodcraft, and Buddhist iconography.

<p align="center">
  <img src="assets/kathmandu_mandala_board.jpg" width="600" alt="Kathmandu Cultural Mandala Board" />
</p>

* **Implementation:** [`preview/app.js`](file:///d:/ludo/preview/app.js) and [`app.js`](file:///d:/ludo/app.js)
* **4 Corner Courtyards ($6 \times 6$ cells each):**
  * **Patan Chowk** (Top-Left, Crimson Red)
  * **Bhaktapur** (Top-Right, Forest Green)
  * **Basantapur** (Bottom-Right, Mustard Gold)
  * **Kirtipur** (Bottom-Left, Royal Blue)
  * Each courtyard includes an inner raised pedestal, brass trim, palace labels, and 4 token slots.
* **72 Track Cells:** Alternating dark walnut wood tiles with directional home path chevrons.
* **8 Sacred Safe Squares:** Marked with 8-pointed golden Nepali mandala stars at coordinates `(1, 6)`, `(6, 2)`, `(8, 1)`, `(12, 6)`, `(13, 8)`, `(8, 12)`, `(6, 13)`, and `(2, 8)`.
* **Central Victory Goal ($3 \times 3$ cells):** 4 home triangles topped by a gilded medallion featuring 16 sculpted lotus petals, Swayambhunath Wisdom Eyes, the sacred unity curl (*Ek*), and the red wisdom *Urna*.

---

### 3. Realistic 3D Peristaltic Snake
A procedural snake model designed for the Snake & Ladder game mode.

<p align="center">
  <img src="assets/realistic_3d_snake.jpg" width="480" alt="Realistic 3D Peristaltic Snake" />
</p>

* **Implementation:** [`preview/app.js`](file:///d:/ludo/preview/app.js)
* **Mechanics:** Cubic Bézier spine calculation, real-time peristaltic swallow bulges driven by a sine envelope function, metallic scale shading, and dynamic tongue flick animations.

---

## Core Games & Modes

1. **Classic Ludo:**
   * 2 to 4 player strategy board race.
   * Full tournament rules: token release on 6, captures returning opponent pieces to yard, safe squares, home column entry, and exact-roll finish.
   * Configurable house rules (consecutive sixes penalty, mandatory captures).
2. **Snake & Ladder:**
   * Traditional 10x10 boustrophedon track (100 cells).
   * Procedural snake swallow transitions and ladder ascents.
3. **Ludora Remix:**
   * Hybrid tactical combat mode on classic tracks.
   * Tile hazards, power cards, and rotating chaos modifiers.

---

## Monetization & AdMob Integration

Ludora balances free-to-play sustainability with player experience. Offline play remains 100% ad-free and functional.

### AdMob Configuration
* **Google Mobile Ads App ID:** `ca-app-pub-9145129314168118~7063463688` (declared in [`AndroidManifest.xml`](file:///d:/ludo/app/src/main/AndroidManifest.xml))
* **Ad Units (Release Builds):**
  * **Banner (`Ludo_Banner`):** `ca-app-pub-9145129314168118/3938692611`
  * **Interstitial (`Ludo_Match_Over`):** `ca-app-pub-9145129314168118/7682410787`
  * **Rewarded (`Ludo_Extra_Roll`):** `ca-app-pub-9145129314168118/9454657458`
* **Debug Protection:** Debug builds automatically route through Google's official test ad unit IDs in [`app/build.gradle.kts`](file:///d:/ludo/app/build.gradle.kts) to prevent invalid traffic penalties during development.
* **Ad Provider Architecture:**
  * [`AdMobProvider.kt`](file:///d:/ludo/app/src/main/kotlin/game/ludora/ads/AdMobProvider.kt): Production ad provider managing background preloading, dismissal callbacks, and rewarded currency grants.
  * [`AdBannerView.kt`](file:///d:/ludo/app/src/main/kotlin/game/ludora/ads/AdBannerView.kt): Jetpack Compose wrapper for AdMob banner ads.
  * [`AdPolicyManager.kt`](file:///d:/ludo/core/common/src/main/kotlin/game/ludora/core/common/ads/AdPolicyManager.kt): Strict policy enforcement (minimum 8-minute gap between interstitials, maximum 3 per hour, grace period for first 3 user sessions).
  * [`BillingService.kt`](file:///d:/ludo/core/common/src/main/kotlin/game/ludora/core/common/ads/BillingService.kt): "Remove Ads" pass contract suppressing all banners and interstitials.

---

## Audio, Haptics & Mascots

* **Himalayan Singing Bowl Synthesizer:** Real-time modal physical synthesis using resonant bandpass filters tuned to traditional singing bowl frequencies ($216\text{ Hz}$, $432\text{ Hz}$, $864\text{ Hz}$, $1296\text{ Hz}$).
* **Tactile Haptic Feedback:** Hand-tuned vibration wave patterns for dice rolls, token captures, safe-square landings, and victories via [`HapticFeedbackManager.kt`](file:///d:/ludo/core/common/src/main/kotlin/game/ludora/core/common/feedback/HapticFeedbackManager.kt).
* **56 Interactive Mascot Spritesheets:** Located in [`core/designsystem/src/main/assets/mascots/`](file:///d:/ludo/core/designsystem/src/main/assets/mascots/), featuring directional gaze tracking and emotion reactions.

---

## Repository Architecture

```text
ludo/
├── app/                        # Android application entry point & UI
│   ├── src/main/AndroidManifest.xml
│   └── src/main/kotlin/game/ludora/
│       ├── LudoraApplication.kt# MobileAds & app initialization
│       ├── MainActivity.kt     # Jetpack Compose root & screen navigation
│       ├── ads/                # AdMobProvider & AdBannerView
│       └── ui/                 # Game screens, dashboards, dialogs
├── core/
│   ├── common/                 # Ad policies, billing, audio, and haptics
│   ├── designsystem/           # Theme, 3D dice renderer, mascots
│   ├── database/               # Room database & local profile storage
│   ├── model/                  # Domain state models, token rules, quests
│   └── network/                # Realtime multiplayer & matchmaking client
├── engine/
│   ├── core/                   # Shared deterministic board logic
│   ├── ludo/                   # Ludo engine, valid moves, win conditions
│   ├── snake/                  # Snake & Ladder engine
│   ├── ai/                     # Heuristic & Monte-Carlo AI opponents
│   └── remix/                  # Ludora Remix power card rules
├── assets/                     # 3D visual renders & board photos
├── preview/                    # Web-based interactive testing studio
│   ├── index.html              # Interactive canvas studio & compliance hub
│   ├── styles.css              # Obsidian slate dark theme styling
│   └── app.js                  # 3D dice tumble, snake canvas, 15x15 board
└── docs/                       # Technical specifications (01 to 21)
```

---

## Web Preview Studio

The repository includes a web preview studio for verifying game mechanics, 3D animations, and compliance policies:
* Open [`preview/index.html`](file:///d:/ludo/preview/index.html) in any browser.
* Controls:
  * **Dice Roller:** Roll the 3D dice and inspect top-face orientation for faces 1 through 6.
  * **Snake Simulator:** Trigger peristaltic token swallow animations along a cubic Bézier curve.
  * **15×15 Board Studio:** Inspect the full cultural layout with 4 courtyards, 8 safe stars, and central medallion.
  * **Audio Vibe Engine:** Test the Himalayan singing bowl synthesizer.
  * **Compliance Hub:** 5-tab policy viewer (Privacy Policy, Terms of Service, Children's Privacy/COPPA, IAP & Refunds, Security & Fair Play) with live clause search.

---

## License & Compliance

* Built in accordance with Google Play Families Policy and COPPA standards.
* In-game advertising is gated by parental consent flags, age assurance gates, and server-side reward verification.
