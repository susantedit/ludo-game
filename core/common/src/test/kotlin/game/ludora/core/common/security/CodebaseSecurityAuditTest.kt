package game.ludora.core.common.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Automated security regression test.
 * Scans the project repository to ensure compliance with CWE-89, CWE-926, CWE-330, and CWE-400.
 */
class CodebaseSecurityAuditTest {

    private val projectRoot: File by lazy {
        var current = File(System.getProperty("user.dir") ?: ".")
        while (current.parentFile != null && !File(current, "settings.gradle.kts").exists()) {
            current = current.parentFile
        }
        current
    }

    @Test
    fun `manifest declares only MainActivity as exported`() {
        val manifestFile = File(projectRoot, "app/src/main/AndroidManifest.xml")
        assertTrue("AndroidManifest.xml must exist", manifestFile.isFile)
        val content = manifestFile.readText(Charsets.UTF_8)

        val exportedMatches = Regex("""android:exported\s*=\s*"true"""").findAll(content).toList()
        assertTrue("At most 1 exported activity allowed (MainActivity)", exportedMatches.size <= 1)
        assertTrue("Must contain MAIN action", content.contains("android.intent.action.MAIN"))
        assertTrue("Must contain LAUNCHER category", content.contains("android.intent.category.LAUNCHER"))
    }

    @Test
    fun `no insecure java util Random in core dice and networking`() {
        val diceFile = File(projectRoot, "engine/core/src/main/kotlin/game/ludora/engine/core/DiceRoller.kt")
        val roomCodeFile = File(projectRoot, "core/network/src/main/kotlin/game/ludora/core/network/util/RoomCodeGenerator.kt")

        assertTrue("DiceRoller must exist", diceFile.isFile)
        assertTrue("RoomCodeGenerator must exist", roomCodeFile.isFile)

        val diceText = diceFile.readText(Charsets.UTF_8)
        val roomText = roomCodeFile.readText(Charsets.UTF_8)

        assertTrue("DiceRoller must use SecureRandom", diceText.contains("java.security.SecureRandom"))
        assertFalse("DiceRoller must not use java.util.Random", diceText.contains("java.util.Random"))

        assertTrue("RoomCodeGenerator must use SecureRandom", roomText.contains("java.security.SecureRandom"))
        assertFalse("RoomCodeGenerator must not use java.util.Random", roomText.contains("java.util.Random"))
    }

    @Test
    fun `database DAOs do not use string concatenation in queries`() {
        val daoDir = File(projectRoot, "core/database/src/main/kotlin/game/ludora/core/database/dao")
        assertTrue("DAO directory must exist", daoDir.isDirectory)

        val daoFiles = daoDir.listFiles { _, name -> name.endsWith(".kt") } ?: emptyArray()
        assertTrue("Must contain DAO files", daoFiles.isNotEmpty())

        for (file in daoFiles) {
            val text = file.readText(Charsets.UTF_8)
            assertFalse(
                "DAO ${file.name} must not use string concatenation in queries",
                text.contains("""@Query(".*"\s*\+""".toRegex())
            )
            assertFalse(
                "DAO ${file.name} must not use SimpleSQLiteQuery",
                text.contains("SimpleSQLiteQuery")
            )
        }
    }

    @Test
    fun `security audit report exists and covers all target CWEs`() {
        val auditReport = File(projectRoot, "docs/24_SECURITY_AUDIT_REPORT.md")
        assertTrue("Security audit report must exist", auditReport.isFile)
        val content = auditReport.readText(Charsets.UTF_8)

        val expectedCwes = listOf("CWE-89", "CWE-926", "CWE-330", "CWE-312", "CWE-400")
        for (cwe in expectedCwes) {
            assertTrue("Security report must document $cwe", content.contains(cwe))
        }
    }
}
