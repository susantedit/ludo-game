# Game Rules Specification - Ludora

Ruleset Identifiers:
- `LUDO_CLASSIC_V1`
- `SNAKE_LADDER_CLASSIC_V1`
- `REMIX_HAZARDS_V1`

---

## 1. Ludora Game Rules Principles

The Ludora game engine operates according to seven core principles:
1. **Absolute Determinism:** Given identical starting state, game ruleset configuration, and input actions, the game engine produces identical output states every time.
2. **Strict UI and Presentation Decoupling:** Presentation layers (Jetpack Compose, Canvas renderers, sound players, animation controllers) contain zero game rule logic. UI components submit player actions and observe engine states.
3. **Engine Authority for Legal Moves:** The game engine computes all legal moves. The UI queries the engine for valid move options and displays them. The engine rejects illegal actions.
4. **Isolated Randomness:** Random generation (dice rolls, card draws) is isolated from state transition functions. Random values are passed into state mutators as deterministic inputs.
5. **Universal Rule Parity:** Core rule logic is identical offline and online. An offline AI match and an online ranked match evaluate legal moves and victory conditions with identical engine code.
6. **Untrusted Client Model:** In online multiplayer, client devices never determine dice values, legal moves, captures, or victories. The authoritative server engine executes all transitions.
7. **Complete Explainability and Testability:** Every state transition emits structured event logs explaining why a token moved, captured, climbed, dropped, or won. Every rule is verifiable with headless unit tests.

---

## 2. Ludo Specifications

### 2.1 Players and Seat Allocations
- **Player Capacity:** Minimum 2 players, maximum 4 players.
- **Player Colors:** Red, Green, Yellow, Blue.
- **Seat Indices:**
  - Seat 0: Red (Starting quadrant: Top-Left)
  - Seat 1: Green (Starting quadrant: Top-Right)
  - Seat 2: Yellow (Starting quadrant: Bottom-Right)
  3. Seat 3: Blue (Starting quadrant: Bottom-Left)
- **Token Count:** Exactly 4 tokens per player (Total 16 tokens on board).
- **Player Identifiers:** Each participant is uniquely identified by `playerId` (UUID or synthetic string) and assigned to a `seatIndex` (0 to 3).
- **Turn Order:** Strictly clockwise: Seat 0 (Red) -> Seat 1 (Green) -> Seat 2 (Yellow) -> Seat 3 (Blue) -> Seat 0.
  - In a 2-player match, players occupy opposing seats (Seat 0 Red vs Seat 2 Yellow) or adjacent seats depending on room configuration. Standard default is opposing seats (Red vs Yellow).
  - In a 3-player match, play cycles through active seats (e.g. Seats 0, 1, 2), skipping unoccupied Seat 3.

---

## 3. Ludo Board Architecture

The logical board is completely decoupled from visual screen coordinates.

### 3.1 Logical Track Layout
The board consists of three distinct positional zones:
1. **Base Zones (Home Bases):**
   - 4 bases (Red, Green, Yellow, Blue).
   - Each base holds 4 slots: `BASE_0`, `BASE_1`, `BASE_2`, `BASE_3`.
2. **Main Common Track (Circuits):**
   - Exactly 52 continuous perimeter steps, indexed `0` through `51` in clockwise order.
   - Shared by all players.
3. **Colored Home Paths:**
   - 4 independent home paths (one per color).
   - Each home path consists of 5 linear steps: `HOME_PATH_1`, `HOME_PATH_2`, `HOME_PATH_3`, `HOME_PATH_4`, `HOME_PATH_5`.
   - Accessible only by tokens of the matching player color.
4. **Center Home Goals:**
   - 4 goal destinations: `GOAL_RED`, `GOAL_GREEN`, `GOAL_YELLOW`, `GOAL_BLUE`.
   - Reaching this position transitions a token into the `FINISHED` state.

### 3.2 Key Track Indices

| Player Color | Seat Index | Start Track Step | Home Entrance Step | Home Path Steps | Center Goal |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Red** | 0 | Step 0 | Step 50 | Red H1 - H5 | Red Goal |
| **Green** | 1 | Step 13 | Step 11 | Green H1 - H5 | Green Goal |
| **Yellow** | 2 | Step 26 | Step 24 | Yellow H1 - H5 | Yellow Goal |
| **Blue** | 3 | Step 39 | Step 37 | Blue H1 - H5 | Blue Goal |

- **Step Progression Formula:** Tokens advance along the main track by incrementing their index: `(currentIndex + 1) % 52`.
- **Home Entrance Transition:** When a token reaches its specific Home Entrance Step (e.g. Step 50 for Red) and has remaining roll steps, it turns into its colored Home Path step `H1` instead of continuing to Step 51.

### 3.3 Safe Squares
The board contains exactly 8 safe squares on the common track:
- **4 Start Squares:** Step 0 (Red Start), Step 13 (Green Start), Step 26 (Yellow Start), Step 39 (Blue Start).
- **4 Star Squares:** Step 8, Step 21, Step 34, Step 47 (located 8 steps forward from each starting square).

---

## 4. Token States

Every token exists in exactly one of four discrete states at any point during a match:

```text
       Roll 6
[IN_BASE] ───> [ON_TRACK] ───> [IN_HOME_PATH] ───> [FINISHED]
    ▲              │
    └──────────────┘
        Captured
```

### 4.1 State Definitions

#### 1. `IN_BASE`
- **Definition:** Token rests inside its colored base quadrant.
- **Entry:** Match initialization, or when captured by an opponent on the main track.
- **Exit:** Requires the player to roll a 6 and execute a `RELEASE_TOKEN` action.
- **Legal Actions:** Can be released if rolled value is 6.
- **Illegal Actions:** Cannot move forward along track steps; cannot capture; cannot be captured.

#### 2. `ON_TRACK`
- **Definition:** Token navigates the 52-step common perimeter circuit.
- **Entry:** Released from `IN_BASE` onto player's start step.
- **Exit:** Moves into `IN_HOME_PATH` upon passing Home Entrance step, or captured back to `IN_BASE`.
- **Legal Actions:** Advance forward by exact rolled dice value (1 through 6).
- **Illegal Actions:** Cannot move backward; cannot jump directly to center goal.

#### 3. `IN_HOME_PATH`
- **Definition:** Token navigates its dedicated 5-step colored home stretch.
- **Entry:** Advanced forward from Home Entrance step.
- **Exit:** Advanced into Center Goal upon exact required roll.
- **Legal Actions:** Advance forward toward Center Goal if roll is less than or equal to remaining steps.
- **Illegal Actions:** Cannot be captured (opponents cannot enter); cannot exit back onto the main track; cannot move if roll exceeds distance to Center Goal.

#### 4. `FINISHED`
- **Definition:** Token has successfully entered the Center Goal.
- **Entry:** Moved from `IN_HOME_PATH` step into Center Goal on exact roll.
- **Exit:** None. State is immutable for the remainder of the match.
- **Legal Actions:** None.
- **Illegal Actions:** Cannot move, roll, or be interacted with.

---

## 5. Dice Mechanics

### 5.1 Dice Parameters
- **Quantity:** Exactly one (1) 6-sided cubic die.
- **Range:** Discrete integer values from 1 to 6 inclusive: `{1, 2, 3, 4, 5, 6}`.
- **Distribution:** Uniform discrete probability: $P(X = k) = \frac{1}{6}$ for $k \in \{1, 2, 3, 4, 5, 6\}$.

### 5.2 Bonus Roll Rules
A player receives exactly one bonus roll immediately following an action if:
1. The rolled dice value is **6**.
2. The moved token **captures** an opponent token.
3. The moved token reaches the Center Goal (`FINISHED`), **provided** this is not the 4th token that wins the game.

Bonus rolls are cumulative in sequence (e.g. rolling 6, then rolling 6 again grants a second bonus roll).

### 5.3 Three Consecutive Sixes Penalty
- If a player rolls a **6**, they move and roll again (Roll 2).
- If Roll 2 is a **6**, they move and roll again (Roll 3).
- If Roll 3 is a **6**:
  - The third roll of 6 is **forfeited immediately**.
  - No movement is permitted for the third roll.
  - Valid movements executed on Roll 1 and Roll 2 **remain valid and are not revoked**.
  - The player's turn terminates immediately, and play advances to the next player clockwise.

### 5.4 No Legal Moves
If a player rolls a value where zero legal moves exist across all 4 tokens:
- The engine emits a `NO_LEGAL_MOVES` event.
- The turn terminates, and play advances to the next player clockwise after a 1000ms visual notification delay.

---

## 6. Token Release from Base

### 6.1 Requirements
- Token must be in `IN_BASE` state.
- Player must have rolled an active dice value of **6**.

### 6.2 Execution
- Action: `RELEASE_TOKEN(tokenId)`.
- The token transitions from `IN_BASE` to `ON_TRACK`.
- Destination: The player's designated Starting Step (Red: 0, Green: 13, Yellow: 26, Blue: 39).
- Releasing a token consumes the rolled value of 6.

### 6.3 Starting Square Occupancy on Release
- **Unoccupied:** Token settles onto the start square.
- **Occupied by Opponent:** The opponent token is **captured** and returned to its owner's base. The releasing player earns a bonus roll for the capture.
- **Occupied by Own Token:** Both tokens co-exist on the start square (start squares are permanent safe squares).
- **Player Choice:** If a player has a token in base and an active token on the track, rolling a 6 allows the player to choose whether to release a new token or advance an existing active token.

---

## 7. Movement Mechanics

### 7.1 Forward Movement
- Tokens always move forward in a clockwise direction. Backward movement is strictly prohibited in classic rules.
- A token advances along the track by the exact integer value of the rolled die.
- Movement step calculation on main track: `(currentPosition + roll) % 52`.

### 7.2 Path Traversal and Passing
- Tokens hop from square to square along the defined path.
- Tokens can pass through (hop over) any other tokens (friendly or opponent) situated on intermediate track steps. Intermediate tokens are unaffected by a token hopping past them.

### 7.3 Token Selection Rules
- If **multiple tokens** can legally move, the engine requires the player to select which token to advance.
- If **exactly one token** can legally move:
  - If user configuration `auto_move_single_token` is enabled, the engine auto-executes the move.
  - If disabled, the engine highlights the token and awaits user confirmation tap.

---

## 8. Capture Mechanics

### 8.1 Capture Trigger Condition
A capture occurs if and only if:
1. A player's token concludes its move on a main track step `S`.
2. Step `S` is **not a safe square** (neither a start square nor a star square).
3. Step `S` is currently occupied by one or more opponent tokens.

### 8.2 Capture Resolution Sequence
1. The opponent token on square `S` is removed from `ON_TRACK` and returned to `IN_BASE` of its owner.
2. The capturing player's token settles onto square `S`.
3. The engine emits a `TOKEN_CAPTURED` event.
4. The capturing player receives **one (1) bonus roll**.

### 8.3 Friendly Token Interaction
- A player's token cannot capture their own token.
- Landing on a non-safe square occupied by a friendly token is governed by Section 10 (Stacking / Co-existence).

---

## 9. Safe Squares

### 9.1 Dedicated Safe Squares
There are exactly eight (8) safe squares on the board:
- `Step 0` (Red Start)
- `Step 8` (Red Star)
- `Step 13` (Green Start)
- `Step 21` (Green Star)
- `Step 26` (Yellow Start)
- `Step 34` (Yellow Star)
- `Step 39` (Blue Start)
- `Step 47` (Blue Star)

### 9.2 Safe Square Rules
1. **Immunity from Capture:** No token situated on a safe square can be captured under any circumstances in classic mode.
2. **Multi-Player Co-existence:** Multiple tokens belonging to different players can occupy the same safe square simultaneously without conflict.
3. **Multi-Friendly Co-existence:** Multiple tokens belonging to the same player can occupy the same safe square simultaneously.

---

## 10. Stacking and Block Rules

### 10.1 MVP Decision: Co-existence Without Barriers
To prevent game-stalling deadlocks and ensure smooth digital gameplay:
- **No Impassable Barriers:** Two friendly tokens on the same square **do not** form an impassable wall or barrier. Opponent tokens can pass over them freely.
- **Safe Squares:** Unlimited friendly and opponent tokens can share a safe square.
- **Regular Squares:**
  - Multiple friendly tokens may occupy the same regular square: **Prohibited in MVP**.
  - Rule: If a move would cause a token to land on a non-safe square already occupied by a friendly token, that move is **illegal** and rejected by the engine.
  - Exception: If all possible moves land on friendly tokens or are otherwise invalid, the player has no legal moves and passes turn.

---

## 11. Home Path Mechanics

### 11.1 Entry Condition
- A token enters its colored Home Path when its forward movement steps carry it past its designated Home Entrance Step.
- Example for Red (Entrance: Step 50): Token at Step 48 rolls 4:
  - Step 48 -> Step 49 (1 step)
  - Step 49 -> Step 50 (2 steps)
  - Step 50 -> Home Path H1 (3 steps)
  - Home Path H1 -> Home Path H2 (4 steps)
  - Token settles on `Red H2`.

### 11.2 Home Path Rules
1. **Exclusive Access:** Only tokens of the matching color can enter their home path. Opponents cannot enter.
2. **Zero Captures:** Captures cannot occur within the Home Path.
3. **No Over-Rolls:** A token cannot move if the roll exceeds the steps remaining to the Center Goal. The token remains stationary.

---

## 12. Finishing Tokens

### 12.1 Exact Roll Requirement
- Entering the Center Goal (`FINISHED`) requires an **exact roll**.
- Steps remaining calculation:
  - From `H1`: Requires roll of 5.
  - From `H2`: Requires roll of 4.
  - From `H3`: Requires roll of 3.
  - From `H4`: Requires roll of 2.
  - From `H5`: Requires roll of 1.
- If the rolled value exceeds the exact distance to the Center Goal, the move is invalid and the token cannot be moved.

### 12.2 Resolution
- Token state updates to `FINISHED`.
- The token is permanently removed from the active board.
- The player receives a bonus roll, unless this was the 4th token that wins the game.

---

## 13. Win Conditions and Match Conclusion

### 13.1 Winner Determination
- The first player to transition all four (4) tokens into the `FINISHED` state is declared the **1st Place Winner**.

### 13.2 Match Continuation (Multi-Placements)
- **2-Player Matches:** The match terminates immediately when Player 1 finishes all 4 tokens. The remaining player is awarded 2nd Place.
- **3-Player and 4-Player Matches:**
  - When the 1st place winner finishes, play continues for remaining players.
  - The second player to finish all 4 tokens is awarded **2nd Place**.
  - The third player to finish all 4 tokens is awarded **3rd Place**.
  - The final remaining player is assigned **4th Place**, and the match concludes.

---

## 14. Draw and Deadlock Prevention

### 14.1 Classical Non-Draw Rule
In standard gameplay, Ludo has no formal draw condition; tokens continuously cycle until players finish.

### 14.2 Turn Limit Cutoff (Anti-Stall Defense)
To prevent infinite games caused by griefing or passive AI play in online or automated sessions:
- **Maximum Round Limit:** A match has a hard ceiling of **200 rounds** (where 1 round = all active seats complete one turn cycle).
- If round 200 completes without a full finish:
  - The match concludes immediately with a `TIME_LIMIT_REACHED` event.
  - Standings are ranked by:
    1. Highest number of `FINISHED` tokens.
    2. Highest number of tokens in `IN_HOME_PATH`.
    3. Smallest total remaining distance to Center Goal across all 4 tokens.

---

## 15. Turn Lifecycle State Machine

```text
[TURN_START]
     │
     ▼
[WAITING_FOR_ROLL] ──(Roll Dice Action)──> [DICE_ROLLED]
                                                 │
                                                 ▼
                                     [EVALUATE_LEGAL_MOVES]
                                                 │
                   ┌─────────────────────────────┴─────────────────────────────┐
                   ▼                                                           ▼
         [LEGAL MOVES EXIST]                                          [ZERO LEGAL MOVES]
                   │                                                           │
                   ▼                                                           ▼
    [WAITING_FOR_TOKEN_SELECTION]                                       [TURN_FORFEITED]
                   │                                                           │
                   ▼ (Select Token)                                            │
            [RESOLVE_MOVE]                                                     │
                   │                                                           │
                   ▼                                                           │
       [RESOLVE_CAPTURE_OR_HAZARD]                                             │
                   │                                                           │
                   ▼                                                           │
         [CHECK_WIN_CONDITION]                                                 │
                   │                                                           │
       ┌───────────┴───────────┐                                               │
       ▼                       ▼                                               │
  [GAME_OVER]          [CHECK_BONUS_ROLL]                                      │
                               │                                               │
                   ┌───────────┴───────────┐                                   │
                   ▼                       ▼                                   ▼
          [BONUS_ROLL_EARNED]     [NO_BONUS_ROLL] ───────────────────> [ADVANCE_TURN]
                   │                                                           │
                   └───────────────────────────────────────────────────────────┘
```

### 15.1 State Machine Phases
1. `TURN_START`: Engine sets active player seat, starts turn timer (15 seconds online; unlimited offline).
2. `WAITING_FOR_ROLL`: Engine awaits `ROLL_DICE` action from active seat.
3. `DICE_ROLLED`: Random value (1-6) resolved and verified against consecutive six counter.
4. `EVALUATE_LEGAL_MOVES`: Engine computes legal target tokens. If 0 legal moves exist, transitions to `ADVANCE_TURN`.
5. `WAITING_FOR_TOKEN_SELECTION`: Player selects token. If single move exists and auto-move enabled, executes immediately.
6. `RESOLVE_MOVE`: Token transitions square-by-square to destination.
7. `RESOLVE_CAPTURE_OR_HAZARD`: Checks landing square. If opponent present on non-safe square, executes capture and sets bonus flag.
8. `CHECK_WIN_CONDITION`: Verifies if moving player reached 4 finished tokens.
9. `CHECK_BONUS_ROLL`: If roll was 6, capture occurred, or token finished (non-game-ending), returns to `WAITING_FOR_ROLL`. Otherwise transitions to `ADVANCE_TURN`.
10. `ADVANCE_TURN`: Resets consecutive six counter, sets next clockwise player, and triggers `TURN_START`.

---

## 16. Offline Pause and State Persistence

### 16.1 Save Triggers
The engine serializes the complete logical game state to local storage when:
- App lifecycle triggers `onPause` or `onStop`.
- Any turn transition completes.
- Device reports low battery or impending OS termination.

### 16.2 Resume Guarantee
- When the user relaunches Ludora, if an uncompleted match exists in local storage:
  - Home dashboard presents an interactive "Resume Match" card.
  - Tapping "Resume" restores exact token coordinates, active player seat, dice state, and turn phase within 100ms.
  - Match history does not record an uncompleted game until the player explicitly taps "Abandon" or finishes play.

---

## 17. Snake & Ladder Specifications

### 17.1 Board Configuration
- **Dimensions:** 10 rows by 10 columns = 100 total squares numbered `1` through `100`.
- **Numbering Order:** Standard boustrophedon (alternating horizontal sequence):
  - Row 1 (Bottom): 1 (left) to 10 (right)
  - Row 2: 11 (right) to 20 (left)
  - Row 3: 21 (left) to 30 (right)
  - Row 10 (Top): 100 (left) to 91 (right)
- **Starting Position:** All player tokens begin off-board at virtual square `0`.
- **Victory Tile:** Exactly square `100`.

### 17.2 Exact Board Data Matrix (`SNAKE_LADDER_CLASSIC_V1`)

#### Ladders (7 Total)
- Ladder 1: Base `4` -> Top `14`
- Ladder 2: Base `9` -> Top `31`
- Ladder 3: Base `20` -> Top `38`
- Ladder 4: Base `28` -> Top `84`
- Ladder 5: Base `40` -> Top `59`
- Ladder 6: Base `51` -> Top `67`
- Ladder 7: Base `63` -> Top `81`

#### Snakes (8 Total)
- Snake 1: Head `17` -> Tail `7`
- Snake 2: Head `54` -> Tail `34`
- Snake 3: Head `62` -> Tail `19`
- Snake 4: Head `64` -> Tail `60`
- Snake 5: Head `87` -> Tail `24`
- Snake 6: Head `93` -> Tail `73`
- Snake 7: Head `95` -> Tail `75`
- Snake 8: Head `99` -> Tail `78`

### 17.3 Gameplay Rules
1. **Movement:** Players roll a single 6-sided die and advance their token forward by the rolled amount.
2. **Initial Entry:** A roll of any number advances the token from virtual square `0` onto that square (e.g. roll 3 lands on square 3). A roll of 6 is not required to enter the board.
3. **Ladders:** Landing exactly on a ladder base moves the token to the ladder top immediately.
4. **Snakes:** Landing exactly on a snake head moves the token down to the snake tail immediately.
5. **Pass-through:** Moving past a ladder base or snake head does not trigger any climb or fall.
6. **Co-existence:** Multiple tokens can occupy the same square simultaneously without captures.
7. **Bonus Roll:** Rolling a **6** grants one bonus roll (maximum 2 consecutive bonus rolls; a third 6 forfeits the turn).
8. **Finish Rule (Overshoot Stalls):** Landing on square 100 requires an **exact roll**. If a roll exceeds the steps needed to reach 100 (e.g. token at square 98 rolls 4), the token **cannot move** and stays at square 98.

---

## 18. Snake & Ladder Board Data Representation

Board structures are decoupled from UI rendering and modeled as immutable data:

```kotlin
data class BoardConfiguration(
    val rulesetId: String,
    val totalSquares: Int = 100,
    val ladders: Map<Int, Int>, // Base -> Top
    val snakes: Map<Int, Int>   // Head -> Tail
)
```

UI components query this model to draw rungs and serpentine vectors dynamically.

---

## 19. Snake & Ladder Special Rules & MVP Boundaries

- **Classic MVP Scope:** Strictly pure classic rules (no cards, no power-ups, no wagers).
- **Remix Scope:** Power cards (Shield, Boost, Reroll) and dynamic hazard tiles are strictly isolated in **Remix Mode** to preserve the classic game's purity.

---

## 20. Remix Mode Specifications

Remix Mode extends base game rules through modular modifier layers:

$$\text{ActiveRules} = \text{BaseRules} + \sum \text{Modifiers}$$

### 20.1 Core Remix Modifiers (MVP Foundation)

#### Hazard Circuit (Ludo + Hazards)
- 4 Snake Heads placed on Ludo common track: Steps `16`, `29`, `42`, `3`.
- Landing on a Snake Head drops the token backward 6 steps:
  - If it drops onto an opponent on an unstarred square, it captures the opponent token.
- 4 Ladder Bases placed on Ludo common track: Steps `6`, `19`, `32`, `45`.
  - Landing on a Ladder Base advances the token forward 6 steps.

#### Power Cards
- Earned upon capturing an opponent or landing on a designated mystery tile.
- Stored in player inventory (max 2 cards in hand).
- Usable before rolling dice:
  - **Shield Card:** Token is immune to captures and snakes for 2 full rounds.
  - **Boost Card:** Adds +2 to the upcoming dice roll.
  - **Reroll Card:** Allows re-rolling a 1 or 2 once per match.

---

## 21. Rule Precedence and Execution Order

When multiple rule triggers occur in a single step, the game engine evaluates them in strict deterministic order:

1. **Input Validation:** Verify action matches active player seat and current turn phase.
2. **Dice Outcome Evaluation:** Verify roll value; evaluate consecutive six count.
3. **Movement Calculation:** Compute candidate landing step.
4. **Track Boundary / Home Path Check:** Evaluate whether token transitions into Home Path or Center Goal.
5. **Tile Hazard Resolution (Remix only):** Apply snake retreat or ladder climb.
6. **Capture Resolution:** Check landing tile for opponent tokens. If unstarred and valid, trigger capture, return opponent to base, and flag bonus roll.
7. **Win Condition Check:** Evaluate whether active player has 4 tokens finished.
8. **Turn Continuation Check:** If win condition met, trigger victory. Else if bonus roll earned, transition to `WAITING_FOR_ROLL`. Else advance turn to next clockwise player.

---

## 22. Randomness Architecture

```text
[Random Source] ──(Integer 1..6)──> [Game Engine Transition] ──> [New State]
```

- **Offline Mode:** Uses `java.security.SecureRandom` on device.
- **Online Mode:** Authoritative game server generates CSPRNG values. Clients never generate, forecast, or transmit roll values.
- **State Determinism:** The engine function signature enforces purity:
  `reduce(currentState: GameState, action: GameAction, rollInput: Int): EngineResult`

---

## 23. Logical Game State Definitions

### 23.1 Ludo Logical State
```text
LudoGameState:
  matchId: String
  rulesetId: String ("LUDO_CLASSIC_V1")
  seats: List<PlayerSeat> (0..3)
  activeSeatIndex: Int
  turnPhase: TurnPhase
  consecutiveSixes: Int
  lastRollValue: Int?
  tokens: Map<TokenId, TokenRecord>:
    tokenId: String
    seatIndex: Int
    state: IN_BASE | ON_TRACK | IN_HOME_PATH | FINISHED
    trackPosition: Int (0..51, or home step 1..5)
  bonusRollEarned: Boolean
  rankings: List<Int> (Completed player seat indices)
  roundNumber: Int
  history: List<GameActionRecord>
```

### 23.2 Snake & Ladder Logical State
```text
SnakeGameState:
  matchId: String
  rulesetId: String ("SNAKE_LADDER_CLASSIC_V1")
  seats: List<PlayerSeat>
  activeSeatIndex: Int
  turnPhase: TurnPhase
  consecutiveSixes: Int
  lastRollValue: Int?
  playerPositions: Map<SeatIndex, Int> (0..100)
  winnerSeatIndex: Int?
  roundNumber: Int
```

---

## 24. Move History and Action Audit Log

Every engine state change produces a structured action record:
- `ROLL_DICE(seatIndex, value, timestamp)`
- `RELEASE_TOKEN(seatIndex, tokenId, targetSquare)`
- `MOVE_TOKEN(seatIndex, tokenId, fromSquare, toSquare)`
- `CAPTURE_TOKEN(seatIndex, attackerTokenId, victimTokenId, square)`
- `ENTER_HOME_PATH(seatIndex, tokenId, homeStep)`
- `FINISH_TOKEN(seatIndex, tokenId)`
- `CLIMB_LADDER(seatIndex, fromSquare, toSquare)`
- `DROP_SNAKE(seatIndex, fromSquare, toSquare)`
- `CONSECUTIVE_SIX_PENALTY(seatIndex)`
- `PASS_TURN(seatIndex, reason)`
- `MATCH_VICTORY(seatIndex, placement)`

---

## 25. Ruleset Versioning

All saved games and multiplayer sessions store a `rulesetId`.
- `LUDO_CLASSIC_V1`: Baseline classical Ludo.
- `SNAKE_LADDER_CLASSIC_V1`: Standard 100-square grid with 7 ladders and 8 snakes.
- `REMIX_HAZARDS_V1`: Ludo circuit with dynamic snakes and ladders.

If rules change in future releases, older match replays or incomplete save states execute against their original versioned ruleset engine.

---

## 26. Error and Invalid Action Handling

The game engine strictly rejects invalid actions with specific error codes:
- `ERR_INVALID_PLAYER`: Action submitted by player not assigned to active seat.
- `ERR_INVALID_PHASE`: `ROLL_DICE` submitted during movement phase, or `SELECT_TOKEN` submitted before roll.
- `ERR_TOKEN_IN_BASE`: Move action attempted on token in base without a roll of 6.
- `ERR_ROLL_EXCEEDS_GOAL`: Token in home path attempted to move with roll exceeding distance to goal.
- `ERR_FRIENDLY_COLLISION`: Token attempted to land on a non-safe square occupied by a friendly token.
- `ERR_MATCH_COMPLETED`: Action submitted after match has finished.

Rejections leave the game state unchanged and keep turn timers running.

---

## 27. Testable Rules and Edge Case Matrix

### Scenario 1: Releasing Token on 6
- **Given:** Red Token 0 is `IN_BASE`.
- **When:** Red rolls a `6` and submits `RELEASE_TOKEN(Red_0)`.
- **Then:** Red Token 0 transitions to `ON_TRACK` at `Step 0`. Red receives a bonus roll.

### Scenario 2: Token Release on 5 is Rejected
- **Given:** Red Token 0 is `IN_BASE`.
- **When:** Red rolls a `5` and submits `RELEASE_TOKEN(Red_0)`.
- **Then:** Engine returns `ERR_TOKEN_IN_BASE`. State remains unchanged.

### Scenario 3: Capture on Common Track
- **Given:** Green Token 1 is at `Step 14` (`ON_TRACK`). Red Token 0 is at `Step 10`.
- **When:** Red rolls a `4` and advances Red Token 0 to `Step 14`.
- **Then:** Green Token 1 transitions to `IN_BASE` (Green). Red Token 0 occupies `Step 14`. Red receives a bonus roll.

### Scenario 4: Capture Prohibited on Star Square
- **Given:** Green Token 1 is at `Step 21` (Star Safe Square). Red Token 0 is at `Step 17`.
- **When:** Red rolls a `4` and advances Red Token 0 to `Step 21`.
- **Then:** Red Token 0 and Green Token 1 both occupy `Step 21`. No capture occurs. Red receives no bonus roll.

### Scenario 5: Three Consecutive Sixes
- **Given:** Red has rolled `6` (Roll 1, moved) and rolled `6` (Roll 2, moved).
- **When:** Red rolls a `6` on Roll 3.
- **Then:** Engine emits `CONSECUTIVE_SIX_PENALTY`. Roll 3 movement is discarded. Moves from Roll 1 and 2 are preserved. Turn advances immediately to Green.

### Scenario 6: Exact Roll Requirement at Home Goal
- **Given:** Blue Token 2 is at `Blue H4` (requires exactly 2 steps to Goal).
- **When:** Blue rolls a `3`.
- **Then:** Blue Token 2 cannot move. If no other Blue token can move, turn terminates.

### Scenario 7: Snake & Ladder Overshoot Stalls
- **Given:** Player 1 token is at square `98`.
- **When:** Player rolls a `5`.
- **Then:** Player token cannot move. Token remains at square `98`. Turn advances.

### Scenario 8: Snake & Ladder Square 100 Exact Win
- **Given:** Player 1 token is at square `98`.
- **When:** Player rolls a `2`.
- **Then:** Token advances to square `100`. Match concludes with Player 1 winning 1st Place.

---

## 28. Offline vs. Online Rule Parity

The rules are identical in offline and online modes:
- **Offline:** The local Kotlin game engine (`engine/ludo`, `engine/snake`) runs in-memory on the Android device and acts as the legal authority.
- **Online:** The backend server runs the identical engine logic and acts as the legal authority.
- **Client Prediction:** In online play, the client engine displays local animation predictions, reconciling instantly when the authoritative server event arrives.

---

## 29. Accessibility and Rule Presentation

The game engine enforces technical precision; the UI translates engine states into clear visual and auditory cues:
- Tokens with legal moves pulse visually with a 4dp elevation lift.
- Tokens blocked by overshoot display a subtle lock icon upon tapping.
- Rule explanations in the Help Menu present rules in plain, friendly language:
  - "Roll a 6 to bring a piece out."
  - "Star tiles and your starting tile are safe from capture."
  - "You need an exact roll to enter the center goal."

---

## 30. Final Verification and Rule Approval

This specification removes all ambiguities:
1. Three-sixes penalty forfeits only the 3rd roll, preserving rolls 1 and 2.
2. Safe squares permit multi-player co-existence without capture.
3. Snake & Ladder overshoot rolls stall the token in place.
4. Finishing a token grants a bonus roll unless it is the 4th and final winning token.
5. Ties and infinite games are bounded by a 200-round limit.
