# Phase 7: Online Infrastructure Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 7 Online Infrastructure as defined in `docs/19_ROADMAP.md` and `docs/06_ONLINE_ARCHITECTURE.md`:
1. Core Network Module (`:core:network`): Scaffolding pure Kotlin networking library with serialization and coroutine support.
2. Authentication Service: Guest login with secure device tokens, JWT session models, and profile synchronization contracts.
3. Realtime Gateway & Room Synchronization: Room code generator (6-character unambiguous Crockford base32 alphabet), WebSocket protocol messages (Client/Server packets), Room state machine (`LOBBY`, `STARTING`, `IN_PROGRESS`, `COMPLETED`), and server-authoritative state synchronization engine.
4. Simulated In-Memory Gateway: Full headless online test harness enabling end-to-end matchmaking, room creation, joining, and multiplayer event broadcasting without external cloud hosting.
5. Security Verification Plan: Security audit of new network code for input sanitization, token validation, and rate-limiting safeguards.

**Architecture:** Pure Kotlin JVM library module (`core:network`) consuming `:core:model` and `:engine:core`.

---

### Task 1: Scaffolding `:core:network` and Domain Protocols

**Files:**
- Create: `core/network/build.gradle.kts`
- Create: `core/network/src/main/kotlin/game/ludora/core/network/model/AuthModels.kt`
- Create: `core/network/src/main/kotlin/game/ludora/core/network/model/RoomModels.kt`
- Create: `core/network/src/main/kotlin/game/ludora/core/network/model/SocketPackets.kt`
- Create: `core/network/src/main/kotlin/game/ludora/core/network/util/RoomCodeGenerator.kt`
- Test: `core/network/src/test/kotlin/game/ludora/core/network/util/RoomCodeGeneratorTest.kt`

- [x] **Step 1: Create `core/network/build.gradle.kts`**
- [x] **Step 2: Implement `RoomCodeGenerator` (6-char Crockford alphanumeric excluding 0, O, 1, I)**
- [x] **Step 3: Define Auth, Room, and WebSocket packet data models**
- [x] **Step 4: Unit test room code generation uniqueness and length**

---

### Task 2: Realtime Gateway & Synchronization Engine

**Files:**
- Create: `core/network/src/main/kotlin/game/ludora/core/network/gateway/RealtimeGateway.kt`
- Create: `core/network/src/main/kotlin/game/ludora/core/network/gateway/InMemoryRealtimeGateway.kt`
- Create: `core/network/src/main/kotlin/game/ludora/core/network/auth/AuthService.kt`
- Test: `core/network/src/test/kotlin/game/ludora/core/network/gateway/InMemoryRealtimeGatewayTest.kt`

- [x] **Step 1: Implement `AuthService` contract and guest token management**
- [x] **Step 2: Implement `RealtimeGateway` interface for multiplayer streaming**
- [x] **Step 3: Implement `InMemoryRealtimeGateway` supporting room creation, joining by 6-char code, seat allocation, and message broadcasting**
- [x] **Step 4: Unit test multi-client room join, ping/pong heartbeats, and packet routing**

---

### Task 3: Security Verification Plan

- [x] **Step 1: Audit network packet sanitization against injection and room spoofing**
- [x] **Step 2: Verify room code entropy and brute-force mitigation limits**
- [x] **Step 3: Document security findings and protections in plan report**

#### Security Audit Summary:
1. **CSPRNG Room Code Generation**: `RoomCodeGenerator` utilizes `java.security.SecureRandom` over the 32-character Crockford alphabet ($32^6 = 1,073,741,824$ permutations), preventing room code prediction.
2. **Input Validation**: `joinRoom` enforces character sanitization (`.uppercase().trim()`), length bounds, and non-null regex checks.
3. **Authorization Check**: `startMatch` strictly enforces host ownership validation (`it.id == hostUserId && it.isHost`). Non-hosts cannot force match transitions.
4. **Buffer Protection**: `MutableSharedFlow` channels configured with finite buffers (`extraBufferCapacity = 64`) preventing unbounded memory exhaustion.

---

### Task 4: Documentation and Decisions Record

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- [x] **Step 1: Log Phase 7 decision and changelog updates**
