# Security Plan

## Threat Model
The Ludora threat model identifies potential attackers, attack surfaces, and defensive boundaries across offline and online environments.
- Attackers: Malicious players seeking unfair match advantages, automated bots farming rewards, and network eavesdroppers.
- Assets: Authoritative match integrity, player account credentials, virtual currency balances, and anti-cheat reputation.
- Threat Categories:
  - Client state tampering (modifying memory to force dice values or bypass collision rules).
  - Protocol replay attacks (repeating roll or reward requests).
  - Impersonation and unauthorized room hijacking.
  - Fraudulent reward claims via spoofed ad callbacks.
  - Denial of Service (DoS) targeting matchmaking gateways.

## Client Trust Boundaries
- The mobile application running on end-user hardware is untrusted.
- Client memory, APK binaries, and network traffic are subject to inspection and manipulation via tools like Frida, Cheat Engine, or proxy interceptors.
- Trust boundary line: All security enforcement, rule adjudication, and reward distribution occur on the server side of the API and WebSocket boundary.

## Server Authority
- The server maintains the sole source of truth for online matches.
- All gameplay events (dice rolls, piece deployments, hazard activations, win checks) execute inside the server-side engine.
- Clients submit player intentions, which the server validates against active rules before state mutation.

## Authentication Security
- Token-based authentication using industry standard JWT for API transactions.
- Refresh tokens stored in Android EncryptedSharedPreferences (backed by Android Keystore).
- Short expiration lifetimes on access tokens (15 minutes); automated rotation on refresh token usage.
- Secure binding of device credentials during guest authentication.

## Authorization Controls
- Role-based and ownership-based checks:
  - Only the host player can start private room matches or change room rules.
  - Only the player seated at the active index can submit roll and move commands.
  - Players cannot inspect private room states without holding a valid room membership token.

## Input Validation
- Strict server-side schema validation on every inbound JSON or Protobuf payload.
- Enforce bounds checking: Room codes must match `^[A-Z0-9]{6}$`, player display names must not exceed 24 UTF-8 characters, and numerical inputs must match expected ranges.
- Sanitize all player-provided strings to prevent cross-site scripting (XSS) or injection attacks in leaderboards and profile displays.

## Rate Limiting
- API Gateway rate limits:
  - Authentication attempts: 5 per minute per IP.
  - Matchmaking ticket creation: 3 per minute per user.
  - In-game chat/emotes: 1 message per 2 seconds per player to prevent room spam.
  - General REST endpoints: 60 requests per minute per authenticated token.

## Anti-Cheat Protections
- Deterministic Verification: Server cross-checks client timestamps against wall-clock deadlines. Late submissions are discarded.
- Impossible Move Detection: Server verifies spatial continuity. A token cannot jump across non-consecutive tiles unless an explicit ladder or hazard event justifies the position.
- Emote Flooding and Desync Defense: Automatic throttling and disconnection of clients that emit malformed command sequences.

## Secure Randomization
- Dice roll outcomes must never depend on client-side math libraries or unseeded PRNGs.
- Offline: Cryptographically strong random generator (`java.security.SecureRandom`) seeded by the operating system entropy pool.
- Online: Server-side cryptographic RNG generating values in isolation. The server only reveals the rolled value after logging the command.

## WebSocket and Realtime Security
- All realtime connections require Transport Layer Security (TLS 1.3) over `wss://`.
- Initial WebSocket handshake requires authentication ticket verification before socket upgrade.
- Socket ping/pong frames track keepalives; dead sockets terminate promptly to prevent lingering resource exhaustion.

## Reward Security
- Reward calculations (coins, XP, rank points) occur strictly on the backend after match completion.
- Clients receive reward notifications, but balance adjustments write directly into server databases.
- Offline rewards accumulate in a signed local store and reconcile against server sanity checks upon reconnection.

## Ad Reward Verification
- Rewarded ad grants require Server-Side Verification (SSV) callbacks from the ad network.
- The app client never directly reports: "I finished watching an ad, give me 50 coins."
- Flow:
  1. Client initiates ad view with unique transaction nonce.
  2. Ad network displays video to player.
  3. Ad network server delivers signed cryptographic callback to Ludora backend.
  4. Ludora backend verifies cryptographic signature, checks nonce novelty, and credits player balance.

## Data Protection and Encryption
- In Transit: All communications encrypted via TLS 1.3 with certificate pinning on sensitive endpoints: TBD - requires a later architectural decision.
- At Rest: Local sensitive preferences encrypted using Android Keystore and Jetpack Security primitives.
- PII Minimization: The platform avoids collecting unnecessary personal data, storing only what is strictly required for gameplay and ranking.

## Security Logging and Audit
- Centralized audit logging for critical events: Account linkings, suspicious move rejections, repeated rate-limit violations, and reward grants.
- Anomaly alerts trigger when a user account exhibits statistical anomalies (such as an impossible 100% win rate over 50 matches).

## Abuse Prevention
- Reporting mechanism allowing players to flag offensive usernames, avatar images, or unsportsmanlike conduct.
- Automated word filters blocking hate speech, profanity, and harassment in custom room names and player handles.
- Graduated penalty system: Warning -> Temporary Matchmaking Ban -> Permanent Account Suspension.

## Security Testing Strategy
- Automated security testing integrated into CI/CD pipelines.
- Static Application Security Testing (SAST) targeting Android source code and dependencies.
- Dynamic penetration testing targeting WebSocket gateways, room authorization rules, and reward callbacks.
- Verification plan alignment with the `create-security-implementation-plan` skill before production release.
