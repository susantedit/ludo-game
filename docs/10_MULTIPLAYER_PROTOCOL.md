# Multiplayer Protocol Specification

## Protocol Overview
The Ludora multiplayer protocol defines the message formats and state exchange sequences between the mobile client and the game server. It operates over full-duplex WebSocket connections with binary Protobuf or structured JSON framing.

## Client and Server Responsibilities

### Client Responsibilities
- Render board state, animations, and sound effects accurately from server updates.
- Capture user inputs (tap dice cup, tap token) and send explicit command intents to the server.
- Interpolate token movement along board paths for visual smoothness.
- Manage local connection health, heartbeats, and reconnection attempts.

### Server Responsibilities
- Maintain the single authoritative game state.
- Generate all random dice roll outcomes using secure server-side randomness.
- Validate that commands originate from the active player and represent legal moves.
- Enforce turn timers, calculate captures, and advance turn progression.
- Broadcast state changes to all players in the room simultaneously.

## Commands (Client to Server)

### `CONNECT_ROOM`
Establishes session within an assigned room.
```json
{
  "type": "CONNECT_ROOM",
  "roomId": "room_abc123",
  "authToken": "jwt_token_here"
}
```

### `ROLL_DICE`
Requests a dice roll on the active player's turn.
```json
{
  "type": "ROLL_DICE",
  "matchId": "match_xyz789",
  "clientTimestamp": 1727764800000
}
```

### `SELECT_TOKEN`
Selects a specific token to execute a move after rolling.
```json
{
  "type": "SELECT_TOKEN",
  "matchId": "match_xyz789",
  "tokenId": "red_token_2",
  "targetSquare": 18
}
```

### `SEND_EMOTE`
Sends an in-game reaction emote to room participants.
```json
{
  "type": "SEND_EMOTE",
  "matchId": "match_xyz789",
  "emoteId": "emote_thumbs_up"
}
```

## Events (Server to Client)

### `ROOM_JOINED`
Confirms room entry and provides assigned seat and current room roster.

### `MATCH_STARTED`
Signals match start and delivers initial board layout and player seat allocations.

### `TURN_STARTED`
Notifies all clients whose turn is active and begins the turn countdown timer.
```json
{
  "type": "TURN_STARTED",
  "activeSeat": 0,
  "turnDeadline": 1727764815000,
  "sequenceId": 42
}
```

### `DICE_ROLLED`
Broadcasts the authoritative dice outcome and lists legal move options.
```json
{
  "type": "DICE_ROLLED",
  "seatIndex": 0,
  "diceValue": 6,
  "legalTokenIds": ["red_token_0", "red_token_1"],
  "bonusRollEarned": true,
  "sequenceId": 43
}
```

### `TOKEN_MOVED`
Broadcasts token movement path, resolved captures, and hazard triggers.
```json
{
  "type": "TOKEN_MOVED",
  "seatIndex": 0,
  "tokenId": "red_token_1",
  "fromSquare": 12,
  "toSquare": 18,
  "capturedToken": { "seatIndex": 2, "tokenId": "yellow_token_3" },
  "hazardTriggered": null,
  "sequenceId": 44
}
```

### `MATCH_COMPLETED`
Declares final placements, winner, and reward allocations.

## Game State Synchronization
- Monotonic Sequence IDs: Every server event increments a sequence ID (`sequenceId`).
- Delta Updates: Regular gameplay exchanges send event deltas (`TOKEN_MOVED`, `DICE_ROLLED`).
- Full Snapshot Reconciliation: If a client detects a missing sequence number, it requests a full `GET_STATE_SNAPSHOT` to resynchronize without restarting the app.

## Turn Validation
- The server checks whether `clientTimestamp` falls within the active turn window.
- The server verifies that `playerId` matches `activeSeatIndex`.
- The server computes legal moves independently. If `SELECT_TOKEN` names an invalid piece, the server rejects the command with an `INVALID_MOVE` error and keeps the turn active until the timer expires.

## Dice Generation Protocol
- Clients never supply roll numbers.
- Upon receiving `ROLL_DICE`, the server draws a cryptographic random integer from 1 to 6.
- The server tracks consecutive sixes. A third six triggers a `TURN_FORFEITED` event and passes play immediately.

## Reconnection Sequence
1. Client loses network connectivity; socket disconnects.
2. Client detects network restoration and establishes a new WebSocket connection to the gateway.
3. Client sends `RECONNECT_MATCH` with `matchId`, `userId`, and `lastSeenSequenceId`.
4. Server verifies the player's reservation in the active match.
5. Server sends a complete `STATE_SNAPSHOT` containing current token positions, remaining turn timer, and current turn seat.
6. Client resets its local board to the snapshot coordinates and resumes normal play.

## Disconnect Handling
- When a client disconnects, the server marks their status as `DISCONNECTED` and starts a 60-second grace timer.
- Room participants receive a `PLAYER_DISCONNECTED` notification.
- If the turn belongs to the disconnected player, a 15-second timer runs. If the player does not reconnect in time, the server auto-selects a legal move.

## Timeout Handling
- Single Timeout: Server auto-plays the highest-utility move (such as releasing a token or capturing) or skips the turn if no move exists.
- Two Consecutive Timeouts: Server toggles the seat to `AUTO_BOT_ACTIVE` mode. An automated heuristic agent handles subsequent turns so the match proceeds without waiting for dead connections.
- The original player can reclaim their seat at any point before match conclusion by reconnecting.

## Anti-Cheat Considerations
- Untrusted Client: The server rejects any client message attempting to dictate token positions, roll values, or game outcomes.
- Timing Attacks: Server rejects move actions submitted after the authoritative turn deadline has passed.
- Replay Protection: Packets require valid session tokens and sequential message counters; replayed packets are dropped immediately.
