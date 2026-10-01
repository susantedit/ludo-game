# Data Model Specification

## Data Architecture Overview
This document defines domain entities, persistence models, and data relationships across Ludora. The model separates local offline data structures from server-synchronized online entities.

Database implementations (e.g. Room entities, SQL tables) will map directly to these domain models in later phases.

## User Entity
Represents the authenticated person or guest identity.
- `id`: String (UUID or server-generated unique identifier).
- `createdAt`: Long (Epoch timestamp in milliseconds).
- `updatedAt`: Long (Epoch timestamp in milliseconds).
- `isAnonymous`: Boolean (True if guest, false if linked to an identity provider).
- `linkedProvider`: String? (e.g. "google", "apple", "email", or null).
- `email`: String? (Nullable for guest accounts).

## LocalProfile Entity
Stores offline profile information on the local device.
- `profileId`: String (Local unique identifier).
- `displayName`: String (Default "Player").
- `avatarId`: String (Identifier for selected avatar graphic).
- `frameId`: String (Identifier for selected avatar frame).
- `experiencePoints`: Long (Accumulated offline XP).
- `level`: Int (Calculated based on XP threshold).
- `coins`: Long (Local offline currency balance).
- `statsMatchesPlayed`: Int.
- `statsMatchesWon`: Int.
- `statsTokensCaptured`: Int.
- `lastActiveTimestamp`: Long.

## OnlineProfile Entity
Represents server-verified player profile data.
- `userId`: String (Foreign key to User).
- `username`: String (Unique global username).
- `ratingMMR`: Int (Competitive rating, e.g. 1200).
- `rankTier`: String (e.g. "Bronze", "Silver", "Gold", "Diamond").
- `onlineMatchesPlayed`: Int.
- `onlineMatchesWon`: Int.
- `winStreak`: Int.
- `seasonId`: String.

## Game Entity
Defines static metadata and configurations for supported board games.
- `gameType`: Enum (`LUDO`, `SNAKE_AND_LADDER`, `REMIX`).
- `displayName`: String.
- `minPlayers`: Int (2).
- `maxPlayers`: Int (4).
- `supportedRules`: List<String> (Keys for togglable game rules).

## Match Entity
Represents an individual game session instance.
- `matchId`: String (Unique match identifier).
- `gameType`: Enum (`LUDO`, `SNAKE_AND_LADDER`, `REMIX`).
- `mode`: Enum (`OFFLINE_PASS_AND_PLAY`, `OFFLINE_AI`, `ONLINE_PRIVATE`, `ONLINE_MATCHMAKING`).
- `status`: Enum (`PENDING`, `ACTIVE`, `COMPLETED`, `ABANDONED`).
- `startTime`: Long.
- `endTime`: Long?.
- `winnerPlayerId`: String?.
- `rulesConfig`: Map<String, Boolean> (Configured rule set).

## Room Entity
Represents a multiplayer lobby (local or online).
- `roomId`: String.
- `roomCode`: String? (6-character uppercase code for private rooms).
- `hostPlayerId`: String.
- `gameType`: Enum (`LUDO`, `SNAKE_AND_LADDER`, `REMIX`).
- `maxSlots`: Int (2 to 4).
- `turnTimeSeconds`: Int (e.g. 15).
- `createdAt`: Long.

## Player Entity
Represents a participant seated in a specific match.
- `playerId`: String (User ID or local synthetic ID for AI/guest).
- `seatIndex`: Int (0 to 3).
- `color`: Enum (`RED`, `GREEN`, `YELLOW`, `BLUE`).
- `isAi`: Boolean.
- `aiDifficulty`: Enum? (`EASY`, `MEDIUM`, `HARD`, or null for humans).
- `isHost`: Boolean.
- `connectionStatus`: Enum (`CONNECTED`, `RECONNECTING`, `DISCONNECTED`).

## GameState Entity
Snapshot of the active board state during a match.
- `matchId`: String.
- `turnNumber`: Int.
- `activeSeatIndex`: Int.
- `diceValue`: Int? (Last rolled value, 1 through 6, or null if awaiting roll).
- `consecutiveSixes`: Int.
- `turnPhase`: Enum (`WAITING_FOR_ROLL`, `WAITING_FOR_MOVE`, `RESOLVING_MOVE`, `TURN_COMPLETED`).
- `turnDeadline`: Long? (Timestamp when current turn times out).
- `tokens`: List<TokenState> (Positions of all tokens on the board).
- `hazardSquares`: List<HazardSquare>? (Active hazards for Remix mode).

## Achievement Entity
Defines achievable milestones.
- `achievementId`: String (e.g. "ACH_WIN_FIRST_GAME", "ACH_CAPTURE_50").
- `title`: String.
- `description`: String.
- `xpReward`: Int.
- `coinReward`: Int.
- `isUnlocked`: Boolean.
- `unlockedTimestamp`: Long?.

## Cosmetic Entity
Defines a customizable appearance item.
- `cosmeticId`: String.
- `category`: Enum (`DICE_SKIN`, `TOKEN_SKIN`, `BOARD_THEME`, `AVATAR_FRAME`, `VICTORY_FX`).
- `displayName`: String.
- `assetReference`: String (Path or bundle resource identifier).
- `priceCoins`: Long (0 for default items).
- `isPremium`: Boolean.

## Inventory Entity
Associates unlocked cosmetics with a user or local profile.
- `inventoryId`: String.
- `profileId`: String.
- `cosmeticId`: String (Foreign key to Cosmetic).
- `acquiredTimestamp`: Long.
- `isEquipped`: Boolean.

## Reward Entity
Defines currency, XP, or items granted for completing matches or quests.
- `rewardId`: String.
- `source`: Enum (`MATCH_WIN`, `DAILY_QUEST`, `LEVEL_UP`, `SEASON_PASS`, `AD_REWARD`).
- `coinsAmount`: Long.
- `xpAmount`: Long.
- `cosmeticId`: String?.
- `claimedTimestamp`: Long.

## AdReward Entity
Records rewarded ad engagements and grant verification.
- `transactionId`: String (Unique verification token).
- `userId`: String.
- `adPlacementId`: String.
- `verificationStatus`: Enum (`PENDING`, `VERIFIED`, `REJECTED`).
- `rewardGranted`: Reward Entity.
- `timestamp`: Long.

## MatchHistory Entity
Summary record stored locally or on server for completed matches.
- `historyId`: String.
- `matchId`: String.
- `gameType`: Enum.
- `mode`: Enum.
- `playerCount`: Int.
- `result`: Enum (`WON`, `LOST`, `DRAW`, `ABANDONED`).
- `finalPlacement`: Int (1st, 2nd, 3rd, 4th).
- `turnsPlayed`: Int.
- `tokensCaptured`: Int.
- `xpEarned`: Long.
- `coinsEarned`: Long.
- `playedTimestamp`: Long.

## Entity Relationships and Schema Definitions
- `User` 1-to-1 `OnlineProfile`
- `LocalProfile` 1-to-Many `Inventory`
- `LocalProfile` 1-to-Many `MatchHistory`
- `Match` 1-to-Many `Player` (2 to 4 players per match)
- `Match` 1-to-1 `GameState`
- `Match` 1-to-Many `MatchHistory`
- `Inventory` references `Cosmetic`
