package game.ludora.core.common.release

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Validates that all production release deliverables, store metadata constraints,
 * legal compliance policies, and R8 configuration rules meet production readiness standards.
 */
class ReleaseReadinessTest {

    private val projectRoot: File by lazy {
        var current = File(System.getProperty("user.dir") ?: ".")
        while (current.parentFile != null && !File(current, "settings.gradle.kts").exists()) {
            current = current.parentFile
        }
        current
    }

    private val locales = listOf("en-US", "es-ES", "fr-FR", "de-DE", "hi-IN")

    @Test
    fun `store metadata conforms to google play character limits across all locales`() {
        for (locale in locales) {
            val localeDir = File(projectRoot, "fastlane/metadata/android/$locale")
            assertTrue("Locale directory must exist: $locale", localeDir.isDirectory)

            val titleFile = File(localeDir, "title.txt")
            assertTrue("title.txt must exist for $locale", titleFile.isFile)
            val title = titleFile.readText(Charsets.UTF_8).trim()
            assertTrue("Title for $locale must not be empty", title.isNotEmpty())
            assertTrue(
                "Title for $locale exceeds 30 characters (was ${title.length}): '$title'",
                title.length <= 30
            )

            val shortDescFile = File(localeDir, "short_description.txt")
            assertTrue("short_description.txt must exist for $locale", shortDescFile.isFile)
            val shortDesc = shortDescFile.readText(Charsets.UTF_8).trim()
            assertTrue("Short description for $locale must not be empty", shortDesc.isNotEmpty())
            assertTrue(
                "Short description for $locale exceeds 80 characters (was ${shortDesc.length}): '$shortDesc'",
                shortDesc.length <= 80
            )

            val fullDescFile = File(localeDir, "full_description.txt")
            assertTrue("full_description.txt must exist for $locale", fullDescFile.isFile)
            val fullDesc = fullDescFile.readText(Charsets.UTF_8).trim()
            assertTrue("Full description for $locale must not be empty", fullDesc.isNotEmpty())
            assertTrue(
                "Full description for $locale exceeds 4000 characters (was ${fullDesc.length})",
                fullDesc.length <= 4000
            )
        }
    }

    @Test
    fun `whatsnew release notes exist for production release`() {
        val whatsNewFile = File(projectRoot, "distribution/whatsnew/whatsnew-en-US")
        assertTrue("whatsnew-en-US file must exist", whatsNewFile.isFile)
        val content = whatsNewFile.readText(Charsets.UTF_8).trim()
        assertTrue("whatsnew release notes must not be empty", content.isNotEmpty())
        assertTrue("whatsnew must mention 1.0.0", content.contains("1.0.0"))
    }

    @Test
    fun `privacy policy covers offline guarantees and children privacy`() {
        val policyFile = File(projectRoot, "docs/22_PRIVACY_POLICY.md")
        assertTrue("Privacy policy must exist", policyFile.isFile)
        val text = policyFile.readText(Charsets.UTF_8)

        assertTrue("Must document offline data collection", text.contains("Offline Gameplay Data"))
        assertTrue("Must document online multiplayer data", text.contains("Online Multiplayer Data"))
        assertTrue("Must document advertising policies", text.contains("Advertising Data"))
        assertTrue("Must cover children's privacy and COPPA", text.contains("Children's Privacy"))
        assertTrue("Must detail data retention and local deletion", text.contains("Data Retention and Deletion"))
    }

    @Test
    fun `terms of service cover fair play virtual currency and in-app purchase licensing`() {
        val termsFile = File(projectRoot, "docs/23_TERMS_OF_SERVICE.md")
        assertTrue("Terms of service must exist", termsFile.isFile)
        val text = termsFile.readText(Charsets.UTF_8)

        assertTrue("Must cover acceptance of terms", text.contains("Acceptance of Terms"))
        assertTrue("Must state fair play rules against cheating", text.contains("Fair Play and Code of Conduct"))
        assertTrue("Must clarify virtual currency has no real cash value", text.contains("Virtual Currency and Cosmetic Items"))
        assertTrue("Must specify non-consumable Remove Ads license", text.contains("In-App Purchases"))
    }

    @Test
    fun `release deployment checklist contains all seven pre-flight verification gates`() {
        val checklistFile = File(projectRoot, "distribution/release_checklist.md")
        assertTrue("Release checklist must exist", checklistFile.isFile)
        val text = checklistFile.readText(Charsets.UTF_8)

        val requiredGates = listOf(
            "Gate 1: Application Version",
            "Gate 2: Signing & Keystore",
            "Gate 3: R8 Optimization",
            "Gate 4: Permissions & Manifest",
            "Gate 5: Offline-First",
            "Gate 6: Store Metadata",
            "Gate 7: Performance Budgets"
        )

        for (gate in requiredGates) {
            assertTrue("Checklist must contain $gate", text.contains(gate))
        }
    }

    @Test
    fun `app proguard rules cover models room serialization and symbolication`() {
        val proguardFile = File(projectRoot, "app/proguard-rules.pro")
        assertTrue("app/proguard-rules.pro must exist", proguardFile.isFile)
        val text = proguardFile.readText(Charsets.UTF_8)

        assertTrue("Must keep line numbers for crash symbolication", text.contains("LineNumberTable"))
        assertTrue("Must keep core domain models", text.contains("game.ludora.core.model.**"))
        assertTrue("Must keep engine core state", text.contains("game.ludora.engine.core.**"))
        assertTrue("Must configure kotlinx serialization rules", text.contains("kotlinx.serialization"))
        assertTrue("Must configure room database rules", text.contains("androidx.room"))
    }

    @Test
    fun `app build configuration specifies release version 1_0_0 and R8 optimizations`() {
        val buildFile = File(projectRoot, "app/build.gradle.kts")
        assertTrue("app/build.gradle.kts must exist", buildFile.isFile)
        val text = buildFile.readText(Charsets.UTF_8)

        assertTrue("versionCode must be 100", text.contains("versionCode = 100"))
        assertTrue("versionName must be 1.0.0", text.contains("versionName = \"1.0.0\""))
        assertTrue("isMinifyEnabled must be true", text.contains("isMinifyEnabled = true"))
        assertTrue("isShrinkResources must be true", text.contains("isShrinkResources = true"))
        assertTrue("signingConfigs block must be configured", text.contains("signingConfigs"))
    }

    @Test
    fun `android manifest declares vibrate permission for tactile feedback`() {
        val manifestFile = File(projectRoot, "app/src/main/AndroidManifest.xml")
        assertTrue("AndroidManifest.xml must exist", manifestFile.isFile)
        val text = manifestFile.readText(Charsets.UTF_8)

        assertTrue("Must declare VIBRATE permission", text.contains("android.permission.VIBRATE"))
        assertTrue("Must declare INTERNET permission", text.contains("android.permission.INTERNET"))
        assertTrue("Must declare ACCESS_NETWORK_STATE permission", text.contains("android.permission.ACCESS_NETWORK_STATE"))
    }
}
