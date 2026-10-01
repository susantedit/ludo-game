# Phase 12: Release Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the complete Phase 12 Release deliverables as defined in `docs/19_ROADMAP.md`:
1. Production Build Signing & R8 Optimization: R8 keep rules for models/serialization/Room/Compose, version bump to 1.0.0 (versionCode 100), reproducible signing configuration with environment fallback, and VIBRATE permission declaration.
2. Store Listings, Localized Metadata & Compliance Docs: Fastlane-compliant store listings across 5 locales (en-US, es-ES, fr-FR, de-DE, hi-IN), what's new release notes, Privacy Policy (`docs/22_PRIVACY_POLICY.md`), and Terms of Service (`docs/23_TERMS_OF_SERVICE.md`).
3. Release Verification Gate & Automated Validation Test: Pre-flight release deployment checklist (`distribution/release_checklist.md`) and automated `ReleaseReadinessTest` validating metadata length limits, privacy disclosures, and security constraints.
4. Documentation & Roadmap Finalization: Architecture decision record in `docs/21_DECISIONS.md`, `CHANGELOG.md` entry, and `docs/19_ROADMAP.md` status completion.

**Architecture:** Release engineering artifacts under `distribution/`, `fastlane/metadata/android/`, `docs/`, `app/`, and `:core:common` test suite.

---

### Task 1: Production Signing Configuration & R8 Optimization

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/proguard-rules.pro`

- [x] **Step 1: Update versioning in `app/build.gradle.kts` to 1.0.0 (versionCode 100) and configure release signing**
- [x] **Step 2: Add `VIBRATE` permission to `app/src/main/AndroidManifest.xml` for haptics support**
- [x] **Step 3: Define complete R8 keep rules in `app/proguard-rules.pro` for Kotlinx Serialization, Room, Compose, and domain engines**

---

### Task 2: Fastlane Store Metadata & Localized Listings

**Files:**
- Create: `fastlane/metadata/android/en-US/title.txt`
- Create: `fastlane/metadata/android/en-US/short_description.txt`
- Create: `fastlane/metadata/android/en-US/full_description.txt`
- Create: `fastlane/metadata/android/es-ES/title.txt`
- Create: `fastlane/metadata/android/es-ES/short_description.txt`
- Create: `fastlane/metadata/android/es-ES/full_description.txt`
- Create: `fastlane/metadata/android/fr-FR/title.txt`
- Create: `fastlane/metadata/android/fr-FR/short_description.txt`
- Create: `fastlane/metadata/android/fr-FR/full_description.txt`
- Create: `fastlane/metadata/android/de-DE/title.txt`
- Create: `fastlane/metadata/android/de-DE/short_description.txt`
- Create: `fastlane/metadata/android/de-DE/full_description.txt`
- Create: `fastlane/metadata/android/hi-IN/title.txt`
- Create: `fastlane/metadata/android/hi-IN/short_description.txt`
- Create: `fastlane/metadata/android/hi-IN/full_description.txt`
- Create: `distribution/whatsnew/whatsnew-en-US`

- [x] **Step 1: Generate Fastlane store metadata directory and default en-US listing**
- [x] **Step 2: Generate localized store listings for es-ES, fr-FR, de-DE, and hi-IN**
- [x] **Step 3: Create release notes `distribution/whatsnew/whatsnew-en-US`**

---

### Task 3: Legal & Store Compliance Documentation

**Files:**
- Create: `docs/22_PRIVACY_POLICY.md`
- Create: `docs/23_TERMS_OF_SERVICE.md`
- Create: `distribution/release_checklist.md`

- [x] **Step 1: Write `docs/22_PRIVACY_POLICY.md` covering offline data safety, guest auth, ads, and children's privacy**
- [x] **Step 2: Write `docs/23_TERMS_OF_SERVICE.md` covering fair play, virtual currency, and IAP licenses**
- [x] **Step 3: Create `distribution/release_checklist.md` with 7 pre-flight release gates**

---

### Task 4: Automated Release Readiness Validation Test

**Files:**
- Create: `core/common/src/test/kotlin/game/ludora/core/common/release/ReleaseReadinessTest.kt`
- Create: `scripts/verify_release_readiness.py`

- [x] **Step 1: Implement `ReleaseReadinessTest` testing character limits across all 5 locales**
- [x] **Step 2: Test compliance document sections and privacy policy clauses**
- [x] **Step 3: Test R8 Proguard rules coverage for core packages**
- [x] **Step 4: Execute test runner script to verify all tests pass**

---

### Task 5: Documentation, Decision Log & Remote Push

**Files:**
- Modify: `docs/21_DECISIONS.md`
- Modify: `CHANGELOG.md`
- Modify: `docs/19_ROADMAP.md`
- Modify: `docs/superpowers/plans/2026-10-01-phase-12-release.md`

- [x] **Step 1: Record Phase 12 architecture decision in `docs/21_DECISIONS.md`**
- [x] **Step 2: Update `CHANGELOG.md` with Phase 12 release features**
- [x] **Step 3: Update `docs/19_ROADMAP.md` status to Complete**
- [x] **Step 4: Check off all tasks in `docs/superpowers/plans/2026-10-01-phase-12-release.md`**
- [x] **Step 5: Git commit and push to `origin/main` with `BypassSandbox: true`**
