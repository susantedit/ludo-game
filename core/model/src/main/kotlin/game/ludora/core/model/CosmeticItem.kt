package game.ludora.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class CosmeticCategory {
    DICE_SKIN,
    TOKEN_STYLE,
    BOARD_THEME
}

@Serializable
data class CosmeticItem(
    val id: String,
    val name: String,
    val category: CosmeticCategory,
    val description: String,
    val unlockLevel: Int = 1,
    val coinPrice: Long = 0L,
    val previewHex: String = "#FFFFFF"
) {
    companion object {
        // Pre-defined Dice Skins
        val DICE_CLASSIC = CosmeticItem(
            id = "dice_classic",
            name = "Classic Bone",
            category = CosmeticCategory.DICE_SKIN,
            description = "Traditional white cubic die with dark slate pips",
            unlockLevel = 1,
            coinPrice = 0L,
            previewHex = "#FFFFFF"
        )
        val DICE_GOLDEN_EMBER = CosmeticItem(
            id = "dice_golden_ember",
            name = "Golden Ember",
            category = CosmeticCategory.DICE_SKIN,
            description = "Rich amber-gold metallic face with polished finish",
            unlockLevel = 2,
            coinPrice = 150L,
            previewHex = "#F59E0B"
        )
        val DICE_CYBER_NEON = CosmeticItem(
            id = "dice_cyber_neon",
            name = "Cyber Neon",
            category = CosmeticCategory.DICE_SKIN,
            description = "Deep violet face with glowing cyan pip insets",
            unlockLevel = 5,
            coinPrice = 400L,
            previewHex = "#8B5CF6"
        )

        // Pre-defined Token Styles
        val TOKEN_CLASSIC = CosmeticItem(
            id = "token_classic",
            name = "Gloss Disc",
            category = CosmeticCategory.TOKEN_STYLE,
            description = "High-contrast tactile token with crisp edge bevels",
            unlockLevel = 1,
            coinPrice = 0L,
            previewHex = "#EF4444"
        )
        val TOKEN_ROYAL_CROWN = CosmeticItem(
            id = "token_royal_crown",
            name = "Royal Crown",
            category = CosmeticCategory.TOKEN_STYLE,
            description = "Embossed crest with high-luster golden rim",
            unlockLevel = 3,
            coinPrice = 250L,
            previewHex = "#EAB308"
        )

        // Pre-defined Board Themes
        val BOARD_OBSIDIAN = CosmeticItem(
            id = "board_obsidian",
            name = "Obsidian Dark",
            category = CosmeticCategory.BOARD_THEME,
            description = "Deep dark mode slate palette tailored for low eye strain",
            unlockLevel = 1,
            coinPrice = 0L,
            previewHex = "#0F172A"
        )
        val BOARD_WARM_WOOD = CosmeticItem(
            id = "board_warm_wood",
            name = "Nordic Pine",
            category = CosmeticCategory.BOARD_THEME,
            description = "Warm organic wood grain tones with satin varnish",
            unlockLevel = 4,
            coinPrice = 300L,
            previewHex = "#78350F"
        )

        val ALL_COSMETICS = listOf(
            DICE_CLASSIC, DICE_GOLDEN_EMBER, DICE_CYBER_NEON,
            TOKEN_CLASSIC, TOKEN_ROYAL_CROWN,
            BOARD_OBSIDIAN, BOARD_WARM_WOOD
        )
    }
}
