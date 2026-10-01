# API Specification

## Protocol and Conventions
- Base URL: `https://api.ludora.game/v1` (Staging/Production endpoints: TBD - requires a later architectural decision).
- Data Format: JSON payloads for REST API; Protocol Buffers or JSON for WebSocket frames.
- Authentication: Bearer token format via `Authorization: Bearer <JWT>` header on all protected endpoints.
- Error Format: Standard error envelope returning `code`, `message`, and optional `details`.

## Authentication Endpoints

### POST /auth/guest
Registers or authenticates an anonymous guest session.
- Request Body:
  - `deviceId`: String
  - `clientVersion`: String
- Response:
  - `userId`: String
  - `accessToken`: String
  - `refreshToken`: String
  - `expiresIn`: Long

### POST /auth/link
Links a guest account to a permanent identity provider.
- Request Body:
  - `provider`: String ("google", "apple")
  - `idToken`: String
- Response:
  - `userId`: String
  - `profile`: OnlineProfile
  - `status`: String ("LINKED")

### POST /auth/refresh
Refreshes an expired access token using a valid refresh token.
- Details: TBD - requires a later architectural decision.

## Room Management Endpoints

### POST /rooms
Creates a new private multiplayer room.
- Request Body:
  - `gameType`: String ("LUDO", "SNAKE_AND_LADDER", "REMIX")
  - `maxPlayers`: Int (2 to 4)
  - `turnTimeSeconds`: Int (15)
  - `rules`: Map<String, Boolean>
- Response:
  - `roomId`: String
  - `roomCode`: String (6-character code)
  - `hostId`: String
  - `joinUrl`: String

### GET /rooms/{roomId}
Retrieves room status and list of seated players.
- Response: Room entity object.

### POST /rooms/join
Joins an existing room using a 6-character room code.
- Request Body:
  - `roomCode`: String
- Response:
  - `roomId`: String
  - `assignedSeat`: Int
  - `gatewaySocketUrl`: String

## Matchmaking Endpoints

### POST /matchmaking/queue
Enters the matchmaking queue for a specific game type.
- Request Body:
  - `gameType`: String
  - `queueType`: String ("CASUAL", "RANKED")
- Response:
  - `ticketId`: String
  - `estimatedWaitSeconds`: Int

### DELETE /matchmaking/queue/{ticketId}
Cancels an active matchmaking ticket.
- Response: 204 No Content.

### GET /matchmaking/status/{ticketId}
Polls queue ticket status (if not using WebSocket notifications).
- Details: TBD - requires a later architectural decision.

## Match Endpoints

### GET /matches/{matchId}
Fetches current metadata and participant list for an active match.
- Response: Match entity object.

### POST /matches/{matchId}/surrender
Forfeits the requesting player's position in an active match.
- Response: Match status with forfeiture recorded.

## Game State Endpoints

### GET /matches/{matchId}/state
Retrieves a full snapshot of the authoritative board state.
- Response: GameState entity object with active token coordinates and turn indicators.

### Realtime State Actions
Realtime actions (rolling dice, selecting tokens) execute over WebSocket connections rather than HTTP endpoints. See `10_MULTIPLAYER_PROTOCOL.md` for command definitions.

## Profile Endpoints

### GET /profiles/me
Retrieves the authenticated player's online profile, ranking, and stats.
- Response: OnlineProfile entity.

### PATCH /profiles/me
Updates editable profile fields (display name, active avatar, equipped cosmetics).
- Request Body:
  - `displayName`: String?
  - `avatarId`: String?
  - `equippedDice`: String?
  - `equippedBoard`: String?
- Response: Updated OnlineProfile entity.

## Friends Endpoints

### GET /friends
Retrieves the user's friends list and online presence statuses.
- Response: List of friend profiles with presence flags.

### POST /friends/request
Sends a friend request by player tag.
- Request Body:
  - `playerTag`: String
- Response: Status confirmation.

### POST /friends/invite
Invites a friend to an active room.
- Details: TBD - requires a later architectural decision.

## Rewards Endpoints

### GET /rewards/quests
Retrieves active daily and weekly quests with current progress counters.
- Response: List of quest objects.

### POST /rewards/quests/{questId}/claim
Claims the rewards for a completed quest.
- Response: Updated balances for coins and XP.

### POST /rewards/ad/verify
Verifies a completed rewarded ad view and credits currency.
- Request Body:
  - `adPlacementId`: String
  - `verificationToken`: String
- Response:
  - `rewardGranted`: Reward entity
  - `newBalance`: Long

## Leaderboard Endpoints

### GET /leaderboards/global
Fetches top global players for the current competitive season.
- Query Parameters: `limit` (default 50), `offset` (default 0).
- Response: Paginated list of leaderboard entries.

### GET /leaderboards/regional
Fetches regional leaderboard based on IP or configured country code.
- Details: TBD - requires a later architectural decision.
