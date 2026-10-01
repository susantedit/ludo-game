# Offline Architecture

## Offline-First Principle
Ludora treats offline mode as a primary target rather than a fallback state. The entire game runtime operates independently of network connectivity. Network absence must never trigger blocking error dialogs, lock screens, or degraded engine functionality.

## Local Storage
- Local Persistence Layer: Android Room database over SQLite for structured records (profiles, match history, unlocked assets, statistics).
- Key-Value Storage: Jetpack DataStore (Preferences and Proto DataStore) for application settings, audio volumes, accessibility options, and cache flags.
- Integrity: Local SQLite databases use SQLCipher or file-level encrypted keystores to prevent trivial local tampering: TBD - requires a later architectural decision.

## Local Profile
- Creates an anonymous LocalProfile record on initial launch without requiring user credentials.
- Tracks:
  - Local player display name.
  - Selected avatar and frame identifiers.
  - XP, player level, and match statistics (matches played, wins, captures).
  - Unlocked dice skins, token skins, and board themes.
  - Local currency balances earned through offline gameplay.
- Synchronizes with an OnlineProfile only when the user explicitly signs in to online services.

## Game State
- Immutable state model representing board configurations, player turns, token positions, dice values, and remaining move options.
- State transitions execute as pure functions within the game engine:
  `(CurrentState, Action) -> NewState`
- Every action produces a snapshot in a local state buffer, enabling instant replay, undo in practice mode, and deterministic restoration.

## Save and Resume
- The game engine serializes the active match state to local storage whenever:
  - The app enters the background (onPause / onStop).
  - A turn completes.
  - An incoming phone call or OS interruption occurs.
- On app launch or return, if an uncompleted local match exists, the user receives an option to resume the match or return to the main menu.
- Expired or abandoned matches archive automatically into match history.

## Offline AI Engine
- Pure Kotlin heuristic evaluation engine executing on background coroutine dispatchers (Dispatchers.Default).
- Operates entirely in-memory with zero remote API dependencies.
- Analyzes candidate token moves against weighted heuristics (distance to goal, vulnerability to capture, offensive capture opportunities, safe zone proximity).
- Execution takes less than 15ms per decision, with deliberate UI delays inserted so moves feel natural to human opponents.

## Local Multiplayer
- Coordinates turn handoffs between 2, 3, or 4 participants on a single device screen.
- Manages player seat assignments, color mappings, and optional screen orientation flips.
- Allows any combination of human seats and AI seats in a single local session.

## Offline Achievements
- Evaluation engine listens to local game event streams (e.g. `TokenCapturedEvent`, `MatchWonEvent`, `ConsecutiveSixEvent`).
- Evaluates criteria locally and unlocks achievements immediately.
- Queues completed achievements in a pending sync table so that when the user reconnects online, server-side achievements unlock without data loss.

## Offline Settings
All user settings persist locally via Jetpack DataStore:
- Sound effects, music, and ambient audio levels.
- Haptic feedback toggles.
- Board theme, dice skin, and piece skin selections.
- Animation speed settings (1.0x, 1.5x, 2.0x).
- Accessibility settings (high contrast, color blind mode, reduced motion).

## Zero Network Connectivity Guarantees
When running in airplane mode or in areas with no reception, Ludora guarantees:
1. Application cold starts in under 1.5 seconds without attempting network handshakes.
2. All offline modes (Ludo, Snake & Ladder, Remix, AI, Pass & Play) are fully playable.
3. Zero ad requests, tracking beacons, or telemetry timeouts.
4. No modal error dialogs displaying "Connection Lost" or "Please Connect to Internet".
5. Unlocked cosmetics and earned coins remain accessible.
