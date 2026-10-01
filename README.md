# Ludora

**Where every roll matters.**

Ludora is a mobile board game platform built with an offline-first architecture. It combines classic physical board gameplay with modern game modes, local and AI opponents, and optional server-authoritative online multiplayer.

> **Active Development - Phase 1 (Foundation) and Phase 3 (Ludo Game Engine) implemented.**

## Core Games
- **Ludo**: Classic 2 to 4 player strategy board game with token racing, captures, safe squares, and configurable house rules.
- **Snake & Ladder**: Traditional 10x10 boustrophedon race to square 100 with dynamic snake falls and ladder ascents.
- **Ludora Remix**: Signature hybrid mode featuring hazards, power cards, and rotating chaos modifiers on classic board tracks.

## Offline-First Principle
Ludora treats offline mode as a first-class experience rather than a degraded fallback:
- 100% of core games, AI opponents, and pass-and-play multiplayer function with zero network access.
- Local profiles, achievements, and cosmetic progression persist directly on device.
- Zero ad requests or blocking network errors occur when offline.

## Planned Online Features
- Private multiplayer rooms with 6-character room codes.
- Global and regional matchmaking queues.
- Server-authoritative game state validation and anti-cheat protection.
- Reconnection recovery for interrupted mobile connections.
- Friends list and casual in-game emote reactions.

## Documentation Directory
The `/docs` directory serves as the single source of truth for all requirements, rules, architecture, and engineering standards:

```text
docs/
├── 01_PRD.md
├── 02_PRODUCT_VISION.md
├── 03_GAME_DESIGN.md
├── 04_GAME_RULES.md
├── 05_OFFLINE_ARCHITECTURE.md
├── 06_ONLINE_ARCHITECTURE.md
├── 07_TECH_ARCHITECTURE.md
├── 08_DATA_MODEL.md
├── 09_API_SPEC.md
├── 10_MULTIPLAYER_PROTOCOL.md
├── 11_SECURITY_PLAN.md
├── 12_ADS_MONETIZATION.md
├── 13_UI_UX_SPEC.md
├── 14_DESIGN_SYSTEM.md
├── 15_ANIMATION_SPEC.md
├── 16_ACCESSIBILITY.md
├── 17_TESTING_PLAN.md
├── 18_PERFORMANCE_PLAN.md
├── 19_ROADMAP.md
├── 20_DEVELOPMENT_RULES.md
└── 21_DECISIONS.md
```

## Current Development Status
- Phase: **Phase 0 - Documentation**
- Status: Source-of-truth documentation structure complete. Application implementation has not started.
