package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.fate_grand_automata.scripts.bna.BnaBattleProcess
import io.github.fate_grand_automata.scripts.bna.BnaLocations
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class Metaspace @Inject constructor(
    exitManager: ExitManager,
    api: IFgoAutomataApi,
    private val battleProcess: BnaBattleProcess,
    private val bnaLocations: BnaLocations
) : EntryPoint(exitManager), IFgoAutomataApi by api {

    data class ExitState(
        val runsCompleted: Int,
        val wins: Int,
        val losses: Int
    )

    sealed class ExitReason(val cause: Exception? = null) {
        data object Completed : ExitReason()
        data object InvalidTeam : ExitReason()
        data object NoBattle : ExitReason()
        class Unexpected(cause: Exception) : ExitReason(cause)
    }

    class ExitException(val reason: ExitReason, val state: ExitState) : Exception()

    override fun script(): Nothing {
        var wins = 0
        var losses = 0

        try {
            val totalRuns = 10
            repeat(totalRuns) { runIndex ->
                exitManager.checkExitRequested()
                1.seconds.wait()
                if (runIndex > 0) {
                    // Navigate from metaspace screen → team screen
                    // TODO: This button gets matched while the victory screen is up if we accidentally fail the check there. Did similarity fix?
                    val button = bnaLocations.metaspaceBattleButton.exists(images[Images.MetaSpaceBattleButton], similarity = .92)
                    if (button) {
                        bnaLocations.metaspaceBattleButtonClick.click()
                        1.seconds.wait()
                    } else {
                        // Run ended early?
                        throw ExitException(
                            ExitReason.NoBattle,
                            ExitState(wins + losses, wins, losses)
                        )
                    }
                }

                // 1a TODO: team modification (unsupported — add team edit logic here?)

                // Start the battle
                val result = battleProcess.performBattle()

                when (result) {
                    true -> wins++
                    false -> losses++
                    null -> {
                        throw ExitException(
                            ExitReason.InvalidTeam,
                            ExitState(wins + losses, wins, losses)
                        )
                    }
                }
                1.seconds.wait()

                // On return: back on the metaspace screen ready for next iteration
            }

            throw ExitException(ExitReason.Completed, ExitState(totalRuns, wins, losses))
        } catch (e: ExitException) {
            throw e
        } catch (e: Exception) {
            throw ExitException(
                ExitReason.Unexpected(e),
                ExitState(wins + losses, wins, losses)
            )
        }
    }
}
