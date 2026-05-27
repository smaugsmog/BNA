package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.fate_grand_automata.scripts.bna.BnaBattleProcess
import io.github.fate_grand_automata.scripts.bna.BnaLocations
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.Location
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class VoidMirror @Inject constructor(
    exitManager: ExitManager,
    api: IFgoAutomataApi,
    private val battleProcess: BnaBattleProcess,
    private val bnaLocations: BnaLocations,
) : EntryPoint(exitManager), IFgoAutomataApi by api {

    data class ExitState(
        val successfulRuns: Int,
        val totalAttempts: Int,
        val failedRetries: Int
    )

    sealed class ExitReason(val cause: Exception? = null) {
        data object Completed : ExitReason()
        data object MaxRetriesReached : ExitReason()
        class Unexpected(cause: Exception) : ExitReason(cause)
    }

    class ExitException(val reason: ExitReason, val state: ExitState) : Exception()

    companion object {
        private const val MAX_FAILED_RETRIES = 20
        private const val MAX_WINS = 100
    }

    override fun script(): Nothing {
        var totalAttempts = 0
        var successfulRuns = 0
        var failedRetries = 0
        var needsBuffSelection = true

        try {
            while (true) {
                exitManager.checkExitRequested()
                0.5.seconds.wait()
                exitManager.checkExitRequested()
                bnaLocations.voidStartBattle.click()
                0.8.seconds.wait()

                if (needsBuffSelection) {
                    val selectedBuff = selectBestAvailableBuff()
                    if (!selectedBuff){
                        throw ExitException(
                            ExitReason.Unexpected(Exception("Battle result was null")),
                            ExitState(successfulRuns, totalAttempts, failedRetries)
                        )
                    }
                    1.seconds.wait()
                }

                val result = battleProcess.performBattle()
                totalAttempts++

                when (result) {
                    true -> {
                        successfulRuns++
                        failedRetries = 0
                        needsBuffSelection = true

                        if (successfulRuns >= MAX_WINS) {
                            throw ExitException(
                                ExitReason.Completed,
                                ExitState(successfulRuns, totalAttempts, failedRetries)
                            )
                        }
                    }

                    false -> {
                        failedRetries++
                        needsBuffSelection = false

                        if (failedRetries >= MAX_FAILED_RETRIES) {
                            throw ExitException(
                                ExitReason.MaxRetriesReached,
                                ExitState(successfulRuns, totalAttempts, failedRetries)
                            )
                        }
                    }

                    null -> {
                        throw ExitException(
                            ExitReason.Unexpected(Exception("Battle result was null")),
                            ExitState(successfulRuns, totalAttempts, failedRetries)
                        )
                    }
                }
            }
        } catch (e: ExitException) {
            throw e
        } catch (e: Exception) {
            throw ExitException(
                ExitReason.Unexpected(e),
                ExitState(successfulRuns, totalAttempts, failedRetries)
            )
        }
    }

    private fun selectBestAvailableBuff(): Boolean {
        return useSameSnapIn {
            val button = bnaLocations.voidBuffConfirmRegion.find(images[Images.PopupConfirmButton]) ?: return@useSameSnapIn false
            var foundBuff = false
            for (buff in prefs.voidMirrorBuffPriority) {
                val buffMatch = bnaLocations.voidBuffSelectionRegion.find(images[buff.image])
                if (buffMatch != null) {
                    foundBuff = true
                    buffMatch.region.click()
                    0.3.seconds.wait()
                    break
                }
            }
            if (!foundBuff) {
                // Just select the first buff as a backup if we failed to match one
                bnaLocations.voidFirstBuff.click()
            }
            exitManager.checkExitRequested()
//            bnaLocations.voidBuffConfirm.click()
            Location(button.region.center.x + 200, button.region.center.y).click()
            return@useSameSnapIn true
        }
    }
}
