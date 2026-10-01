# Online Architecture

## System Overview
The online architecture provides competitive and cooperative multiplayer over the internet. The backend operates on an untrusted-client model, where the server acts as the single source of truth for match state, dice rolls, and turn validation.

Specific cloud hosting and database technologies remain open: TBD - requires a later architectural decision.

## Authentication
- Guest Authentication: Enables instant anonymous play using a secure device-generated token.
- Federated Identity: Optional account linking via Google Play Games, Apple Game Center, or OAuth providers.
- Session Management: Issues short-lived JSON Web Tokens (JWT) for API requests and persistent refresh tokens securely stored in Android EncryptedSharedPreferences.
- Anonymous-to-Permanent Migration: Upgrades guest profiles to permanent accounts without losing cosmetics or stats.

## Matchmaking
- Queues: Casual Matchmaking and Ranked Matchmaking.
- Matchmaker Logic: Pairs players based on latency, geographical region, and player skill rating (MMR).
- Room Allocation: When 2 to 4 matching tickets are found, the matchmaker creates an authoritative game room instance and directs clients to connect.
- Matchmaking Timeout: If no opponent appears within 45 seconds, the system offers an option to fill remaining slots with AI players or continue searching.

## Private Rooms
- Host-created custom lobbies supporting 2, 3, or 4 players.
- Room configuration parameters:
  - Game type (Ludo, Snake & Ladder, Remix).
  - Turn timer duration (10s, 15s, 30s).
  - Selected custom rules (three-sixes penalty, bounce-back, power cards).
  - Slot privacy (private invite-only or open to friends).

## Room Codes
- 6-character alphanumeric uppercase codes (e.g. `K7X92P`).
- Codes exclude easily confused characters (such as `0`, `O`, `1`, `I`).
- Codes map to active room identifiers in the gateway routing table with a 30-minute idle time-to-live (TTL).

## Friends
- Friends list tracking mutual contacts, current presence (Online, In Match, Offline), and match invitations.
- Friend discovery via unique player tags (e.g. `PlayerName#1234`) or shareable deep links.
- Block list and report features to prevent unwanted invitations or harassment.

## Realtime Communication
- Transport Layer: Secure WebSockets (WSS) or gRPC streaming for low-latency bidirectional message delivery: TBD - requires a later architectural decision.
- Heartbeats: Client sends ping packets every 5 seconds. Server considers connection dead if 3 consecutive pings fail.
- Message Serialization: Protocol Buffers (Protobuf) or lightweight JSON for low bandwidth consumption.

## Server-Authoritative Gameplay
- The server maintains the master copy of the game state for each active match.
- Clients send intents (e.g. `ROLL_DICE`, `SELECT_TOKEN`), never state modifications.
- The server validates:
  - Is it the requesting player's turn?
  - Has the dice roll already occurred for this turn?
  - Is the selected move legal under current board rules?
- The server resolves moves, calculates captures, checks win conditions, and broadcasts state deltas to all connected clients.

## Reconnection
- Grace Period: If a player disconnects during an active match, the server preserves their slot for 60 seconds.
- State Catch-up: Upon reconnecting with a valid match token, the server returns the complete current match snapshot including sequence number and active turn timer.
- Bot Takeover: If the player fails to reconnect within the grace period, an automated AI takes control of their seat, or the seat forfeits depending on room rules.

## Match State
- Match Lifecycle: `LOBBY` -> `STARTING` -> `IN_PROGRESS` -> `COMPLETED` -> `ARCHIVED`.
- Each state change carries a monotonically increasing sequence ID. Clients discard out-of-order packets.
- Completed match results persist to backend storage to update rankings, distribute coins, and write match records.

## Online Profiles
- Server-side entity storing:
  - Global user ID and account credentials.
  - Verified online stats (matches, win rates, MMR, rank tier).
  - Server-verified cosmetic inventory and balance of premium/earned currency.
  - Active quest completions and seasonal milestone points.

## Leaderboards
- Global and regional leaderboards for competitive play.
- Ranked tiers: Bronze, Silver, Gold, Platinum, Diamond, Master.
- Ranked seasons run on a monthly cycle with reward distributions at season conclusion.
- Anti-exploit auditing runs before posting final seasonal standings.
