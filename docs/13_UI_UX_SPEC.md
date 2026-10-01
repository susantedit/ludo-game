# UI/UX Specification

## Information Architecture
The interface structure is hierarchical, keeping primary actions accessible within two taps of the home dashboard:
```text
Root Application
├── Home Dashboard
│   ├── Quick Play (Direct into last-played mode)
│   ├── Play Offline
│   │   ├── Ludo Setup (Players, AI count, Difficulty, Rules)
│   │   ├── Snake & Ladder Setup (Players, AI count, Theme)
│   │   └── Remix Setup (Hazard modifiers, Power cards)
│   ├── Play Online
│   │   ├── Quick Matchmaking
│   │   ├── Create Private Room
│   │   └── Join with Code
│   ├── Profile & Inventory (Stats, Skins, Boards, Dice)
│   ├── Friends & Social (Presence, Invites)
│   └── Settings (Audio, Accessibility, Graphics)
└── Active Match Shell
    ├── Board Canvas (Interactive game surface)
    ├── Player HUDs (Avatars, turn timers, score/progress)
    ├── Dice Area (Roll button, dice physics container)
    └── In-game Menu / Surrender Modal
```

## Navigation Flow
- Built using Navigation Compose with type-safe route definitions.
- Deep-linking support for 6-character room codes (`ludora://room/{code}`).
- Back navigation rules:
  - Back press during an active match prompts a confirmation dialog to prevent accidental forfeit.
  - Back press from setup screens returns to the Home Dashboard without preserving unconfirmed configurations.

## Home Screen
- Header: Player mini-profile (avatar, display name, current level, coin balance, online status pill).
- Hero Area: Prominent Play button triggering last-played mode or game picker.
- Mode Cards:
  - "Offline Play" (Pass & Play, AI Challenge).
  - "Online Play" (Quick Match, Private Rooms).
  - "Ludora Remix" (Rotating featured modifier).
- Bottom Navigation or Action Bar: Direct navigation to Shop/Inventory, Quests, Friends, and Settings.

## Game Selection Screen
- Visual cards displaying Ludo, Snake & Ladder, and Remix with clean vector artwork.
- Highlights game features: Player count (2-4), estimated match duration (5-15 min), and active modifiers.

## Ludo Screens
- Board Presentation: Clean geometric 4-color layout (Red, Green, Yellow, Blue) centered on the display.
- Token Visibility: 2.5D elevated pawn models with distinct contrasting base rings and player icons.
- Active Indicators: The active player quadrant glows softly; valid selectable tokens pulse gently to guide the user.
- Interactive Feedback: Tapping a valid token initiates smooth path stepping accompanied by per-step audio clicks.

## Snake & Ladder Screens
- Board Presentation: 10x10 boustrophedon grid with crisp cell numbering and high-contrast snake/ladder overlays.
- Snake Visuals: Styled serpentine paths with clear head markers and tail termination points.
- Ladder Visuals: Crisp structural rungs connecting source and destination cells.
- Path Highlights: When landing on a hazard or benefit, the path animates sequentially to communicate the movement.

## Remix Mode Screens
- Board Overlays: Displays hybrid hazard icons (snakes, ladders, mystery power-up cubes) overlaid directly onto the standard perimeter track.
- Active Modifier Banner: Top-center HUD pill communicating current round rules (e.g. "Round 3: Chaos Roll Active").
- Power Card Drawer: Slide-out bottom tray displaying held cards with clear action prompts ("Use Shield", "Boost +2").

## Offline Experience Flows
- Zero network checks or login interstitials.
- Instant player setup: Tap "Play Offline", select player count, assign AI seats, and launch directly into the match.
- Resume Banner: If a previous match was interrupted, a card on the home screen offers "Resume Match (Turn 14)".

## Online Experience Flows
- Connectivity Banner: Unobtrusive status indicator showing online state.
- Single-tap matchmaking queue with real-time wait estimation and cancel option.
- Smooth transition from lobby countdown directly to synchronized board rendering.

## Profile Screen
- Comprehensive player statistics: Total games, win percentage, longest win streak, captures tally.
- Equipment Showcase: 3D interactive preview of equipped dice, custom tokens, and board skins.
- Level Progression Bar: Visual milestone tracker showing upcoming rewards for next levels.

## Settings Screen
- Audio Controls: Independent sliders for Master, Music, Sound Effects, and Ambient Atmosphere.
- Haptics: Toggle for tactile vibration on dice rolls and captures.
- Graphics: Frame rate selector (30fps, 60fps, Device Default), particle effects toggle.
- Account: Link Account button, Data Export, and Account Deletion options.

## Friends Management Screen
- Search input for player tags.
- Tabbed list: Friends, Pending Requests, Blocked Users.
- Contextual action buttons: "Invite to Room", "View Profile", "Remove Friend".

## Matchmaking Screen
- Clean circular radar animation indicating queue search status.
- Cancel button accessible at all times before match assignment.
- Clear latency indicators displaying ping to regional server clusters.

## Private Room Screen
- Prominent 6-character room code with a single-tap "Copy Code" or "Share Link" action.
- 4 player seat cards displaying player avatar, ready status, and host badge.
- Host controls: Game mode selector, turn timer toggle, and "Start Game" button (active once minimum player count is met).

## Match Results Screen
- Podium presentation highlighting 1st, 2nd, 3rd, and 4th place finishes.
- Stat summary breakdown: Tokens captured, total steps moved, sixes rolled.
- Reward rollup: Animated counter detailing XP earned and coins collected.
- Action buttons: "Play Again", "Rematch" (in private rooms), and "Return Home".

## Rewards Screen
- Quest completion modal with celebratory particle effect.
- Level-up celebration showcasing unlocked cosmetics and currency grants.

## Error States
- Non-blocking toast notifications for transient errors (e.g. "Room code not found").
- Inline retry buttons for failed network requests.
- Graceful degradation without application crashes.

## Empty States
- Friends list empty state: "No friends added yet. Share your player tag to invite friends."
- Match history empty state: "No matches recorded. Complete your first match to view stats here."
- Inventory category empty state: "No items unlocked in this category yet."

## Loading States
- Skeleton loaders for profile and leaderboard screens.
- Rotating dice icon for initial app startup and asset decoding.
- Avoid full-screen blocking spinners during background state syncs.

## Network State Feedback
- Seamless, non-intrusive status pill at the screen edge indicating: "Offline Mode", "Connecting...", or "Connected".
- Offline mode is presented as a normal operating state, not an error condition.
