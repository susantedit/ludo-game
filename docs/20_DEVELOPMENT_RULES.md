# Development Rules

## Core Engineering Principles
The following mandatory rules govern the development of Ludora. Every developer and autonomous coding agent must adhere strictly to these principles across all phases.

## Documentation Before Implementation
- No feature code is written before technical requirements, architecture specs, and data models are defined in `/docs`.
- When changes to logic or architecture occur, update the corresponding documentation first or document the change in `21_DECISIONS.md`.

## Small Implementation Tasks
- Break large features into small, focused, independently testable tasks.
- Avoid massive single-prompt changes. Implement iteratively with verification checkpoints after every unit of work.

## No Unnecessary Dependencies
- Do not import external libraries or packages without a clear justification.
- Avoid bloated monolithic frameworks or game engines for features easily expressed in native Kotlin and Jetpack Compose.

## Real Implementation Only (No Fake Functionality)
- Do not generate fake mock implementations, dummy stub algorithms, or hardcoded return values in place of real logic.
- If a subsystem is in progress, isolate it cleanly rather than faking execution.

## No Placeholder Production UI
- Do not check in production UI screens containing unfinished placeholder boxes or "Coming Soon" blocks where core gameplay belongs.
- Every shipped screen must represent a functioning, tested user experience.

## No Hardcoded Secrets
- Never commit API keys, server credentials, encryption keys, or private tokens to the repository.
- Inject secrets through `local.properties`, system environment variables, and Gradle BuildConfig fields.

## Offline Functionality Must Remain Independent
- Offline gameplay must never depend on remote server responses, internet connection checks, or backend availability.
- The game engine must run locally without network imports in the domain logic.

## Server Authority for Online Competitive Gameplay
- The backend server is the sole authority for online multiplayer matches.
- Never trust the mobile client to calculate dice rolls, legal destinations, captures, or victories during online play.

## Test Before Moving to the Next Milestone
- Every milestone requires concrete test verification (unit tests, UI tests, or command benchmarks).
- Do not declare a milestone complete without presenting passing test results.

## Preserve Existing Functionality
- Modifications to the codebase must preserve existing passing tests and validated behaviors.
- Refactoring tasks must maintain backwards compatibility with existing local database schemas via explicit migrations.

## Keep Game Logic Separate from Presentation
- Domain game rules (Ludo, Snake & Ladder, Remix) must reside in pure Kotlin modules (`engine/`) with zero dependencies on Android UI packages (`android.*`, `androidx.compose.*`).
- UI composables act strictly as presentation renderers and input dispatchers.

## Prefer Simple Solutions
- Choose clean, idiomatic Kotlin and Compose primitives over intricate custom frameworks.
- Prioritize code readability, maintainability, and direct execution paths.

## Avoid Overengineering
- Build strictly for current requirements and documented roadmap phases.
- Do not add speculative abstractions or unnecessary design patterns for features that do not yet exist.

## Skill Integration Guidelines
- Use [stop-slop]: Apply stop-slop writing guidelines to keep all documentation, UI text, and code comments concise, natural, and free of generic AI padding.
- Use [impeccable]: Apply the impeccable design skill for reviewing, hardening, polishing, and auditing the user interface to ensure high visual quality.
- Use [animate]: Apply the animate skill when creating game motion to ensure animations communicate state clearly with appropriate physics and timing.
- Use [playwright-cli]: Use Playwright where applicable for automated browser verification of web portals, admin consoles, or backend dashboards.
- Use [ui-ux-pro-max]: Apply ui-ux-pro-max when building the design system tokens, typography scales, color palettes, and responsive layouts.
