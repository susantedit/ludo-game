# Game Design Document

## Core Gameplay Loop
The fundamental loop applies across all Ludora game variations:
1. Turn Initialization: Active player is signaled via visual highlight, audio chime, and optional haptic pulse.
2. Roll Phase: Player taps or drags the active dice cup to execute a roll (values 1 through 6).
3. Evaluation Phase: Game engine evaluates all tokens belonging to the player against board rules and determines legal destinations.
4. Move Phase:
   - If exactly one legal move exists, the engine either moves automatically or highlights the selectable token based on user settings.
   - If multiple legal moves exist, the player selects their chosen token.
   - If zero legal moves exist, turn passes immediately to the next player.
5. Action Resolution: Token moves along its path tile-by-tile. Landings resolve captures, safe zones, ladders, snakes, or power-up squares.
6. Bonus Check: Rolling a 6, capturing an opponent, or entering home grants an extra roll. Otherwise, turn advances to the next player clockwise.
7. Victory Evaluation: When a player satisfies win conditions, the engine triggers match victory routines and distributes progression XP and rewards.

## Ludo
- Board Configuration: Standard 4-arm cross circuit containing 52 shared perimeter steps, 4 home paths of 5 steps each, and 4 home center goals.
- Token Supply: 4 tokens per player across Red, Green, Yellow, and Blue bases.
- Deployment Rule: A roll of 6 releases one token from the base to the start square.
- Movement: Tokens travel clockwise around the perimeter track and turn into their respective colored home stretch.
- Captures: Landing on an opponent token on an unprotected tile captures it and sends it back to the owner base. The capturer receives a bonus roll.
- Safe Tiles: Starting tiles and starred tiles prevent captures. Multiple tokens from different players may occupy safe tiles concurrently: TBD - requires a later architectural decision.
- Home Entry: Reaching the center requires an exact roll or standard bounce-back according to configured rules.

## Snake & Ladder
- Board Configuration: 10x10 numbered grid from 1 (bottom left) to 100 (top left) running in boustrophedon (alternating row direction) order.
- Tokens: 1 token per player in standard mode; 2 to 4 tokens in multi-token variants.
- Snakes: Landing on a snake head moves the token down to the tail tile.
- Ladders: Landing on the base of a ladder moves the token up to the top tile.
- Victory Condition: First player whose token lands on square 100 wins the match.
- Movement Variant: Exact roll requirement vs bounce-back when approaching 100: TBD - requires a later architectural decision.

## Remix Mode
- Ludora Remix blends elements of Ludo and Snake & Ladder:
  - Hazard Circuit: Ludo perimeter track features dynamic snake squares (drop tokens back 6 tiles) and ladder squares (jump tokens forward 6 tiles).
  - Multi-Token Snake: Players manage 4 tokens on a 100-square board, racing to bring all 4 pieces to square 100.
  - Power Cards: Players draw cards upon capturing tokens or landing on mystery squares (e.g., Shield, Reroll, Boost, Swap).
  - Chaos Modifiers: Rotating board modifiers change conditions every 3 rounds (e.g., Double Roll, Safe Haven Deactivation, Speed Timer).

## Game Modes
- Play vs AI: Single player against 1 to 3 computer-controlled opponents with selectable difficulty.
- Local Pass and Play: Single device multiplayer with 2 to 4 human players passing the phone or tablet.
- Quick Match Online: Public matchmaking pairing players in casual or ranked queues.
- Private Room Online: Custom match created by a host with a 6-character room code.
- Practice Mode: Freeform board for testing rules and mechanics without stat tracking.

## AI
- AI Architecture: Heuristic decision trees evaluating move utility locally on device:
  - Easy: High randomness; does not plan ahead or aggressively pursue captures.
  - Medium: Prioritizes token rollout, base defense, and easy captures.
  - Hard: Calculates board state utility, tracks threat zones, prioritizes token consolidation, and blocks opponents.
- Turn Latency: Artificial delays (400ms to 800ms) prevent AI turns from executing unnaturally fast.

## Local Multiplayer
- Support for 2, 3, or 4 players on a single device.
- Screen Orientation: Portrait mode default; optional board rotation or table layout for seated opponents.
- Player slot customization: Mix human and AI players across any of the 4 color slots.

## Online Multiplayer
- Real-time turn synchronisation using server-authoritative state.
- Fixed turn timer (default 15 seconds) displayed via a circular visual countdown.
- Auto-play fallback: If a player disconnects or lets the timer expire, an automated bot handles the turn to prevent stalling.
- Surrender and match forfeit options.

## Progression
- Experience Points (XP) earned from:
  - Match completion.
  - Match victory.
  - Opponent token captures.
  - Successful ladder climbs.
- Account levels (Level 1 to 100) unlocking profile frames, titles, and currency.
- Stat tracking: Total games, win rates, capture totals, highest win streaks, favorite boards.

## Rewards
- Soft Currency (Coins): Earned through gameplay, leveling up, daily logins, and optional rewarded ads. Used to unlock cosmetics.
- Daily Quests: 3 refreshed quests daily (e.g., "Capture 4 tokens", "Win 1 Snake & Ladder match", "Roll 5 sixes").
- Weekly Milestones: Tiered progression bars awarding rare cosmetic items upon completing quest thresholds.

## Cosmetics
- Dice Styles: Classic ivory, neon cyan, obsidian gold, molten lava, pixel retro.
- Token Styles: Traditional pawn, robotic drone, mythical dragon, crown knight, astronaut helmet.
- Board Themes: Kathmandu Valley, Cosmic Nebulae, Buccaneer Cove, Minimalist Nordic.
- Emotes and Stickers: Fast in-game reactions for online rooms (e.g., celebration, crying, facepalm, applause).
- Cosmetic purity: Cosmetics never grant stat increases, extra rolls, or board bonuses.

## Events
- Weekend Chaos: Limited-time queues with radical Remix modifiers active.
- Seasonal Leaderboards: Monthly competitive rankings resetting with exclusive badges and cosmetic trophies.
- Themed Festivals: Time-limited boards and quests celebrating cultural holidays and regional milestones.
