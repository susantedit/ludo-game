# Product Roadmap

## Roadmap Structure
Ludora follows a sequenced, milestone-driven execution plan. Each phase builds upon verified outcomes of preceding phases. No phase starts until all acceptance criteria of the prior phase pass. No arbitrary calendar dates are assigned.

## Phase 0: Documentation
- Deliverables:
  - Complete architectural, rules, design, and protocol documents in `/docs`.
  - Establish development guidelines, testing standards, and decision records.
- Status: Active.

## Phase 1: Foundation
- Deliverables:
  - Android application project scaffolding (Kotlin, Gradle Version Catalogs, multi-module structure).
  - Core domain models and common utility abstractions.
  - Local database baseline (Room) and preferences store (DataStore).
  - Navigation shell and top-level routing.

## Phase 2: Design System
- Deliverables:
  - Compose theme tokens (colors, typography, shapes, elevation).
  - Core component library (cards, buttons, icons, modals, status pills).
  - Board drawing primitives and Canvas coordinate mappers.
  - Design verification using the `ui-ux-pro-max` skill.

## Phase 3: Ludo Offline
- Deliverables:
  - Deterministic Ludo game engine with full rule coverage.
  - Headless unit test suite verifying all movement and capture cases.
  - Interactive Compose board UI and token rendering.
  - Local pass-and-play coordinator for 2 to 4 human players.
  - Local heuristic AI opponents (Easy, Medium, Hard).

## Phase 4: Snake & Ladder Offline
- Deliverables:
  - 10x10 boustrophedon grid engine with snake and ladder resolution.
  - Rule test matrix (exact finish roll, snake falls, ladder climbs).
  - Snake & Ladder board UI, custom vector snakes, and rungs.
  - Local multiplayer and AI support for Snake & Ladder.

## Phase 5: Remix Mode
- Deliverables:
  - Remix engine supporting hybrid rules (Ludo with snake/ladder hazard tiles).
  - Power card system (Shield, Boost, Reroll, Swap).
  - Round chaos modifiers and customizable rule toggles.

## Phase 6: Progression
- Deliverables:
  - Local profile management and stats tracking.
  - Experience point (XP) engine and player level milestones.
  - Offline cosmetic inventory (dice skins, token styles, board themes).
  - Daily quest tracking and reward claiming.

## Phase 7: Online Infrastructure
- Deliverables:
  - Backend service architecture and API gateway setup.
  - Authentication service (guest accounts and account linking).
  - Realtime WebSocket gateway and room state synchronization engine.
  - Security audit and verification plan via `create-security-implementation-plan`.

## Phase 8: Online Multiplayer
- Deliverables:
  - Private room creation and 6-character room code joining.
  - Casual and ranked matchmaking queues.
  - Server-authoritative gameplay loop with turn timer enforcement.
  - Reconnection workflow and state reconciliation.

## Phase 9: Ads
- Deliverables:
  - AdProvider abstraction layer and SDK integration (online only).
  - Home screen banner unit.
  - Post-match interstitial ad with frequency limits and cooldowns.
  - Rewarded ad flow with server-side verification callbacks.
  - Ad-free in-app purchase configuration.

## Phase 10: Polish
- Deliverables:
  - Purposeful motion implementation and fine-tuning via the `animate` skill.
  - Interface refinement and micro-interaction polish via the `impeccable` skill.
  - Sound effects and haptic feedback integration.
  - Audio and accessibility mode verification.

## Phase 11: Testing
- Deliverables:
  - End-to-end integration and automated regression suites.
  - Network interruption and chaos testing.
  - Performance profiling, frame rate benchmarking, and memory leak checks.
  - Low-end hardware compatibility audit.

## Phase 12: Release
- Deliverables:
  - Production build signing and R8 optimization.
  - Store listings, localized metadata, and asset bundling.
  - Production release deployment and operational monitoring.
- Status: Complete.

## Phase Transition Criteria
- A phase is complete only when all deliverables exist in the codebase and pass automated verification.
- Blockers or architecture adjustments discovered during a phase must be recorded in `21_DECISIONS.md` before advancing.
