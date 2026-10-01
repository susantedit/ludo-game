# Product Requirements Document (PRD)

## Product
**LUDORA**  
*Where every roll matters.*

---

## 1. Product Overview
Ludora is an offline-first mobile board-game application built around two classic games: Ludo and Snake & Ladder. The product provides a complete, polished gaming experience on a single mobile device without requiring network connectivity, accounts, or advertisements. 

When network connectivity is present, Ludora optionally extends gameplay with online multiplayer (Quick Match and Private Rooms with 6-character room codes), player presence, friend lists, server-authoritative matches, and cosmetic progression sync. Advertisements appear only during online connectivity and never interrupt active gameplay turns.

A core differentiator is Ludora's **Remix Mode**, which introduces modular rule modifiers, board hazards, and power-up cards to traditional game formats.

---

## 2. Problem Statement
Existing digital board games on mobile app stores suffer from common flaws:
1. **Network Hostage:** Many digital Ludo and board games require continuous network handshakes or remote authentication even for single-player AI or pass-and-play matches. Commuters, travelers, and players in areas with intermittent connectivity face lockouts, loading freezes, or degraded performance.
2. **Aggressive and Disruptive Monetization:** Free-to-play board games routinely force unskippable 30-second video advertisements during player turns, break game immersion, and offer pay-to-win mechanics (e.g. purchasing guaranteed dice rolls or rerolls).
3. **Stale Mechanics:** Traditional digital board games replicate physical cardboard without leveraging mobile software capabilities. Matches become monotonous after few sessions due to a lack of gameplay variations.

Ludora solves these problems by guaranteeing complete offline playability, confining ads strictly to online transitions outside of active matches, maintaining strict fair-play integrity, and introducing dynamic rule variations through Remix Mode.

---

## 3. Product Goals
1. **100% Offline Autonomy:** All core games (Ludo, Snake & Ladder, Remix Mode, AI opponents, and local multiplayer) must run without internet access, account creation, or background network pings.
2. **Zero In-Game Ad Interruptions:** Advertisements must never trigger during an active game turn, roll sequence, or match state. Offline players experience zero ads.
3. **No Pay-To-Win:** Virtual currency and rewards are strictly tied to cosmetic customization (dice skins, token styles, board themes, profile frames). Dice mechanics remain strictly fair and unrigged.
4. **Reliable Online Progression & Play:** Optional online multiplayer with server-authoritative state reconciliation, private rooms, and instant 6-character room codes.
5. **Fast Native Performance:** Launch to interactive board in under 2 seconds, maintaining 60 frames per second rendering across entry-level and high-end mobile devices.

---

## 4. Non-Goals
1. **Real-Money Gambling or Betting:** Ludora does not support cash wagers, cryptocurrency, or real-money payouts.
2. **Heavy 3D Engine Footprint:** Ludora will not use heavy 3D game engines (e.g., Unity or Unreal) that bloat APK sizes and drain mobile batteries.
3. **Mandatory Social Graph:** Ludora will not require social logins (Facebook, Google, Apple) to access the application or play games locally.
4. **Unbounded Voice/Video Chat:** In-game communications are limited to preset emotes and text phrases to eliminate toxic behavior, moderation burdens, and high data consumption.
5. **Cross-Platform PC/Console Parity in MVP:** MVP is targeted strictly at mobile devices (Android baseline).

---

## 5. Target Users
1. **The Daily Commuter / Offline Player:** Mobile users on buses, trains, flights, or rural regions with no or unstable cellular reception. Needs immediate, distraction-free games against AI or offline pass-and-play.
2. **Families and Casual Groups:** Friends or family members in the same physical room wanting pass-and-play on a single phone or tablet without buying multiple physical boards.
3. **Board Game Traditionalists:** Players seeking standard, classic rules of Ludo and Snake & Ladder with clean tactile visuals, haptic feedback, and predictable rule enforcement.
4. **Competitive Social Players:** Friends playing remotely via private room codes or public casual/ranked matchmaking with reliable reconnection handling.

---

## 6. Core User Journeys

### Journey 1: First-Time Offline Play
1. User downloads app, boards an airplane, and turns on Airplane Mode.
2. User opens Ludora. The app loads directly to the Home Dashboard in < 1.5 seconds.
3. User selects "Play Offline" -> "Ludo".
4. User selects 4 players (1 Human, 3 AI Medium), configures rule options, and taps "Start Game".
5. Match proceeds to completion with zero network prompts, zero ad interruptions, and full sound/haptics.
6. Match concludes; user earns local XP and coins, viewing a local summary screen before returning home.

### Journey 2: Pass-and-Play Family Session
1. Three family members sit around a table with a single tablet.
2. Host taps "Local Multiplayer" -> "Snake & Ladder".
3. Host assigns 3 human seats with chosen colors and player names, selecting 1 AI player for the 4th slot.
4. The board indicates whose turn it is with clear color highlights, rotational HUD indicators, and sound alerts.
5. Players pass the device; the match completes, awarding local stats to the active device profile.

### Journey 3: Remote Private Room Match
1. Host opens Ludora with active internet connectivity.
2. Host taps "Play Online" -> "Create Room".
3. Host selects "Ludora Remix", sets a 15-second turn timer, and receives a 6-character code: `K7X92P`.
4. Host taps "Share Code" and texts it to 3 friends via chat apps.
5. Friends open Ludora, tap "Join Room", paste the code, and enter the lobby.
6. Host starts the match. The server manages all dice rolls, moves, and turn clocks authoritatively.
7. One player temporarily loses cell reception for 20 seconds. An AI bot handles their turn during timeout.
8. Player reconnects, downloads the current snapshot, re-assumes control of their seat, and finishes the match.

---

## 7. Offline Experience

### Priority: MUST HAVE

### User Goal
Play any game mode against AI or local friends anytime, anywhere, with zero connectivity, zero accounts, and zero ad interruptions.

### Functional Requirements
- The application runtime must never query remote servers, block UI threads, or display network error dialogs when disconnected.
- Local profile records, stats (games played, win rates), settings, and unlocked cosmetics store directly on device.
- Full access to all game engines: Ludo, Snake & Ladder, and Remix Mode.
- Matches can be saved and resumed if the app is closed or backgrounded during offline play.
- Zero ad SDK initialization or ad network requests while offline.
- Offline soft currency (Coins) and experience points (XP) accumulate in a secure local database.

### Important Edge Cases
- **App Killed Mid-Turn:** The engine auto-serializes game state after every turn resolution. Upon restart, the home screen displays a "Resume Match" card.
- **Clock Manipulation:** If device system clock is set forward/backward, match state integrity remains intact via monotonic session timestamps.
- **Low Storage:** App checks local storage capacity gracefully and notifies user if space is insufficient to persist match history.

### Offline vs. Online Behavior
- **Offline:** 100% of game mechanics, AI, local settings, and pass-and-play modes execute locally. No ads displayed.
- **Online:** Matches can be played remotely; progression and match history sync to cloud profile when linked.

### Dependencies
- Local Persistence Layer (Room Database / Key-Value Store: TBD — requires architectural decision).
- Pure Kotlin deterministic game engine.

### Acceptance Criteria
- App launches and reaches playable match state in Airplane Mode without throwing unhandled exceptions.
- Zero ad requests or network sockets opened when device connectivity is false.
- Match resumes at exact board positions and turn index following process termination.

---

## 8. Ludo

### Priority: MUST HAVE

### User Goal
Play traditional 2 to 4 player Ludo with clear rules, tactile dice rolling, and customizable house rules.

### Functional Requirements
- **Board Layout:** Standard 4-arm cross circuit containing 52 shared perimeter steps, 4 colored home paths of 5 steps each, and 4 home center goals.
- **Player Colors:** Red, Green, Yellow, Blue.
- **Tokens:** 4 tokens per player base.
- **Rollout Rule:** Rolling a 6 is required to release a token from the base to the starting step.
- **Movement:** Clockwise movement around the outer perimeter according to the rolled die value (1 to 6).
- **Captures:** Landing on an opponent token on an unstarred, non-safe square captures the opponent token, returning it to its owner's base.
- **Bonus Rolls:** Granted upon:
  1. Rolling a 6.
  2. Capturing an opponent token.
  3. Moving a token into the home center goal.
- **Consecutive Sixes Rule:** Configurable rule. By default, rolling three consecutive 6s forfeits the third roll and advances the turn immediately.
- **Safe Squares:** 4 starting steps (one per color) and 4 starred perimeter steps prevent captures. Multiple tokens can occupy safe squares simultaneously.
- **Home Entry:** Tokens must navigate up their respective colored 5-step home lane. Entering the center requires an exact roll. If the roll exceeds the required steps, the piece cannot move.
- **Win Condition:** The first player to bring all 4 tokens into the center goal wins 1st place. The match continues for 2nd, 3rd, and 4th place.

### Configurable House Rules (SHOULD HAVE)
- Three 6s Penalty: On/Off.
- Mandatory Capture: If a capture move is legally available, player must take it: On/Off.
- Token Stacking: Two tokens of the same color on a single square form an impassable barrier: TBD — requires architectural decision.

### Important Edge Cases
- **No Legal Moves:** If a player rolls a value where no token can move (e.g. all pieces in base and rolls a 3), the engine displays a brief "No Move" cue and advances turn after 1 second.
- **Multiple Move Options:** When multiple tokens have legal moves, the engine highlights only valid tokens and awaits user tap.
- **Single Legal Move:** If only one token can legally move, user setting determines whether it auto-moves or awaits confirmation tap.

### Offline vs. Online Behavior
- **Offline:** Dice generation and move validation executed by local engine.
- **Online:** Server generates random roll value and validates legal move commands.

### Dependencies
- Local Game Engine (`engine/ludo`).

### Acceptance Criteria
- Unit tests verify 100% of Ludo rule branch logic (rollout, pathing, safe squares, captures, bonuses, home entry).
- Match concludes precisely when all tokens of the winning player reach the home goal.

---

## 9. Snake & Ladder

### Priority: MUST HAVE

### User Goal
Experience traditional Snake & Ladder on a clean 100-square grid with clear visual indicators of hazard and benefit paths.

### Functional Requirements
- **Board Grid:** 10x10 numbered grid from 1 (bottom left) to 100 (top left) arranged in boustrophedon (alternating row direction) order.
- **Player Capacity:** 2 to 4 players.
- **Token Count:** 1 token per player in standard mode.
- **Ladders:** Landing on the base of a ladder immediately moves the token to the top square of that ladder.
- **Snakes:** Landing on the head of a snake immediately moves the token down to the tail square of that snake.
- **Pass-through Behavior:** Moving past a ladder base or snake head does not trigger movement; token must land directly on the tile.
- **Finish Tile (Square 100):** Exact roll required to land on square 100.
  - Configurable: Excess roll bounces backward from square 100 or causes the token to remain stationary: TBD — requires architectural decision.
- **Bonus Rolls:** Rolling a 6 grants one bonus roll. Maximum consecutive 6s allowed is two; a third cancels the turn.

### Important Edge Cases
- **Chained Hazards:** A ladder top never connects directly to a snake head, and a snake tail never terminates on a ladder base.
- **Square 99 Hazard:** Traditional snake head on square 99 requires careful navigation.
- **Simultaneous Occupancy:** Multiple player tokens can occupy the same numerical square without capturing or knocking back.

### Offline vs. Online Behavior
- **Offline:** Resolved on-device via local random number generator and local board definition.
- **Online:** Server resolves roll, calculates board coordinate, and broadcasts token trajectory.

### Dependencies
- Local Game Engine (`engine/snake`).

### Acceptance Criteria
- Engine properly resolves all snake falls and ladder climbs.
- Game strictly prevents winning without satisfying square 100 rule conditions.

---

## 10. AI Opponents

### Priority: MUST HAVE

### User Goal
Enjoy challenging, realistic single-player games offline without waiting for human players.

### Functional Requirements
- **AI Profiles:** 3 selectable difficulty levels:
  1. **Easy:** Selects randomly among legal moves. Rarely anticipates captures or protects vulnerable tokens.
  2. **Medium:** Prioritizes releasing tokens from base on a 6, capturing opponent tokens within range, and moving tokens safely into the home stretch.
  3. **Hard:** Utility-based evaluation. Calculates risk-reward: weighs capture probability, distance to goal, proximity to threat tokens, blocking chokepoints, and safe square occupancy.
- **Execution Timing:** AI turns must simulate natural deliberation (300ms to 700ms) with animated dice and token taps. AI must not move instantaneously.
- **Slot Flexibility:** AI can fill any seat (Seats 1-4) in single-player or local multiplayer games.

### Important Edge Cases
- **No Moves Available:** AI recognizes zero legal moves and yields turn smoothly without stalling the game loop.
- **Multiple Equal-Utility Moves:** AI uses deterministic pseudo-random tie-breaking among equal-score moves.

### Offline vs. Online Behavior
- **Offline:** Runs locally on background coroutine dispatchers with zero remote API dependencies.
- **Online:** Used as automated fallback bot when a human player disconnects or times out in online matches.

### Dependencies
- Local AI evaluation module (`engine/ai`).

### Acceptance Criteria
- AI never executes an illegal move under any circumstance.
- Hard AI demonstrates statistically higher win rates against Medium and Easy AI over 1,000 automated simulated matches.

---

## 11. Local Multiplayer

### Priority: MUST HAVE

### User Goal
Pass a single mobile device or tablet around a group to play a full board game with friends and family.

### Functional Requirements
- Supports 2, 3, or 4 human players on a single device screen.
- Allows mixing human and AI players across any seats (e.g. 2 humans vs 2 AI).
- Turn Handoff Indicator: Clear visual banner, audio chime, and optional haptic vibration signaling the next active player.
- Player Seat Customization: Players can set custom local display names and pick their token colors during match setup.
- Tabletop Orientation (SHOULD HAVE): In tablet or landscape view, HUD elements orient toward seated sides of the board.

### Important Edge Cases
- **Accidental Screen Touch During Handoff:** Touch rejection during turn transition animation prevents accidental dice rolls while passing the phone.
- **Screen Rotation:** Changing device orientation preserves board coordinates and active turn state without reloading.

### Offline vs. Online Behavior
- **Offline:** 100% self-contained on the host device.
- **Online:** Not applicable.

### Dependencies
- UI Component Library & Turn State Machine.

### Acceptance Criteria
- Complete 4-player pass-and-play match runs from start to finish without requiring network access or external accounts.

---

## 12. Remix Mode

### Priority: MUST HAVE (Foundation), SHOULD HAVE (Advanced Modifiers)

### User Goal
Experience fresh, high-energy variants of traditional board games with unexpected twists, power cards, and board hazards.

### Functional Requirements

#### 1. Hazard Circuit (Ludo + Snake & Ladder) [MUST HAVE]
- The standard Ludo 52-tile perimeter track includes 4 snake hazard squares and 4 ladder boost squares.
- Landing on a snake hazard pushes the token backward 6 squares. If it lands on an opponent on an unstarred square, it captures them.
- Landing on a ladder square advances the token forward 6 squares.
- Snake and ladder squares are never placed on base rollout tiles or home lane entrances.

#### 2. Power Cards (SHOULD HAVE)
- Players gain single-use power cards by capturing an opponent or landing on designated mystery squares.
- Card Hand Limit: Maximum 2 cards in hand per player.
- Proposed Cards:
  - **Shield:** Protects active token from captures and snakes for 2 consecutive rounds.
  - **Reroll:** Discards current roll and rolls again immediately.
  - **Boost:** Adds +2 steps to the current rolled value.
  - **Swap:** Swaps positions between the active token and another chosen token on the perimeter.

#### 3. Chaos Modifiers (COULD HAVE)
- Game engine rotates global match modifiers every 3 rounds:
  - *Speed Round:* Turn timers drop to 7 seconds.
  - *No Safe Havens:* Starred squares lose capture immunity for 1 round.
  - *Double Dice:* Players roll two dice and pick the preferred value.

#### 4. Custom Rule Studio (FUTURE)
- Players can configure custom board sizes, card spawn frequencies, and penalty rules for private rooms.

### Important Edge Cases
- **Power Card Use Timing:** Cards must be played before rolling the dice or immediately after rolling, depending on card type.
- **Hazard Loop Prevention:** A backward snake drop cannot land on another snake square.

### Offline vs. Online Behavior
- **Offline:** All Remix rules and card effects resolve locally inside the offline engine.
- **Online:** Server-authoritative card deck shuffling and modifier triggers.

### Dependencies
- Remix Engine Extension (`engine/remix`).

### Acceptance Criteria
- Unit tests verify all card actions and hazard relocations trigger accurately and handle bounds collisions.

---

## 13. Progression

### Priority: SHOULD HAVE (Offline baseline in MVP)

### User Goal
Earn experience, level up, unlock titles, and collect cosmetic currency through regular play.

### Functional Requirements
- **XP Engine:** Players earn XP for:
  - Match completion: 50 XP.
  - Match victory: 150 XP.
  - Token capture: 20 XP.
  - Reaching home / finish line: 25 XP.
- **Leveling Curve:** Levels 1 to 100 with incremental XP requirements.
- **Soft Currency (Coins):** Earned via level milestones, match completions, daily quests, and optional rewarded ads.
- **Daily Quests:** 3 quests generated daily (e.g. "Roll three 6s", "Win 1 Ludo match", "Capture 3 tokens").
- **Offline Integrity:** XP and Coins accumulate locally. A tamper-evident signature or hash ensures local values cannot be trivially modified: TBD — requires architectural decision.

### Important Edge Cases
- **Offline to Online Sync:** When a guest profile links to an online account, the server validates offline progress within reasonable bounds before merging.
- **Negative Progression:** Losses never subtract XP, levels, or coins.

### Offline vs. Online Behavior
- **Offline:** Stored and rewarded locally. Quests track local match events.
- **Online:** Server validates match results and issues verified currency adjustments.

### Dependencies
- Local Profile Database (`core/database`).

### Acceptance Criteria
- Matches played completely offline award XP and coins correctly, persisting across app restarts.

---

## 14. Cosmetics

### Priority: SHOULD HAVE

### User Goal
Personalize boards, tokens, dice, and profiles with collected visual themes.

### Functional Requirements
- **Cosmetic Categories:**
  - **Dice Skins:** Custom 3D visuals, textures, pip styles, and roll trail particles.
  - **Token Skins:** Custom pawn silhouettes, models, and color trims.
  - **Board Themes:** Custom visual boards (Kathmandu Valley, Space Galaxy, Pirate Cove, Minimalist Slate).
  - **Profile Frames:** Custom borders surrounding player avatars.
  - **Victory Banners:** Celebratory animations on match wins.
- **Strict Aesthetic Purity:** Cosmetics must never alter dice probabilities, speed up token movement, or grant gameplay benefits.
- **Catalog Management:** Catalog definitions stored in a local asset registry for offline access.

### Important Edge Cases
- **Asset Fallback:** If a custom cosmetic asset fails to decode or load, the engine reverts to the default classic asset without crashing.
- **Opponent Cosmetics:** In local multiplayer, different human players can equip different unlocked dice or token skins from the device inventory.

### Offline vs. Online Behavior
- **Offline:** Unlocked items accessible and equipable at all times.
- **Online:** Unlocked cosmetic ownership records backed up to cloud profile.

### Dependencies
- Asset Manager & Design System Tokens.

### Acceptance Criteria
- Equipping any cosmetic skin alters visual appearance without modifying engine game state or calculations.

---

## 15. Online Multiplayer

### Priority: SHOULD HAVE (Post-MVP Phase)

### User Goal
Match against global human opponents in real-time online board game matches.

### Functional Requirements
- **Server Authority:** The game server acts as the single source of truth for online matches. Clients send command intents (`ROLL_DICE`, `SELECT_TOKEN`); server validates turns and broadcasts state deltas.
- **Randomness Authority:** All dice values generated on the server using cryptographic pseudo-random number generators.
- **Turn Clock:** Authoritative 15-second turn timer. Displays as a circular visual countdown on client HUDs.
- **Quick Matchmaking:** Automatically matches 2 to 4 players by geographic region, latency, and skill rating (MMR).
- **Match Lifecycle:** LOBBY -> MATCH_START -> IN_PROGRESS -> COMPLETED -> REWARD_SUMMARY.

### Important Edge Cases
- **Player Disconnects:** 60-second grace period. An AI bot handles turns while player is disconnected.
- **Player Forfeit:** Player can tap "Forfeit". Remaining seats continue match; forfeiting player takes last place.
- **Server Desync:** Client periodically compares server sequence IDs; on mismatch, client requests a full snapshot.

### Offline vs. Online Behavior
- **Offline:** Disabled. UI displays "Connect to internet to play Online".
- **Online:** Full real-time bidirectional communication via secure WebSockets: TBD — requires architectural decision.

### Dependencies
- Online Game Server & Gateway (`core/network`).

### Acceptance Criteria
- Server successfully rejects forged moves or roll values sent from modified clients.
- Matches complete and allocate MMR adjustments accurately.

---

## 16. Private Rooms

### Priority: SHOULD HAVE (Post-MVP Phase)

### User Goal
Create private rooms and invite friends to play with custom rules using simple room codes.

### Functional Requirements
- **Room Creation:** Any online player can create a private room.
- **Room Code:** 6-character alphanumeric uppercase code (e.g. `H9X2K7`), excluding ambiguous characters (`0`, `O`, `1`, `I`).
- **Lobby Roster:** Displays 4 player slots, avatar previews, ready check status, and host indicator.
- **Host Controls:** Host selects game mode (Ludo, Snake & Ladder, Remix), turn timer duration, and custom rule toggles.
- **Slot Filling:** Host can add AI bots to empty seats if friends are unavailable.

### Important Edge Cases
- **Host Drops:** If the room host disconnects in the lobby, host privileges migrate automatically to the next seated player.
- **Invalid Room Code:** User entering an expired or non-existent code receives an inline error message ("Room not found").

### Offline vs. Online Behavior
- **Offline:** Disabled.
- **Online:** Managed via room gateway service.

### Dependencies
- Room Coordinator Service.

### Acceptance Criteria
- Players can join an active room within 2 seconds of entering a valid 6-character code.

---

## 17. Friends

### Priority: COULD HAVE

### User Goal
Track online friends, check their availability, and send direct match invites.

### Functional Requirements
- Unique Player Tag system (e.g. `Aarav#4821`).
- Friends list displaying: Display name, avatar, online status (Online, In Game, Offline).
- In-app friend requests: Send, accept, decline, block.
- Direct Room Invite: Send push notification or in-app pop-up inviting friend directly into a private room.

### Important Edge Cases
- **Invite Spam:** Rate-limit room invites to 1 invite per 30 seconds per friend.
- **Blocked Users:** Blocked users cannot view presence or send invites.

### Offline vs. Online Behavior
- **Offline:** Friends tab shows cached list with "Offline" status or offline banner.
- **Online:** Real-time presence updates via presence service.

### Dependencies
- Social & Presence Gateway.

### Acceptance Criteria
- Friend invites successfully direct invited users into the target room lobby upon acceptance.

---

## 18. Profiles

### Priority: MUST HAVE (Local Profile), SHOULD HAVE (Cloud Sync)

### User Goal
Track career statistics, win rates, achievements, and equipped cosmetics.

### Functional Requirements
- **Local Profile (MVP):**
  - Generated on first app launch with default name "Player" and default avatar.
  - Fully editable offline (display name, avatar selection, equipped cosmetics).
  - Career Stats: Matches played, wins, win percentage, total tokens captured, sixes rolled.
- **Online Profile (Post-MVP):**
  - Optional account linking (Google Play Games, Apple Game Center, Email).
  - Synchronizes career stats, level, and cosmetic inventory across multiple devices.

### Important Edge Cases
- **Guest-to-Linked Upgrade:** When linking an anonymous guest account to Google/Apple, local cosmetics and stats migrate without loss.
- **Conflicting Cloud State:** Server-side timestamp resolution handles multi-device merge conflicts: TBD — requires architectural decision.

### Offline vs. Online Behavior
- **Offline:** All profile updates write to local SQLite/DataStore.
- **Online:** Profile mutations synchronize with backend REST API.

### Dependencies
- Profile Database (`core/database`).

### Acceptance Criteria
- Local profile persists reliably across app updates, device restarts, and process death.

---

## 19. Match History

### Priority: SHOULD HAVE

### User Goal
Review past match outcomes, placements, opponents, and dates.

### Functional Requirements
- Retains records of the last 50 completed matches.
- Details per record: Date/time, game mode, player count, final placement (1st, 2nd, 3rd, 4th), turns played, tokens captured, and rewards earned.
- Filterable by game mode: All, Ludo, Snake & Ladder, Remix.

### Important Edge Cases
- **Storage Truncation:** Records beyond 50 are automatically pruned locally to conserve device storage, while aggregate career stats remain incremented.

### Offline vs. Online Behavior
- **Offline:** Stores local matches in local SQLite database.
- **Online:** Synchronizes online match records with the backend match repository.

### Dependencies
- Room Database (`core/database/MatchHistoryDao`).

### Acceptance Criteria
- Every completed match writes a history entry within 200ms of match termination.

---

## 20. Leaderboards

### Priority: COULD HAVE

### User Goal
Compare competitive skills against global, regional, and friend circles.

### Functional Requirements
- Global Top 100 Leaderboard based on seasonal MMR / trophies.
- Friends Leaderboard based on total wins or current season points.
- Seasonal Reset: Monthly competitive seasons with badges awarded to top finishers.

### Important Edge Cases
- **Offline Viewing:** Displays the most recently cached leaderboard with an explicit "Cached" timestamp indicator.
- **Anti-Cheat Pruning:** Cheaters flagged by server auditing are excised from leaderboard standings before seasonal rewards distribute.

### Offline vs. Online Behavior
- **Offline:** Read-only view of cached leaderboard data; no network fetch attempted.
- **Online:** Fetches latest paginated standings from REST API.

### Dependencies
- Leaderboard Service & Redis/Database backend: TBD — requires architectural decision.

### Acceptance Criteria
- Ranked match victories update leaderboard scores accurately upon server match finalization.

---

## 21. Events

### Priority: COULD HAVE

### User Goal
Participate in limited-time weekend challenges and seasonal events for unique cosmetics.

### Functional Requirements
- **Weekend Chaos:** Limited-time Remix queues featuring experimental modifiers.
- **Themed Festivals:** Seasonal board designs and special quest lines celebrating festivals.
- Event tracking banner on the Home Dashboard.

### Important Edge Cases
- **Event Expiration Mid-Match:** If an event expires while a player is in an active match, the player is allowed to complete the match and receive event rewards.

### Offline vs. Online Behavior
- **Offline:** Unconnected users can play pre-bundled offline challenge modes if included in the client build.
- **Online:** Dynamic event rules and schedules download via remote config.

### Dependencies
- Remote Config & Scheduling Service.

### Acceptance Criteria
- Event rewards unlock strictly when the documented event criteria are verified.

---

## 22. Advertising

### Priority: MUST HAVE (Architecture & Offline Guarantee), SHOULD HAVE (Ad Placements)

### User Goal
Enjoy uninterrupted games, with clear transparency that ads only exist to support the free online game.

### Strict Monetization Rules
1. **OFFLINE GUARANTEE:** Advertisements must never appear, load, or initialize while the user is offline.
2. **ZERO IN-GAME INTERRUPTIONS:** Advertisements must never trigger during an active game turn, dice roll, or token move.
3. **NO PAY-TO-WIN:** Ads cannot be watched to buy rerolls, extra turns, or unfair game advantages.

### Ad Placements
- **Home Banner Ad:** Displayed at the bottom of the Home Dashboard only when connectivity is confirmed.
- **Post-Match Interstitial:** Displayed strictly on the match completion screen after victory fanfares finish.
  - Cooldown: Minimum 8 minutes and at least 2 completed matches between interstitials.
  - Suppression: Never shown to new users during their first 3 app sessions.
- **Rewarded Video Ad:** 100% voluntary opt-in. Watched exclusively for soft currency (Coins) or daily quest rerolls.
  - Rate limit: Maximum 5 rewarded videos per 24 hours.

### Important Edge Cases
- **Mid-Session Network Drop:** If network disconnects while on home screen, active banner ad dismisses cleanly.
- **Ad Fails to Load:** If rewarded video fails to load, UI informs the player cleanly without hanging or withholding other features.
- **Ad-Free IAP:** Purchasing "Remove Ads" permanently eliminates all banner and interstitial ads while keeping voluntary rewarded ads available.

### Offline vs. Online Behavior
- **Offline:** Ad SDK is dormant; zero ad views rendered; zero ad requests sent.
- **Online:** Ads served according to frequency limits.

### Dependencies
- `AdProvider` abstraction interface (`core/ads`). Specific SDK: TBD — requires architectural decision.

### Acceptance Criteria
- Zero ad SDK calls recorded during automated offline test runs.
- Interstitial ads strictly honor the 8-minute cooldown across app restarts.

---

## 23. Connectivity Handling

### Priority: MUST HAVE

### User Goal
Experience seamless transitions between online and offline states without crashes, hangs, or modal error spam.

### Functional Requirements
- Non-blocking network status monitoring using Android `ConnectivityManager` callbacks.
- **No Modal Spam:** The app must never display blocking popups stating "Connection Lost" or "Internet Required" when the user is playing offline.
- Status Indicator: A subtle non-intrusive status indicator on the dashboard edge indicates "Offline Mode", "Connecting...", or "Connected".
- Offline mode is treated as a natural operating condition, not an error.

### Important Edge Cases
- **Transitioning from Online to Offline in Match:**
  - If in an offline/AI match: Zero interruption; game continues without stutter.
  - If in an online multiplayer match: Socket detects drop; HUD displays a 60-second reconnection banner while the local engine awaits signal.
- **Transitioning from Offline to Online:** Background worker checks for pending offline achievements and syncs non-sensitive profile metrics smoothly.

### Offline vs. Online Behavior
- **Offline:** All online buttons (Quick Match, Join Room) display disabled states or redirect to local play options with clear guidance.
- **Online:** All online options become interactive automatically.

### Dependencies
- Network Monitor Module (`core/network/NetworkMonitor`).

### Acceptance Criteria
- Switching the device into Airplane Mode during an active AI match produces zero frame drops or visual hitches.

---

## 24. Accessibility

### Priority: MUST HAVE

### User Goal
Enable players with visual, motor, or auditory impairments to play all games comfortably.

### Functional Requirements
- **Color Independence:** Color is never the sole indicator of player identity:
  - Player 1 (Red): Embossed Circle symbol.
  - Player 2 (Green): Embossed Triangle symbol.
  - Player 3 (Yellow): Embossed Diamond symbol.
  - Player 4 (Blue): Embossed Square symbol.
- **Color Blind Presets:** Palette adjustment modes for Protanopia, Deuteranopia, and Tritanopia.
- **Touch Target Sizing:** Minimum 48x48dp interactive bounding boxes for all clickable elements, tokens, and dice.
- **Dynamic Font Scaling:** Supports system typography scaling up to 200% without layout clipping.
- **Screen Reader Support:** Semantic accessibility tree with TalkBack content descriptions on board squares, tokens, and dice values.
- **Reduced Motion Mode:** Toggles off camera zooms, rapid shaking, and particle bursts, replacing them with instant step transitions.

### Important Edge Cases
- **Tokens on Crowded Squares:** When multiple tokens share a safe square, tapping expands a selection disambiguation sheet so motor-impaired players can easily choose the intended token.

### Offline vs. Online Behavior
- Identical accessibility features across both offline and online modes.

### Dependencies
- Android Accessibility Framework & Jetpack Compose Semantics.

### Acceptance Criteria
- TalkBack navigation can traverse the entire game board and execute a complete turn cycle without sighted assistance.

---

## 25. Privacy

### Priority: MUST HAVE

### User Goal
Enjoy board games without forfeiting personal data or being tracked across applications.

### Functional Requirements
- **Zero Mandatory Registration:** Offline play requires zero personal data (no email, phone number, name, or location).
- **Anonymous Guest IDs:** Initial online play uses random device UUIDs without requesting hardware IMEIs or MAC addresses.
- **Compliance:** Full compliance with Google Play Families Policy, COPPA, GDPR, and CCPA.
- **Consent Management:** Online ad initialization is gated behind a certified Consent Management Platform (CMP).
- **Data Export & Deletion:** Settings screen provides a one-tap "Delete All Local Data" button and an account deletion request for linked cloud profiles.

### Important Edge Cases
- **Children's Usage:** Ludora excludes interest-based behavioural tracking for underage users based on age-gate selection.

### Offline vs. Online Behavior
- **Offline:** Zero bytes of analytical or personal data leave the device.
- **Online:** Minimal telemetry strictly for crash diagnostics and anti-cheat verification.

### Dependencies
- Consent Management SDK: TBD — requires architectural decision.

### Acceptance Criteria
- App passes automated privacy audits confirming zero outbound network calls prior to user consent.

---

## 26. Security Requirements

### Priority: MUST HAVE

### User Goal
Compete in online matches knowing the game cannot be compromised by opponents using modified APKs or packet injection tools.

### Functional Requirements
- **Server Authority:** The server validates all game actions. Clients cannot dictate roll values, coordinates, or win triggers.
- **Secure RNG:** Offline rolls use `java.security.SecureRandom`. Online rolls use server-side cryptographic PRNG.
- **Transport Security:** All network requests and WebSocket streams require TLS 1.3 with certificate pinning: TBD — requires architectural decision.
- **Ad Callback Verification:** Rewarded ad rewards require Server-Side Verification (SSV) with cryptographic signature validation.
- **Secure Local Storage:** Sensitive preference tokens stored using Android EncryptedSharedPreferences (backed by Android Keystore).

### Important Edge Cases
- **Memory Tampering:** In offline play, memory modifications (e.g. via GameGuardian) only affect the local device and cannot corrupt online leaderboards or server matches.

### Offline vs. Online Behavior
- **Offline:** Cryptographic local RNG; tamper-evident local save state verification.
- **Online:** Full server-side input validation, rate limiting, and replay attack prevention.

### Dependencies
- Android Keystore, TLS 1.3, Server Validation Engine.

### Acceptance Criteria
- Penetration tests confirm arbitrary client-crafted WebSocket messages cannot force illegal moves or fake dice rolls.

---

## 27. Performance Requirements

### Priority: MUST HAVE

### User Goal
Experience instant launches, smooth animations, and long battery life without device overheating.

### Functional Requirements
- **Frame Rate:** Consistent 60fps (120fps on high-refresh panels) with zero dropped frames during token movement and dice animations.
- **Startup Time:** Cold start to interactive home screen in < 1500ms on mid-range devices; < 2500ms on low-end hardware.
- **Memory Budget:** App heap footprint must not exceed 120MB during active matches.
- **Battery Efficiency:** Continuous 30-minute local play session consumes < 5% battery on standard 4,000mAh hardware.
- **Storage Footprint:** Initial APK download size < 25MB. Total on-device storage footprint < 60MB.
- **Hardware Tier Baseline:** Smooth operation on 2GB RAM devices running Android 8.0 (API 26).

### Important Edge Cases
- **Low Memory Warning:** App listens to `onTrimMemory` and purges non-essential bitmap caches immediately.

### Offline vs. Online Behavior
- **Offline:** Zero CPU wake-locks or background threads when app is minimized.
- **Online:** Network buffers compressed to keep packet payloads < 256 bytes.

### Dependencies
- Jetpack Compose Hardware-Accelerated Canvas & Baseline Profiles.

### Acceptance Criteria
- Automated macrobenchmarks confirm cold startup under 1500ms and zero Jank frames (>16ms) during gameplay animations.

---

## 28. Analytics Requirements

### Priority: SHOULD HAVE (Online only)

### User Goal
Enjoy a stable game where crashes and bugs are discovered and fixed rapidly without invading user privacy.

### Functional Requirements
- Crash Reporting: Anonymous crash stack traces and non-fatal exception tracking.
- Product Telemetry (Online sessions only):
  - Game mode popularity (Ludo vs Snake vs Remix).
  - Average match duration.
  - Drop-off points in onboarding.
  - Ad display counts and frequency limit verifications.
- Zero Offline Logging: Telemetry events must never queue indefinitely offline to prevent storage bloat.

### Important Edge Cases
- **User Opt-Out:** Settings contains an explicit "Crash Reporting & Diagnostics" toggle. When disabled, zero telemetry transmits.

### Offline vs. Online Behavior
- **Offline:** Analytics reporting disabled.
- **Online:** Transmits diagnostic batches over HTTPS.

### Dependencies
- Analytics & Crash SDK: TBD — requires architectural decision.

### Acceptance Criteria
- Disabling analytics in Settings immediately halts all diagnostic network transmissions.

---

## 29. Error Handling

### Priority: MUST HAVE

### User Goal
Encounter clear, friendly error recovery without cryptic error codes or game crashes.

### Functional Requirements
- **Graceful Engine Recovery:** If an invalid move command is submitted, the engine logs the error, discards the command, and retains the active turn state.
- **UI Toast & Banners:** Errors display as non-blocking toasts or status banners rather than modal interruption dialogs.
- **Network Timeouts:** Failed room joins display clear actionable messages: "Room K7X92P is full" or "Room code expired".
- **Corrupt Save Recovery:** If a saved match state file is corrupted, the app discards the corrupt state cleanly and presents the main menu rather than crashing on boot.

### Important Edge Cases
- **Simultaneous Disconnect & Timeout:** Handled by server state coordinator without orphaning game rooms.

### Offline vs. Online Behavior
- **Offline:** Errors resolved locally with safe fallbacks.
- **Online:** Gateway returns structured JSON error responses (`code`, `message`).

### Dependencies
- Standardized Error Envelope (`core/common/Result`).

### Acceptance Criteria
- Zero unhandled crash exceptions across any combination of network loss or corrupted storage files.

---

## 30. MVP Scope
The MVP focuses strictly on delivering a rock-solid, polished offline board game before introducing online server infrastructure.

### Included in MVP
- Complete offline Ludo game engine with 100% rule enforcement.
- Complete offline Snake & Ladder game engine (100-square grid).
- Foundation Remix Mode (Ludo with snake and ladder hazard squares).
- Single-player AI opponents across 3 difficulty tiers (Easy, Medium, Hard).
- Local pass-and-play multiplayer on a single device for 2 to 4 players.
- Local player profile, stats tracking, and save/resume match state.
- Offline soft currency (Coins) and local progression XP.
- Core design system in Jetpack Compose (Material 3 tokens, dark mode, high contrast).
- Full accessibility baseline (WCAG AA contrast, touch targets, color-blind shape markers).
- Zero advertisements and zero mandatory logins.

### Excluded from MVP (Deferred to Post-MVP)
- Online multiplayer (Quick Match and Private Rooms).
- Room codes and friend lists.
- Cloud account sync (Google/Apple linking).
- Advertisement integrations.
- Advanced custom rule creator.
- Global and seasonal leaderboards.

---

## 31. Future Scope
Post-MVP releases will expand Ludora systematically across subsequent roadmap phases:
- **Phase 7 & 8:** Online multiplayer infrastructure, WebSockets, server-authoritative engine, 6-character private rooms, and friend invites.
- **Phase 9:** Online-only monetization (Home banner, post-game interstitial with frequency limits, optional rewarded ads, Ad-Free IAP).
- **Phase 10 & 11:** Soundscapes, haptic fine-tuning, seasonal competitive ladders, and community tournaments.
- **Expansion Games:** Additional traditional games built on the Ludora engine (Carrom, Chess, Checkers).
- **User-Generated Rule Studio:** Allow players to configure, save, and share custom Remix rule combinations.

---

## 32. Acceptance Criteria
The Ludora implementation is verified and ready for production when:
1. **Offline Autonomy:** The app launches, plays all MVP modes (Ludo, Snake & Ladder, Remix, AI, Pass & Play), and completes full matches in Airplane Mode with zero network calls.
2. **Deterministic Rules:** Automated unit test suite achieves 90%+ branch coverage across all movement, capture, hazard, and win conditions.
3. **No Ad Intrusion:** Automated testing confirms zero ads display during offline play and zero ads appear during active player turns online.
4. **Data Integrity:** Local match state resumes accurately after device process termination.
5. **Performance Standards:** 60fps maintained during animations on 2GB RAM Android 8.0 devices; cold startup under 1500ms.
6. **Accessibility Compliance:** All interactive touch targets measure >= 48x48dp, and all player colors pair with unique embossed shape markers.
