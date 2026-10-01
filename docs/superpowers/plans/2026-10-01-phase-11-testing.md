# Phase 11: Testing Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 11 Testing verification suite as defined in `docs/19_ROADMAP.md`, `docs/17_TESTING_PLAN.md`, and `docs/18_PERFORMANCE_PLAN.md`:
1. Network Interruption & Chaos Simulator: Simulates packet loss, latency jitter, sudden disconnections, out-of-order packet drops, and snapshot reconciliation.
2. Performance & Benchmark Suite: Benchmarks turn processing latency (<1ms budget), 1,000 turn throughput, memory allocation limits (<120MB heap budget), and cold start simulation.
3. Multi-Mode End-to-End Regression Suite: Comprehensive integration suite running complete automated game loops across Classic Ludo, Snake & Ladder, Remix Mode, AI heuristics, progression leveling, daily quests, and Ad-Free entitlement.
4. Low-End Hardware & Accessibility Audit: Audits low-RAM memory policies, minimum 48x48dp touch targets, and WCAG AA contrast compliance across all 4 color-blind palettes.

**Architecture:** Pure Kotlin test suites in `:core:network`, `:core:common`, and `:core:designsystem`.

---

### Task 1: Network Interruption & Chaos Transport Testing

**Files:**
- Create: `core/network/src/test/kotlin/game/ludora/core/network/chaos/ChaosRealtimeGatewayTest.kt`

- [x] **Step 1: Implement `ChaosRealtimeGateway` with configurable packet drop rates and latency jitter**
- [x] **Step 2: Test sudden socket drops, 60s disconnect grace period, and snapshot catch-up**
- [x] **Step 3: Test out-of-order sequence packet rejection and duplicate packet filtering**

---

### Task 2: Engine Performance & Memory Benchmark Suite

**Files:**
- Create: `core/common/src/test/kotlin/game/ludora/core/common/benchmark/EnginePerformanceBenchmarkTest.kt`

- [x] **Step 1: Benchmark turn execution latency against the < 1ms budget**
- [x] **Step 2: Benchmark 1,000-turn simulation throughput and zero unhandled exception guarantee**
- [x] **Step 3: Profile heap memory footprint and verify < 120MB active heap threshold**
- [x] **Step 4: Benchmark cold-start initialization latency of core managers**

---

### Task 3: Multi-Mode End-to-End Regression Suite

**Files:**
- Modify: `core/common/build.gradle.kts`
- Create: `core/common/src/test/kotlin/game/ludora/core/common/regression/EndToEndRegressionSuiteTest.kt`

- [x] **Step 1: Add engine test dependencies to `core/common/build.gradle.kts`**
- [x] **Step 2: Implement full match simulations for Classic Ludo, Snake & Ladder, and Remix Mode**
- [x] **Step 3: Verify quest tracking, progression level-ups, and Ad-Free entitlement integration**
- [x] **Step 4: Verify zero cross-mode rule bleed (Remix hazards do not alter classic Ludo rules)**

---

### Task 4: Low-End Hardware & Accessibility Audit

**Files:**
- Create: `core/common/src/main/kotlin/game/ludora/core/common/hardware/LowMemoryPolicy.kt`
- Create: `core/common/src/test/kotlin/game/ludora/core/common/hardware/LowMemoryPolicyTest.kt`
- Create: `core/designsystem/src/test/kotlin/game/ludora/core/designsystem/accessibility/AccessibilityContrastAuditTest.kt`

- [x] **Step 1: Implement `LowMemoryPolicy` managing particle counts, shadow fidelity, and cache trims**
- [x] **Step 2: Unit test `LowMemoryPolicy` behavior under normal vs low-RAM conditions**
- [x] **Step 3: Audit WCAG AA contrast compliance across all 4 color-blind palettes**

---

### Task 5: Documentation and Decisions Record

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- [x] **Step 1: Record Phase 11 decisions and update changelog**
