# Ludora Privacy Policy

Last Updated: October 1, 2026
Effective Date: October 1, 2026

## 1. Overview
Ludora ("we", "our", or "us") is an offline-first mobile board game application. We value player privacy and minimize data collection. Most features in Ludora run entirely on your device without transmitting data over the internet.

## 2. Information We Collect

### A. Offline Gameplay Data
When you play Ludora offline, all game states, player profiles, statistics, daily quest progress, unlocked cosmetics, and match histories are stored locally on your device in an encrypted SQLite database using Android Jetpack Room. This data never leaves your device unless you manually export or backup your device.

### B. Online Multiplayer Data
If you use online multiplayer features (such as Quick Match or Private Rooms):
- **Guest Account Identifier**: A randomly generated UUID string is created to manage your online session. We do not require an email address, phone number, or social media login.
- **Matchmaking Metadata**: Ephemeral data including your match rating (MMR), connection status, and room code are processed in memory during the match.
- **Turn Actions**: Move inputs (dice rolls, token selections, power card activations) are transmitted over secure WebSockets (WSS) to synchronize board state between players.

### C. Advertising Data
Ludora does not show ads during active gameplay. If you choose to watch an optional rewarded ad or view non-gameplay menus:
- Advertising is served through reputable ad networks (such as Google Mobile Ads).
- These networks may process anonymous device identifiers (such as the Google Advertising ID), IP address, and general device information to deliver ads according to your device privacy settings.
- If you purchase the "Remove Ads" pass, banner and interstitial ad requests are permanently disabled.

## 3. How We Use Information
We use collected data solely to:
- Maintain and synchronize active multiplayer game sessions.
- Prevent cheating and maintain fair turn timers in online matches.
- Save your local progression, level milestones, and unlocked items.
- Deliver non-intrusive advertisements outside of active gameplay.

## 4. Data Sharing and Third Parties
We do not sell, rent, or trade your personal data. We only share information with third parties in the following limited contexts:
- **Google Play Services**: To process in-app purchases ("Remove Ads" pass) and handle application distribution.
- **Ad Network Partners**: To serve advertisements when online, strictly in accordance with their privacy policies.
- **Legal Compliance**: If required by applicable law, regulation, or legal process.

## 5. Children's Privacy (COPPA and Families Policy)
Ludora is designed for all audiences. We comply with the Children's Online Privacy Protection Act (COPPA) and Google Play Families Policies:
- We do not knowingly collect personal identifiable information from children under 13.
- In child-directed modes or when an age below 13 is indicated, personalized advertising is completely disabled and only contextual ads compliant with Google Play Families Self-Certified Ads SDKs are served.

## 6. Data Retention and Deletion
- **Local Data**: You can reset your statistics, profile, and local data at any time through the in-app profile settings or by clearing the app data in Android system settings.
- **Online Match Data**: Multiplayer session packets and room states are stored ephemerally in memory and are discarded immediately after match completion or room expiration.

## 7. Security
We implement standard security practices including HTTPS/WSS encryption in transit, isolated application sandboxing, and cryptographically secure pseudorandom numbers for dice rolls and room codes.

## 8. Contact Us
If you have questions about this Privacy Policy or your data, contact us at:
- **Email**: privacy@ludora.game
- **Project Repository**: https://github.com/susantedit/ludo-game
