package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.bna.BnaBattleProcess
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class Dailies @Inject constructor(
    exitManager: ExitManager,
    api: IFgoAutomataApi,
    private val battleProcess: BnaBattleProcess
) : EntryPoint(exitManager), IFgoAutomataApi by api {

    data class ExitState(val stepsCompleted: Int)

    sealed class ExitReason(val cause: Exception? = null) {
        data object Completed : ExitReason()
        class Unexpected(cause: Exception) : ExitReason(cause)
    }

    class ExitException(val reason: ExitReason, val state: ExitState) : Exception()

    override fun script(): Nothing {
        var steps = 0
        try {
            // Step 1: Navigation macros to first screen
            // TODO: add navigation taps

            // Step 2: Daily collection mini-scripts
            // TODO: add collection logic

            // Step 3: Battle delegation
            // battleProcess.performBattle()

            // Step 4: Cleanup navigation
            // TODO: add cleanup

            throw ExitException(ExitReason.Completed, ExitState(steps))
        } catch (e: ExitException) {
            throw e
        } catch (e: Exception) {
            throw ExitException(ExitReason.Unexpected(e), ExitState(steps))
        }
    }
}
