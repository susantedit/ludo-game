package game.ludora.engine.remix.model

import kotlinx.serialization.Serializable

@Serializable
enum class ChaosModifier(val displayName: String, val description: String) {
    NONE("Normal Round", "Standard rules apply"),
    DOUBLE_ROLL("Speed Surge", "All players gain +2 to their dice rolls!"),
    HAZARD_RUSH("Hazard Rush", "Ladders launch +4 extra steps! Snakes bite deeper!"),
    SHIELD_FRENZY("Shield Frenzy", "All active tokens gain temporary immunity this round!")
}
