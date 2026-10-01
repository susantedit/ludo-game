# Ads and Monetization Architecture

## Core Monetization Rules
1. Absolute Offline Freedom: Offline gameplay must not require advertisements or network access. An offline player must experience zero ad interruptions, zero loading delays, and zero degraded features.
2. Zero Mid-Game Interruption: Advertisements must never appear during an active turn, roll sequence, or token movement. Ads may only appear at clean natural transition boundaries (e.g. game over summary screen).
3. Fair Play Integrity: Monetization is strictly cosmetic or convenience-based. Players cannot purchase extra rolls, rigged dice, or competitive advantages.

## Offline Guarantee
- When the device is offline, ad SDKs remain dormant.
- The app suppresses banner containers entirely rather than leaving blank or placeholder boxes.
- Rewarded ad buttons display an offline badge or hide automatically, informing the player that bonus cosmetic rewards require an active internet connection.

## Online Ad Behavior
Ads load conditionally only when:
1. The device reports confirmed internet connectivity.
2. The user has agreed to the required privacy and consent frameworks.
3. The user has not purchased an Ad-Free upgrade pass.

## Banner Ads
- Placement: Bottom of the main menu / home screen dashboard only.
- Suppressed on: Active game boards (Ludo, Snake & Ladder, Remix), room lobby screens, settings, and profile screens.
- Refresh interval: Configured strictly according to ad network policies (typically 30 to 60 seconds) and paused when the screen is hidden.

## Rewarded Ads
- Always 100% Opt-In: Players explicitly choose to watch a video ad in exchange for a documented benefit.
- Permitted rewards:
  - Small bonus of soft currency (Coins) for unlocking cosmetic skins.
  - One daily quest reroll.
  - Cosmetic mystery box spin.
- Hard prohibition: Rewarded ads can never be used to revive eliminated tokens, grant extra turns, or influence match outcomes.

## Interstitial Ads
- Placement: Strictly displayed on the match completion screen, after victory animations and stat calculations finish.
- Triggers: Never triggered after every single match.
- Cooldown: Enforces a strict minimum spacing of at least 8 minutes and 2 completed matches between interstitials.
- Suppression: Interstitials are suppressed for new players during their first 3 sessions to ensure a positive onboarding experience.

## Frequency Limits and Cooldowns
- Maximum Interstitials: 3 per hour per player.
- Maximum Rewarded Ads: 5 per 24-hour cycle to prevent economy inflation and ad fatigue.
- Cooldown timer state persists locally in Jetpack DataStore to prevent resetting limits by restarting the app.

## Ad-Free Gameplay Considerations
- One-Time Purchase: Provide an optional "Remove Ads" in-app purchase.
- Scope of Ad-Free:
  - Permanently removes all banner ads.
  - Permanently removes all post-game interstitial ads.
  - Retains optional rewarded ads so the player can still claim optional coin bonuses if desired.

## Reward Verification Workflow
- Ad grants follow the Server-Side Verification (SSV) workflow outlined in `11_SECURITY_PLAN.md`.
- In offline scenarios, rewarded ads are unavailable because cryptographic server verification cannot execute.
- Verification failures fail safely: Log the incident and notify the player without corrupting local profile data.

## Ad Provider Abstraction Layer
Feature code must never directly import Google Mobile Ads (AdMob), AppLovin, or Unity Ads SDK classes. All ad interactions occur through a clean provider interface:

```kotlin
interface AdProvider {
    fun initialize(context: Context)
    fun isOnline(): Boolean
    fun canShowBanner(): Boolean
    fun showBanner(container: ViewGroup)
    fun hideBanner()
    fun canShowInterstitial(): Boolean
    fun showInterstitial(activity: Activity, onClosed: () -> Unit)
    fun canShowRewarded(): Boolean
    fun showRewarded(activity: Activity, onRewardEarned: (Reward) -> Unit, onFailed: () -> Unit)
}
```

This abstraction allows swapping or aggregating ad networks without touching game engine or UI code.

## Privacy and Consent Regulations
- Full compliance with Google Play Families Policy, GDPR, CCPA, and regional privacy mandates.
- Integration of Google User Messaging Platform (UMP) or equivalent Consent Management Platform (CMP).
- Consent collection occurs during initial online onboarding before requesting any advertising identifiers or tracking tokens.

## What Happens When the Network Disappears
- If internet drops during a match, any pending ad requests cancel immediately without blocking the UI.
- The game flow moves smoothly from the victory screen directly to the main menu without pausing for timeouts.
- In-flight banner views dismiss and collapse their layout footprint cleanly.
