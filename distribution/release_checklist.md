# Ludora Production Release Deployment Checklist

Every release build published to Google Play must pass all seven pre-flight verification gates below before rollout.

---

## Gate 1: Application Version & Identification
- [ ] **Package Namespace**: `game.ludora`
- [ ] **Release Version Code**: Incremented monotonically (Release 1.0.0 uses `versionCode = 100`).
- [ ] **Release Version Name**: Semantic versioning compliant (e.g., `1.0.0`).
- [ ] **Target SDK**: Android 14 (API 34).
- [ ] **Minimum SDK**: Android 8.0 Oreo (API 26).
- [ ] **Compile SDK**: Android 14 (API 34).

## Gate 2: Signing & Keystore Security
- [ ] Release signing certificate generated using 4096-bit RSA or EC P-256 key.
- [ ] No hardcoded keystore passwords or private keys in the Git repository.
- [ ] Keystore credentials provided via environment variables (`KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`).
- [ ] Google Play App Signing (Play Integrity) enrollment confirmed.

## Gate 3: R8 Optimization & Code Shrinking
- [ ] `isMinifyEnabled = true` verified in release build type.
- [ ] `isShrinkResources = true` verified in release build type.
- [ ] `app/proguard-rules.pro` includes keep rules for:
  - Domain models (`game.ludora.core.model.**`)
  - Engine state reducers and boards (`game.ludora.engine.**`)
  - Kotlinx Serialization serializers and polymorphic models
  - Room entities, DAOs, and migrations
  - Compose runtime and Material3 components
- [ ] Proguard mapping file (`mapping.txt`) archived alongside the Android App Bundle (AAB) artifact.
- [ ] Stack trace deobfuscation tested with mapping file.

## Gate 4: Permissions & Manifest Security Audit
- [ ] Explicit permissions declared in `AndroidManifest.xml`:
  - `android.permission.INTERNET` (strictly for online multiplayer and ads when connected)
  - `android.permission.ACCESS_NETWORK_STATE` (for connectivity checks)
  - `android.permission.VIBRATE` (for tactile haptic feedback)
- [ ] Zero unnecessary permissions requested (no `READ_CONTACTS`, `ACCESS_FINE_LOCATION`, `CAMERA`, or storage permissions).
- [ ] `android:exported="true"` applied exclusively to `MainActivity` with `MAIN`/`LAUNCHER` intent filters.
- [ ] App orientation locked to `portrait`.
- [ ] `android:allowBackup="true"` scoped by `data_extraction_rules.xml` and `backup_rules.xml`.

## Gate 5: Offline-First Reliability Verification
- [ ] Application cold start tested with airplane mode enabled:
  - App opens directly to home dashboard without blocking spinners or error dialogues.
  - Local SQLite database loads without internet requirements.
  - Pass-and-play Ludo, Snake & Ladder, and Remix Mode play completely offline.
  - Zero banner ad boxes or placeholder blanks rendered when offline.
  - Zero ad requests dispatched when network is disconnected.

## Gate 6: Store Metadata & Localization Audit
- [ ] Fastlane metadata present and validated:
  - `fastlane/metadata/android/en-US/title.txt` (<= 30 characters)
  - `fastlane/metadata/android/en-US/short_description.txt` (<= 80 characters)
  - `fastlane/metadata/android/en-US/full_description.txt` (<= 4000 characters)
- [ ] Localized listings complete for `es-ES`, `fr-FR`, `de-DE`, and `hi-IN`.
- [ ] Release notes created at `distribution/whatsnew/whatsnew-en-US`.
- [ ] Privacy Policy published at `docs/22_PRIVACY_POLICY.md`.
- [ ] Terms of Service published at `docs/23_TERMS_OF_SERVICE.md`.
- [ ] Google Play Data Safety form completed accurately reflecting local storage and optional advertising.

## Gate 7: Performance Budgets & Operational Monitoring
- [ ] App bundle download size <= 25MB.
- [ ] Cold start time on 2GB RAM baseline device <= 1,200ms.
- [ ] In-game frame rendering maintains 60fps on standard devices.
- [ ] Low-RAM fallback verified: reduced particle counts on devices with <= 2GB memory.
- [ ] ANR rate target < 0.47% (Google Play Core Vitals threshold).
- [ ] Crash rate target < 1.09% (Google Play Core Vitals threshold).
- [ ] Google Play staged rollout strategy configured: 10% -> 25% -> 50% -> 100%.
