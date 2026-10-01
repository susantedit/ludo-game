package game.ludora.core.designsystem.mascot

import kotlinx.serialization.Serializable

@Serializable
enum class MascotCategory {
    ANIMALS,
    PEOPLE,
    ROBOTS_AND_THINGS,
    SPECIAL_STYLES
}

@Serializable
data class MascotDefinition(
    val id: String,
    val name: String,
    val category: MascotCategory,
    val directionsAsset: String,
    val reactionsAsset: String
)

/**
 * Registry of all 56 official Ludora Page-Mascots.
 */
object MascotCatalog {

    val ALL: List<MascotDefinition> = listOf(
        // Animals (20)
        MascotDefinition("bear", "Bear", MascotCategory.ANIMALS, "mascots/bear-directions.webp", "mascots/bear-reactions.webp"),
        MascotDefinition("bunny", "Bunny", MascotCategory.ANIMALS, "mascots/bunny-directions.webp", "mascots/bunny-reactions.webp"),
        MascotDefinition("cat", "Cat", MascotCategory.ANIMALS, "mascots/cat-directions.webp", "mascots/cat-reactions.webp"),
        MascotDefinition("deer", "Deer", MascotCategory.ANIMALS, "mascots/deer-directions.webp", "mascots/deer-reactions.webp"),
        MascotDefinition("dino", "Dino", MascotCategory.ANIMALS, "mascots/dino-directions.webp", "mascots/dino-reactions.webp"),
        MascotDefinition("fox", "Fox", MascotCategory.ANIMALS, "mascots/fox-directions.webp", "mascots/fox-reactions.webp"),
        MascotDefinition("frog", "Frog", MascotCategory.ANIMALS, "mascots/frog-directions.webp", "mascots/frog-reactions.webp"),
        MascotDefinition("hamster", "Hamster", MascotCategory.ANIMALS, "mascots/hamster-directions.webp", "mascots/hamster-reactions.webp"),
        MascotDefinition("hedgehog", "Hedgehog", MascotCategory.ANIMALS, "mascots/hedgehog-directions.webp", "mascots/hedgehog-reactions.webp"),
        MascotDefinition("koala", "Koala", MascotCategory.ANIMALS, "mascots/koala-directions.webp", "mascots/koala-reactions.webp"),
        MascotDefinition("otter", "Otter", MascotCategory.ANIMALS, "mascots/otter-directions.webp", "mascots/otter-reactions.webp"),
        MascotDefinition("owl", "Owl", MascotCategory.ANIMALS, "mascots/owl-directions.webp", "mascots/owl-reactions.webp"),
        MascotDefinition("panda", "Panda", MascotCategory.ANIMALS, "mascots/panda-directions.webp", "mascots/panda-reactions.webp"),
        MascotDefinition("penguin", "Penguin", MascotCategory.ANIMALS, "mascots/penguin-directions.webp", "mascots/penguin-reactions.webp"),
        MascotDefinition("pug", "Pug", MascotCategory.ANIMALS, "mascots/pug-directions.webp", "mascots/pug-reactions.webp"),
        MascotDefinition("raccoon", "Raccoon", MascotCategory.ANIMALS, "mascots/raccoon-directions.webp", "mascots/raccoon-reactions.webp"),
        MascotDefinition("redpanda", "Red Panda", MascotCategory.ANIMALS, "mascots/redpanda-directions.webp", "mascots/redpanda-reactions.webp"),
        MascotDefinition("sheep", "Sheep", MascotCategory.ANIMALS, "mascots/sheep-directions.webp", "mascots/sheep-reactions.webp"),
        MascotDefinition("sloth", "Sloth", MascotCategory.ANIMALS, "mascots/sloth-directions.webp", "mascots/sloth-reactions.webp"),
        MascotDefinition("tiger", "Tiger", MascotCategory.ANIMALS, "mascots/tiger-directions.webp", "mascots/tiger-reactions.webp"),

        // People (18)
        MascotDefinition("afro", "Afro", MascotCategory.PEOPLE, "mascots/afro-directions.webp", "mascots/afro-reactions.webp"),
        MascotDefinition("astronaut", "Astronaut", MascotCategory.PEOPLE, "mascots/astronaut-directions.webp", "mascots/astronaut-reactions.webp"),
        MascotDefinition("bald", "Bald", MascotCategory.PEOPLE, "mascots/bald-directions.webp", "mascots/bald-reactions.webp"),
        MascotDefinition("ballerina", "Ballerina", MascotCategory.PEOPLE, "mascots/ballerina-directions.webp", "mascots/ballerina-reactions.webp"),
        MascotDefinition("beard", "Beard", MascotCategory.PEOPLE, "mascots/beard-directions.webp", "mascots/beard-reactions.webp"),
        MascotDefinition("builder", "Builder", MascotCategory.PEOPLE, "mascots/builder-directions.webp", "mascots/builder-reactions.webp"),
        MascotDefinition("cap", "Cap", MascotCategory.PEOPLE, "mascots/cap-directions.webp", "mascots/cap-reactions.webp"),
        MascotDefinition("chef", "Chef", MascotCategory.PEOPLE, "mascots/chef-directions.webp", "mascots/chef-reactions.webp"),
        MascotDefinition("glasses", "Glasses", MascotCategory.PEOPLE, "mascots/glasses-directions.webp", "mascots/glasses-reactions.webp"),
        MascotDefinition("grandpa", "Grandpa", MascotCategory.PEOPLE, "mascots/grandpa-directions.webp", "mascots/grandpa-reactions.webp"),
        MascotDefinition("granny", "Granny", MascotCategory.PEOPLE, "mascots/granny-directions.webp", "mascots/granny-reactions.webp"),
        MascotDefinition("hijabi", "Hijabi", MascotCategory.PEOPLE, "mascots/hijabi-directions.webp", "mascots/hijabi-reactions.webp"),
        MascotDefinition("nurse", "Nurse", MascotCategory.PEOPLE, "mascots/nurse-directions.webp", "mascots/nurse-reactions.webp"),
        MascotDefinition("pirate", "Pirate", MascotCategory.PEOPLE, "mascots/pirate-directions.webp", "mascots/pirate-reactions.webp"),
        MascotDefinition("scientist", "Scientist", MascotCategory.PEOPLE, "mascots/scientist-directions.webp", "mascots/scientist-reactions.webp"),
        MascotDefinition("sikh", "Sikh", MascotCategory.PEOPLE, "mascots/sikh-directions.webp", "mascots/sikh-reactions.webp"),
        MascotDefinition("skater", "Skater", MascotCategory.PEOPLE, "mascots/skater-directions.webp", "mascots/skater-reactions.webp"),
        MascotDefinition("wizard", "Wizard", MascotCategory.PEOPLE, "mascots/wizard-directions.webp", "mascots/wizard-reactions.webp"),

        // Robots & Things (13)
        MascotDefinition("clockwork", "Clockwork", MascotCategory.ROBOTS_AND_THINGS, "mascots/clockwork-directions.webp", "mascots/clockwork-reactions.webp"),
        MascotDefinition("crt", "CRT Monitor", MascotCategory.ROBOTS_AND_THINGS, "mascots/crt-directions.webp", "mascots/crt-reactions.webp"),
        MascotDefinition("cube", "Cube", MascotCategory.ROBOTS_AND_THINGS, "mascots/cube-directions.webp", "mascots/cube-reactions.webp"),
        MascotDefinition("drone", "Drone", MascotCategory.ROBOTS_AND_THINGS, "mascots/drone-directions.webp", "mascots/drone-reactions.webp"),
        MascotDefinition("gearbot", "Gearbot", MascotCategory.ROBOTS_AND_THINGS, "mascots/gearbot-directions.webp", "mascots/gearbot-reactions.webp"),
        MascotDefinition("knight", "Knight", MascotCategory.ROBOTS_AND_THINGS, "mascots/knight-directions.webp", "mascots/knight-reactions.webp"),
        MascotDefinition("lantern", "Lantern", MascotCategory.ROBOTS_AND_THINGS, "mascots/lantern-directions.webp", "mascots/lantern-reactions.webp"),
        MascotDefinition("postbot", "Postbot", MascotCategory.ROBOTS_AND_THINGS, "mascots/postbot-directions.webp", "mascots/postbot-reactions.webp"),
        MascotDefinition("radio", "Radio", MascotCategory.ROBOTS_AND_THINGS, "mascots/radio-directions.webp", "mascots/radio-reactions.webp"),
        MascotDefinition("rocket", "Rocket", MascotCategory.ROBOTS_AND_THINGS, "mascots/rocket-directions.webp", "mascots/rocket-reactions.webp"),
        MascotDefinition("scout", "Scout", MascotCategory.ROBOTS_AND_THINGS, "mascots/scout-directions.webp", "mascots/scout-reactions.webp"),
        MascotDefinition("toaster", "Toaster", MascotCategory.ROBOTS_AND_THINGS, "mascots/toaster-directions.webp", "mascots/toaster-reactions.webp"),
        MascotDefinition("tv", "Retro TV", MascotCategory.ROBOTS_AND_THINGS, "mascots/tv-directions.webp", "mascots/tv-reactions.webp"),

        // Fox Special Styles (5)
        MascotDefinition("fox-ink", "Fox (Ink)", MascotCategory.SPECIAL_STYLES, "mascots/fox-ink-directions.webp", "mascots/fox-ink-reactions.webp"),
        MascotDefinition("fox-sketch", "Fox (Sketch)", MascotCategory.SPECIAL_STYLES, "mascots/fox-sketch-directions.webp", "mascots/fox-sketch-reactions.webp"),
        MascotDefinition("fox-riso", "Fox (Riso)", MascotCategory.SPECIAL_STYLES, "mascots/fox-riso-directions.webp", "mascots/fox-riso-reactions.webp"),
        MascotDefinition("fox-paper", "Fox (Paper)", MascotCategory.SPECIAL_STYLES, "mascots/fox-paper-directions.webp", "mascots/fox-paper-reactions.webp"),
        MascotDefinition("fox-pixel", "Fox (Pixel)", MascotCategory.SPECIAL_STYLES, "mascots/fox-pixel-directions.webp", "mascots/fox-pixel-reactions.webp")
    )

    val DEFAULT = ALL.first { it.id == "fox" }

    fun findById(id: String): MascotDefinition =
        ALL.firstOrNull { it.id == id } ?: DEFAULT

    fun getByCategory(category: MascotCategory): List<MascotDefinition> =
        ALL.filter { it.category == category }
}
