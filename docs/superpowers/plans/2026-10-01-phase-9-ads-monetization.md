# Phase 9: Ads and Monetization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 9 Ads & Monetization system as defined in `docs/19_ROADMAP.md` and `docs/12_ADS_MONETIZATION.md`:
1. Ad Provider Abstraction Layer: Clean vendor-agnostic `AdProvider` interface with offline-safe defaults and test harness.
2. Ad Policy & Frequency Enforcement: `AdPolicyManager` enforcing 8-minute/2-match interstitial cooldowns, 3/hour cap, 5/day rewarded cap, 3-session onboarding grace period, and strict offline suppression.
3. Rewarded Video Ads: Opt-in rewarded flow with Server-Side Verification (SSV) token validation granting bonus coins and quest rerolls.
4. Ad-Free Entitlement: `BillingService` handling "Remove Ads" purchases, permanently clearing banners and interstitials while preserving optional rewarded ads.
5. Interactive UI: Non-intrusive collapsible home screen banner, post-match interstitial overlay, rewarded ad action buttons, and store remove-ads card.

**Architecture:** Pure Kotlin logic in `:core:common` and `:core:model`, with interactive Jetpack Compose UI in `:app`.

---

### Task 1: Ad Models and Policy Manager

**Files:**
- Create: `core/model/src/main/kotlin/game/ludora/core/model/AdModels.kt`
- Modify: `core/model/src/main/kotlin/game/ludora/core/model/LocalProfile.kt`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/ads/AdPolicyManager.kt`
- Test: `core/common/src/test/kotlin/game/ludora/core/common/ads/AdPolicyManagerTest.kt`

- [x] **Step 1: Define `AdPlacement`, `AdReward`, `AdState`, and `AdPolicyConfig` in `:core:model`**
- [x] **Step 2: Add `isAdFree: Boolean` property to `LocalProfile`**
- [x] **Step 3: Implement `AdPolicyManager` with cooldown, hourly cap, 24h rewarded quota, and offline checks**
- [x] **Step 4: Unit test `AdPolicyManager` edge cases (offline, ad-free, cooldowns, grace period)**

---

### Task 2: AdProvider, BillingService, and Reward Verification

**Files:**
- Create: `core/common/src/main/kotlin/game/ludora/core/common/ads/AdProvider.kt`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/ads/FakeAdProvider.kt`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/ads/BillingService.kt`
- Create: `core/common/src/main/kotlin/game/ludora/core/common/ads/ServerSideRewardVerifier.kt`
- Test: `core/common/src/test/kotlin/game/ludora/core/common/ads/AdIntegrationTest.kt`

- [x] **Step 1: Define `AdProvider` interface and implement `FakeAdProvider` test double**
- [x] **Step 2: Define `BillingService` and `FakeBillingService` for IAP remove-ads entitlement**
- [x] **Step 3: Implement `ServerSideRewardVerifier` for cryptographic SSV validation**
- [x] **Step 4: Unit test ad loading, reward granting, and IAP entitlement transitions**

---

### Task 3: Interactive Ads & Monetization UI

**Files:**
- Create: `app/src/main/kotlin/game/ludora/ui/ads/HomeBannerAdView.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/ads/PostMatchInterstitialDialog.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/ads/RewardedAdButton.kt`
- Create: `app/src/main/kotlin/game/ludora/ui/ads/RemoveAdsCard.kt`
- Modify: `app/src/main/kotlin/game/ludora/MainActivity.kt`

- [x] **Step 1: Implement `HomeBannerAdView` with automatic zero-height collapse when offline or ad-free**
- [x] **Step 2: Implement `PostMatchInterstitialDialog` with 5s countdown, skip button, and dismiss callback**
- [x] **Step 3: Implement `RewardedAdButton` with daily quota indicator and offline badge**
- [x] **Step 4: Implement `RemoveAdsCard` for IAP purchase in store and profile sheets**
- [x] **Step 5: Integrate ads and billing into `MainActivity` dashboard and game-over flows**

---

### Task 4: Documentation and Decisions Record

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- [x] **Step 1: Record Phase 9 decisions and update changelog**
