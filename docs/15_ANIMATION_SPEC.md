# Animation Specification

## Motion Principles
Animations in Ludora communicate game state, clarify spatial relationships, and provide tactile satisfaction. Motion is never added purely for decorative filler.
- Purposeful: Every movement tells the player what happened on the board.
- Snappy: Animations prioritize speed over long flourishes. Standard step animations take between 120ms and 180ms per tile.
- Interruptible: Animations must not lock out user inputs longer than necessary. In offline single-player mode, tapping the screen skips long animations immediately.
- Implementation Reference: Detailed motion implementation will follow the `animate` skill standards during Phase 10.

## Dice Roll Animation
- Trigger: User taps or swipes the dice tray.
- Sequence:
  1. Lift & Shake (0ms - 150ms): Die scales up to 1.15x and rotates rapidly through 3 axes.
  2. Bounce & Settle (150ms - 450ms): Die drops toward the center of the tray with spring physics, bouncing twice before resting face up.
  3. Value Flash (450ms - 600ms): The rolled face pips flash with a bright accent pulse.
  4. Haptics: Light vibration pulses during each bounce; distinct medium pulse on settling.
- Total Duration: 600ms maximum. Configurable to 300ms in fast-mode settings.

## Token Movement
- Path Traversal: Tokens hop from tile center to tile center along the board path rather than sliding continuously through walls.
- Arc Geometry: Parabolic elevation arc reaching peak height of 12dp midway between cell centers.
- Timing: 150ms per step using an ease-in-out cubic easing curve.
- Audio Sync: Subtle wooden tap audio click triggers precisely at the moment of cell impact on each step.

## Capture Animation
- Trigger: A moving token lands on an unprotected opponent token.
- Sequence:
  1. Impact (0ms - 80ms): The capturing token lands with a heavier bounce; small dust or shockwave ring expands outward.
  2. Knockback (80ms - 250ms): The captured token spins and shrinks slightly as it lifts off the board.
  3. Base Return (250ms - 600ms): Captured token traces a smooth accelerated arc directly back to its designated base slot.
  4. Audio & Haptics: Heavy capture sound effect accompanied by a sharp haptic impact pulse.

## Victory and Win Screen Animation
- Trigger: Final winning token enters the goal.
- Sequence:
  1. Board Dimming: Non-winning board elements dim to 40% brightness.
  2. Spotlight: The winning player quadrant and center goal illuminate.
  3. Particle Burst: Stream of celebratory confetti bursts from the center goal.
  4. Trophy Card Entrance: 3D trophy or banner scales in from 0.8x to 1.0x with smooth overshoot spring curve.
- Total Duration: 1200ms before displaying interactive "Play Again" buttons.

## Snake Animation
- Trigger: Token lands on a snake head tile in Snake & Ladder or Remix mode.
- Sequence:
  1. Hazard Cue (0ms - 150ms): Snake eyes flash; subtle warning sound plays.
  2. Descent (150ms - 700ms): Token slides smoothly along the curved body path of the snake down to the tail tile.
  3. Thud (700ms - 850ms): Token settles onto the destination tile with a light downward wobble.

## Ladder Animation
- Trigger: Token lands on the base tile of a ladder.
- Sequence:
  1. Clang & Lift (0ms - 150ms): Upbeat chime plays as the token lifts slightly off the board.
  2. Rung Climbing (150ms - 650ms): Token bounds rapidly up each ladder rung with quick ascending pitch chimes.
  3. Top Arrival (650ms - 800ms): Token steps off the ladder onto the target tile with a confident landing pulse.

## Screen Transitions
- Shared Element Transitions: Board cards on the home screen expand smoothly into the full-screen match view.
- Sub-screen Navigation: Fast horizontal slide-and-fade (250ms, DecelerateEasing) for moving between dashboard tabs.

## UI Micro-Interactions
- Button Press: Instant scale compression to 0.96x on finger-down, springing back to 1.0x on release.
- Turn Indicator: Gentle breathing wave traversing around the active player avatar frame.
- Selectable Token: Subtle vertical bobbing (4dp up and down, 1.2s period) to indicate selectable pieces.

## Loading Animations
- Offline boot: Zero blocking loading screens.
- Asset and Network Load: Elegant rotating wooden die with changing face numbers. Avoid generic circular spinners.

## Online Connection States
- Reconnecting State: A subtle amber banner slides in from the top edge with an animated pulsating wifi icon.
- Connection Restored: Banner shifts to emerald green ("Connected"), displays for 1.5 seconds, and slides off-screen smoothly.
