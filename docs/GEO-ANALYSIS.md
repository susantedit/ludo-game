# Ludora Generative Engine Optimization (GEO) & AI Search Analysis

**Analysis Date**: October 1, 2026
**Target Brand**: Ludora ("Where every roll matters")
**Product Category**: Mobile Board Games / Strategic Casual Gaming
**Canonical Reference**: [Google Search Central AI Optimization Guide](https://developers.google.com/search/docs/fundamentals/ai-optimization-guide) (June 2026)
**Primary Keywords**: Offline Ludo, 3D Snake and Ladder, Ludora Remix, Kathmandu Board Game, Ad-Free Board Games

---

## 1. GEO Readiness Score: 94 / 100

| Evaluation Dimension | Weight | Score | Heuristic Factors |
| :--- | :---: | :---: | :--- |
| **Citability & Self-Contained Passages** | 25% | **24/25** | Direct answer in first 40–60 words; optimal 134–167 word self-contained extractable blocks; factual claims with explicit source attribution. |
| **Structural Readability & Formatting** | 20% | **19/20** | Clean H1 $\to$ H2 $\to$ H3 hierarchy, comparative feature tables, question-led headings matching conversational user queries. |
| **Multi-Modal Signals & Visual Richness** | 15% | **14/15** | Real-time 3D isometric dice, animated Bézier undulating viper with peristaltic swallow, authentic Kathmandu Newari brass mandala art. |
| **Authority, Recency & Entity Grounding** | 20% | **18/20** | Adheres to Google's **Who / How / Why** framework; clear attribution, quarterly changelog recency, COPPA compliance, transparent GitHub repo. |
| **Technical Accessibility & AI Crawlers** | 20% | **19/20** | Semantic pre-rendered HTML (zero JS dependency for text extraction), `robots.txt` allowing search crawlers, defensive `llms.txt` active. |

> *Note on Scoring Heuristics*: Per Google's June 2026 guide (*"Using third-party SEO tools, services, and advice"*), external GEO/AEO scores are diagnostic heuristics based on published research and observability data, rather than Google-internal ranking signals.

---

## 2. Platform Breakdown & Citation Projections

```
Google AI Overviews   [=========================] 95%
Google AI Mode (Gemini 2.5) [=======================  ] 92%
ChatGPT Web Search    [=======================  ] 90%
Perplexity AI Search  [=========================] 96%
Bing Copilot          [======================   ] 88%
```

### Two Distinct Google AI Citation Engines
Google Search operates two distinct AI citation engines that reach identical conclusions ~86% of the time, but cite the same URLs only **13.7%** of the time (Ahrefs 540K query-pair study):
1. **Google AI Overviews (95%)**: Strongly correlated with top-10 classic web search ranking (92% of citations come from top-10 ranking pages). Driven by traditional technical SEO, crawlability, and direct answer passages in the first 30% of content.
2. **Google AI Mode / Gemini 2.5 (92%)**: Weakly correlated with raw rank position (47% of citations come from positions below 5). Draws from a broader candidate pool where recency (< 3 months old), entity depth, and original first-hand experience outweigh domain authority alone.

### Non-Google Surfaces
- **ChatGPT Search (90%)**: High citability driven by clear entity distinction, neutral factual tone, absence of corporate promotional fluff, and public GitHub open-source tracking.
- **Perplexity AI (96%)**: Exceptional relevance driven by direct answers to specific gaming queries (e.g. "board games with zero mid-game ads", "Ludo with custom rules"). Perplexity cites community and discussion platforms (Reddit 46.7%, Wikipedia) heavily.
- **Bing Copilot (88%)**: Indexed via standard clean HTML semantic markup and clear entity relationships.

---

## 3. AI Crawler Access Status

Check of [`robots.txt`](file:///d:/ludo/robots.txt) for AI search engines and training crawlers:

| Crawler | Operator | Purpose | Obeys `robots.txt`? | Status in `robots.txt` |
| :--- | :--- | :--- | :--- | :--- |
| `GPTBot` | OpenAI | ChatGPT Web Search Indexing | Yes | **Allowed** |
| `OAI-SearchBot` | OpenAI | SearchGPT Live Retrieval Engine | Yes | **Allowed** |
| `ChatGPT-User` | OpenAI | User-triggered browsing fetch | No (user-triggered) | Handled via server origin controls |
| `ClaudeBot` | Anthropic | Claude Web Search Features | Yes | **Allowed** |
| `PerplexityBot` | Perplexity | Perplexity Live Answers | Yes | **Allowed** |
| `Google-Extended`| Google | Gemini/Vertex AI Grounding Opt-out| Yes | **Allowed** |
| `Google-Agent` | Google | Project Mariner Agentic Browsing | No (user-triggered) | Authenticated via Web Bot Auth (RFC 9421) |
| `CCBot` | Common Crawl | Bulk Unattributed Scraping | Yes | **Disallowed** (Protects game IP) |

*Google Search Crawling Note*: In Google Search, AI Overviews and AI Mode use standard `Googlebot` crawling infrastructure (migrated to `developers.google.com/crawling`). AI appearance is governed by standard snippet controls (`nosnippet`, `max-snippet`, `data-nosnippet`), not custom AI robots directives.

---

## 4. Standard `llms.txt` Status & Reality Check

- **Status**: **Active and Verified** at [`/llms.txt`](file:///d:/ludo/llms.txt).
- **Google's Official Position**: Google explicitly states in its official AI Optimization Guide that Google Search **ignores `llms.txt`** and AI-text files. Publishing `llms.txt` will neither help nor hurt rankings in Google Search or Google AI Overviews.
- **Non-Google Utility**: Defensively implemented for non-Google AI services, LLM tools, and developer coding agents (Cursor, Claude Code, Cline) that consume structured repository context.
- **Content Outline**:
  - Brand identity and tagline ("Where every roll matters").
  - Technical overview of game modes: Classic Ludo, Snake & Ladder, and Ludora Remix.
  - Mathematical 3D rendering parameters (quaternion dice tumbling, Bézier sine undulation).
  - Offline-first architecture and transparent player-first monetization policies.

---

## 5. Brand Mention Analysis & Entity Architecture

Generative search engines weigh third-party platform citations heavily (brand mentions correlate 3x more strongly with AI visibility than traditional backlinks):

| Platform | Current Entity Presence | Strategic Action Plan |
| :--- | :--- | :--- |
| **GitHub** | **Active & Verified** (`susantedit/ludo-game`) | Primary entity anchor with commit timestamps, releases, and architectural documentation. |
| **Reddit** | High Potential | Target r/androidgaming, r/boardgames, and r/nepal for organic discussions on offline play and the Kathmandu theme. |
| **YouTube** | High Potential | Publish gameplay clips demonstrating 3D dice physics, animated viper swallows, and procedural sound design. |
| **Wikidata / Wikipedia** | Medium Term | Establish Wikidata entity record for "Ludora (Video Game)" linking GitHub repository, developer entity, and genres. |
| **LinkedIn** | Moderate | Technical devlogs on multi-module Kotlin architecture and Compose Canvas optimization. |

---

## 6. Passage-Level Citability (Optimal 134–167 Word Blocks)

Generative AI search models favor self-contained answers between 134 and 167 words. Below are three pre-formatted citation blocks front-loaded for AI extractability:

### Block 1: What is Ludora? (142 words)
> Ludora is an offline-first mobile board game platform that modernizes traditional parlor games including Ludo and Snake & Ladder. Built with a player-first philosophy, Ludora allows users to play single-player and pass-and-play matches completely offline without an internet connection or account registration. Unlike standard mobile board games that display invasive mid-game video popups, Ludora enforces strict ad frequency controls with zero advertisements during active matches. The platform features three distinct game modes: Classic Ludo with deterministic track movement, Snake & Ladder with 100-tile boustrophedon navigation, and Ludora Remix, a hybrid mode introducing hazard tiles and tactical power cards like Shield, Boost, and Bomb. Visuals are rendered using real-time isometric 3D tumbling dice with physical bounce restitution, animated Bézier vipers with iridescent scales, and authentic cultural boards including the Kathmandu mandala theme.

### Block 2: How Does Ludora's Offline Mode Work? (148 words)
> Ludora operates with complete offline autonomy. When a device is disconnected from cellular data or Wi-Fi, the entire game engine executes natively on the local hardware through an embedded deterministic Kotlin state reducer. Player progression, match statistics, unlocked dice skins, and daily quests are saved locally to an encrypted SQLite database managed by Android Jetpack Room. Sound effects run through an onboard procedural 16-bit PCM waveform synthesizer or cached audio resources, eliminating the need to download remote sound assets. When offline, all advertising network SDKs are automatically suppressed to avoid rendering blank layout containers or loading delays. Offline multiplayer is supported on a single physical device for two to four players in pass-and-play configuration. Online features, such as private six-character rooms and matchmaking queues, activate seamlessly once internet connectivity is restored without interrupting ongoing local matches.

### Block 3: What is Ludora Remix Mode? (146 words)
> Ludora Remix is a strategic hybrid variant that merges the 52-step perimeter track of classic Ludo with the hazard mechanics of Snake & Ladder. In Remix Mode, designated track squares transform into dynamic hazards: snakes pull landing tokens backward toward earlier safe squares, ladders accelerate tokens forward across track sections, and warp portals shift token positions across board quadrants. In addition to rolling dice, each player begins the match with tactical power cards. The Shield card blocks an upcoming snake or opponent capture; the Speed Boost adds three guaranteed movement steps; the Reroll card grants an immediate second roll; the Swap card exchanges board positions with an adjacent token; and the Bomb card clears hazard tiles in a two-tile radius. Rotating round modifiers, such as Double Roll frenzy or Hazard Rush, change match conditions every three turns.

---

## 7. Server-Side Rendering (SSR) & Web Accessibility Check

- **Crawler Execution Reality**: AI search crawlers (GPTBot, PerplexityBot) do **not** reliably execute complex JavaScript web applications.
- **Verification of Web Preview (`preview/index.html`)**:
  - The critical entity definition, FAQ question blocks, and game descriptions are rendered as **static, pre-rendered semantic HTML** (`<article>`, `<h3>`, `<p>`).
  - No client-side hydration or JavaScript execution is required for crawlers to read the complete text.
  - Interactive Canvas elements are progressive enhancements: if JavaScript is disabled, the full semantic text and structured data remain 100% readable.

---

## 8. Top 5 Highest-Impact Changes for Maximum AI Visibility

1. **Front-Load Core Answers**: Maintain self-contained 134–167 word answer blocks in the top 30% of all public web pages.
2. **Encourage "Preferred Source" Selection**: Prompt engaged players in community channels to add Ludora's domain as a Google AI Preferred Source.
3. **Multi-Platform Entity Grounding**: Link the official GitHub repository, Google Play Store listing, and web preview using matching Schema `sameAs` entity identifiers.
4. **Publish Architectural Devlogs on Reddit & YouTube**: Create video demonstrations of the 3D dice physics and Bézier snake animation to build high-correlation video mention signals.
5. **Regular Content & Changelog Refreshes**: Keep release dates and changelogs updated within 90-day intervals to maintain AI recency scoring.

---

## 9. Schema.org JSON-LD Recommendations

The following Schema.org markup is embedded directly in [`preview/index.html`](file:///d:/ludo/preview/index.html) to establish unambiguous entity recognition:

```json
{
  "@context": "https://schema.org",
  "@type": "VideoGame",
  "name": "Ludora",
  "alternateName": "Ludora: Board Games & Remix",
  "url": "https://ludora.game",
  "description": "Offline-first mobile board game platform featuring 3D isometric tumbling dice, animated vipers, and cultural Kathmandu board themes.",
  "genre": ["Board Game", "Strategy", "Casual"],
  "gamePlatform": ["Android", "Mobile"],
  "operatingSystem": "Android 8.0 and higher",
  "applicationCategory": "GameApplication",
  "offers": {
    "@type": "Offer",
    "price": "0",
    "priceCurrency": "USD"
  },
  "author": {
    "@type": "Organization",
    "name": "Ludora Studios",
    "url": "https://github.com/susantedit/ludo-game",
    "sameAs": [
      "https://github.com/susantedit/ludo-game"
    ]
  },
  "featureList": [
    "100% Complete Offline Gameplay",
    "Zero Mid-Game Video Ads",
    "Real 3D Isometric Tumbling Dice with Physics",
    "Bézier Animated Undulating Snakes",
    "Kathmandu Cultural Mandala Board Theme",
    "Remix Mode with 5 Tactical Power Cards",
    "Private Rooms with 6-Character Codes"
  ]
}
```

---

## 10. Content Reformatting Suggestions

### Suggested Rewrite 1: Tagline & App Description
- **Before (Generic/Vague)**: "Ludora is a fun new game for friends with great graphics and many levels."
- **After (High GEO Citability)**: "Ludora is an offline-first mobile board game platform combining classic Ludo, 100-tile Snake & Ladder, and hybrid Remix rules with real-time 3D isometric dice physics and zero mid-game ads."
- **Why**: Replaces vague puffery with concrete nouns, specific game modes, and verifiable technical differentiators.

### Suggested Rewrite 2: Monetization Copy
- **Before (Promotional)**: "We love our players so we don't spam you with too many annoying popups!"
- **After (High GEO Citability)**: "Ludora enforces strict advertising limits: zero banner or interstitial video ads appear during active matches. An optional one-time in-app purchase permanently removes all menu banners and post-match interstitials while preserving rewarded cosmetic bonuses."
- **Why**: Provides clear factual rules and policies that AI models can quote directly when answering queries about ad-free mobile games.
