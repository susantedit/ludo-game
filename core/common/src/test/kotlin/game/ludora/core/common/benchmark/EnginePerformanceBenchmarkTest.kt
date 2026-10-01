package game.ludora.core.common.benchmark

import game.ludora.core.common.ads.AdPolicyManager
import game.ludora.core.common.ads.FakeAdProvider
import game.ludora.core.common.feedback.AudioSoundManager
import game.ludora.core.common.feedback.HapticFeedbackManager
import game.ludora.core.common.progression.DailyQuestEngine
import game.ludora.core.common.progression.ProgressionEngine
import game.ludora.core.model.DailyQuest
import game.ludora.core.model.LocalProfile
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * Performance benchmark verifying engine execution speed, throughput, and memory allocations.
 */
class EnginePerformanceBenchmarkTest {

    @Test
    fun `turn outcome and progression calculations execute under 1ms budget`() {
        val profile = LocalProfile(profileId = "bench_player", coins = 500L, level = 5)

        // Warm up JVM
        for (i in 1..100) {
            ProgressionEngine.applyMatchOutcome(profile, placement = 1, tokensCaptured = 3, isWin = true)
        }

        // Measure 500 evaluations
        val nanoDuration = measureNanoTime {
            for (i in 1..500) {
                ProgressionEngine.applyMatchOutcome(profile, placement = 1, tokensCaptured = 2, isWin = true)
            }
        }

        val averageMsPerEvaluation = (nanoDuration / 500.0) / 1_000_000.0
        assertTrue(
            "Average progression calculation must be < 1.0ms, got ${String.format("%.4f", averageMsPerEvaluation)}ms",
            averageMsPerEvaluation < 1.0
        )
    }

    @Test
    fun `throughput benchmark of 1,000 quest tracking iterations executes under 100ms`() {
        var quests = DailyQuest.defaultQuests()

        val millis = measureTimeMillis {
            for (i in 1..1000) {
                quests = DailyQuestEngine.recordMatchEvents(
                    quests = quests,
                    isWin = (i % 2 == 0),
                    tokensCaptured = (i % 3),
                    sixesRolled = (i % 4)
                )
            }
        }

        assertTrue(
            "1,000 quest iterations should complete in < 100ms, took ${millis}ms",
            millis < 100L
        )
    }

    @Test
    fun `active heap footprint during core processing remains under 120MB threshold`() {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        val initialUsedMemory = runtime.totalMemory() - runtime.freeMemory()

        // Allocate and evaluate multiple managers and state objects
        val profiles = mutableListOf<LocalProfile>()
        val adManagers = mutableListOf<AdPolicyManager>()

        for (i in 1..5000) {
            val p = LocalProfile(profileId = "bench_$i", coins = i.toLong(), level = (i % 20) + 1)
            profiles.add(p)
            val ad = AdPolicyManager(initialSessionCount = i % 5)
            ad.recordMatchFinished()
            adManagers.add(ad)
        }

        val postUsedMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsedMb = (postUsedMemory - initialUsedMemory).coerceAtLeast(0L) / (1024 * 1024)

        assertTrue(
            "Memory footprint for 5,000 active records must be < 120MB, measured ${memoryUsedMb}MB",
            memoryUsedMb < 120L
        )
    }

    @Test
    fun `cold start initialization of core subsystems takes under 50ms`() {
        val initMillis = measureTimeMillis {
            val adPolicy = AdPolicyManager(initialSessionCount = 1)
            val adProvider = FakeAdProvider().apply { initialize() }
            val audio = AudioSoundManager()
            val haptic = HapticFeedbackManager()
            val defaultQuests = DailyQuest.defaultQuests()
        }

        assertTrue(
            "Cold start subsystem initialization must be < 50ms, took ${initMillis}ms",
            initMillis < 50L
        )
    }
}
