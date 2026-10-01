# Design System Specification

## Brand
Ludora blends classic board game heritage with modern tactile design. The aesthetic avoids cheap neon glows and generic template gradients, focusing instead on deep, rich surface tones, distinct color coding, crisp typography, and purposeful physical feedback.

Tagline: Where every roll matters.

## Ludora Logo Usage
- Primary Mark: The stylized "Ludora" logotype paired with the geometric dice crown emblem (`logo.png`).
- Safe Margin: Minimum clear space around the logo equal to 50% of the emblem's width.
- Minimum Size: Never render smaller than 32dp height in digital application headers.
- Background Treatment: Displays cleanly over dark slate (`#121620`) and warm dark neutral surfaces.

## Color Palette and Tokens

### Core Brand Tokens
- Primary: Deep Indigo (`#3D5AFE`)
- Primary Container: Midnight Navy (`#1E293B`)
- Secondary: Warm Amber Gold (`#FFB300`)
- Surface Dark: Deep Slate (`#0F172A`)
- Surface Elevated: Slate Card (`#1E293B`)
- Background: Obsidian Black (`#0B0F19`)

### Player Palette (Harmonized, High-Contrast)
- Player 1 (Red): Crimson Vermilion (`#E53935`)
- Player 2 (Green): Emerald Jade (`#00C853`)
- Player 3 (Yellow): Golden Honey (`#FFD600`)
- Player 4 (Blue): Electric Sapphire (`#2979FF`)

## Typography
Uses clean modern geometric sans-serif typefaces (Outfit or Inter) for optimal legibility across mobile displays:
- Display Large: 32sp, Bold, Line Height 40sp. Used for victory screens and level-ups.
- Title Large: 22sp, SemiBold, Line Height 28sp. Used for screen titles.
- Title Medium: 18sp, Medium, Line Height 24sp. Used for card headers.
- Body Large: 16sp, Regular, Line Height 24sp. Primary UI body text.
- Body Medium: 14sp, Regular, Line Height 20sp. Secondary labels and stats.
- Label Large: 14sp, SemiBold, Line Height 20sp. Button labels and interactive chips.
- Dice Numeric: 20sp, ExtraBold. Custom numerals for dice surfaces and score counters.

## Spacing and Grid
Based on a flexible 4dp/8dp incremental grid:
- `space_xxs`: 2dp
- `space_xs`: 4dp
- `space_sm`: 8dp
- `space_md`: 16dp (Standard screen margin and component spacing)
- `space_lg`: 24dp
- `space_xl`: 32dp
- `space_xxl`: 48dp

## Card Components
- Container Shape: Rounded corners with 16dp corner radius (`RoundedCornerShape(16.dp)`).
- Elevation: 2dp resting elevation with subtle 1dp border stroke (`#334155`).
- Padding: 16dp standard interior padding.
- Interactive Feedback: 1.02x scale transition on press with background brightness elevation.

## Button Components
- Primary Action Button: Filled with Primary Indigo or Accent Gold, 48dp minimum touch height, 12dp corner radius.
- Secondary Button: Outlined with 1.5dp stroke, transparent container, high-contrast label.
- Icon Button: 48x48dp interactive touch target with centered 24dp vector icon.
- Sound & Feedback: Every button press triggers a crisp audio click and light haptic feedback.

## Navigation Components
- Top App Bar: Compact 56dp height featuring back navigation icon, title, and contextual status chips.
- Bottom Bar: Floating navigation pill container elevated 16dp above screen bottom with active icon indicators.

## Game Pieces and Tokens
- Construction: 2.5D layered circular tokens featuring a high-contrast rim, primary player color fill, and central unique symbol.
- Differentiation: Each player color pairs with a unique geometric shape symbol (Red: Circle, Green: Triangle, Yellow: Diamond, Blue: Square) to guarantee accessibility for color-blind players.
- Resting State: Subtle drop shadow communicating elevation above board cells.
- Active State: Pulsing ring animation and 4dp elevation lift indicating the piece is legal and selectable.

## Dice System
- Dimensions: 64x64dp on standard mobile screens; 80x80dp on tablets.
- Appearance: Rounded cube (12dp corner radius) with recessed, painted pip dots or clean numeric typography.
- Shading: Ambient lighting gradients giving clear physical depth.
- Roll Surface: Dedicated dice roll tray with soft bounding walls to contain animated roll physics.

## Iconography
- System: Material Symbols / Lucide Vector Icons rendered at 24dp standard viewport.
- Stroke Weight: Consistent 2dp stroke width across all custom icons.
- Usage: Every icon pairs with a descriptive text label or content description tag for accessibility.

## Shadows and Elevation
- Level 0 (Flat): Board surface and circuit cells (0dp).
- Level 1 (Resting): Static UI cards and inactive tokens (2dp elevation, soft ambient shadow).
- Level 2 (Floating): Active selectable tokens and floating action buttons (6dp elevation).
- Level 3 (Modal): Dialogs, bottom sheets, and victory popups (16dp elevation with 60% black scrim).

## Borders and Outlines
- Default Border: 1dp solid `#334155` for component grouping without visual noise.
- Active Highlight Border: 2dp solid `#FFB300` for focused cards and active player turn frames.

## Interactive States
- Normal / Idle: Default token color and elevation.
- Pressed: 0.96x micro-scale compression with immediate haptic response.
- Disabled: 38% alpha opacity with touch interactions disabled.
- Highlighted / Valid Move: Gentle breathing scale animation (1.0x to 1.08x) with soft glow.

## Accessibility Standards
- Minimum touch target size: 48x48dp for all interactive elements.
- Text contrast ratio: Minimum 4.5:1 for body copy; minimum 3:1 for large display titles against their backgrounds.
- Non-text contrast: Minimum 3:1 for player tokens against all board cell backgrounds.

## Theme Modes (Dark and Light Considerations)
- Default Theme: Deep Obsidian Dark Mode optimized for battery conservation and long play sessions.
- Light Theme Consideration: High-legibility Warm Ivory mode designed for outdoor play in direct sunlight: TBD - requires a later architectural decision.
- The design system will utilize the `ui-ux-pro-max` skill during later implementation phases to define exact tokens and CSS/Compose assets.
