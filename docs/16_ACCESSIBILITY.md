# Accessibility Specification

## Accessibility Objectives
Ludora aims to be fully playable by everyone, including players with visual, motor, or auditory impairments. The application ensures all critical game information is communicated through multiple sensory channels (color, shape, sound, and text).

## Text Readability and Scalability
- Support for Dynamic Font Scaling up to 200% without breaking layouts, clipping text, or overlapping buttons.
- Minimum font size of 14sp for all functional labels and 12sp for secondary metadata.
- Clean sans-serif typefaces (Inter or Outfit) with open apertures and distinct character shapes to prevent reading confusion.

## Color Contrast Standards
- Compliance with WCAG 2.1 Level AA standards across all interfaces:
  - Minimum contrast ratio of 4.5:1 for normal text against background surfaces.
  - Minimum contrast ratio of 3.0:1 for large display text and interactive icons.
  - Player colors calibrated specifically to maintain at least 3.0:1 contrast against both board cell tiles and background surfaces.

## Touch Target Dimensions
- Every interactive element maintains a minimum touch target bounding box of 48x48dp.
- Tokens on dense board segments use expanded virtual touch regions to prevent mis-taps when multiple pieces sit on adjacent squares.
- Interactive spacing of at least 8dp between adjacent action buttons.

## Reduced Motion Mode
- Respects system-level Android "Remove Animations" setting automatically.
- Provides an in-app "Reduced Motion" toggle in Settings:
  - Replaces dice rolling animations with an instant 100ms fade-in of the final rolled value.
  - Replaces multi-step token hopping with an instant fade-and-reposition to the target tile.
  - Disables background particle effects and victory confetti.

## Screen Reader Support
- Full TalkBack compatibility across all screens.
- Explicit `contentDescription` tags on all Compose elements:
  - Dice: "Dice rolled value 4. Tap token to move."
  - Tokens: "Player 1 Red Token on square 14. Can advance to square 18."
  - Turn HUD: "Player 2's turn. 12 seconds remaining."
- Logical focus traversal order moving systematically clockwise around the board.

## Audio Alternatives and Haptic Feedback
- Redundant Multi-Modal Feedback: Every critical sound cue (turn chime, capture sound, dice roll impact, victory fanfares) pairs with:
  - Distinct haptic vibration patterns.
  - Clear visual text banners or animated icon badges.
- Closed captions and visual popup indicators for players with hearing impairments.

## Color-Independent Player Identification
- Never rely on color alone to identify players or pieces.
- Geometric Shape Markers:
  - Player 1 (Red): Embossed Circle icon.
  - Player 2 (Green): Embossed Triangle icon.
  - Player 3 (Yellow): Embossed Diamond icon.
  - Player 4 (Blue): Embossed Square icon.
- High-Contrast Color Blind Modes:
  - Deuteranopia / Protanopia adjusted palette presets.
  - Monochromatic high-contrast pattern overlays (stripes, dots, cross-hatching) for board quadrants.

## Accessibility Configuration Settings
A dedicated Accessibility section in the Settings screen contains:
- Color Blind Palette selector (Standard, Deuteranopia, Protanopia, Tritanopia).
- Piece Icon Overlay toggle (On by default).
- High Contrast Board Mode toggle.
- Reduced Motion toggle.
- Haptic Intensity slider (Off, Light, Medium, Strong).
- Screen Reader Verbosity selector (Compact, Detailed).
