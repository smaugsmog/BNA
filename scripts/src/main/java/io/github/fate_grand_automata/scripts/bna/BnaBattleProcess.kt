package io.github.fate_grand_automata.scripts.bna

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.lib_automata.ExitManager
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class BnaBattleProcess @Inject constructor(
    private val api: IFgoAutomataApi,
    private val exitManager: ExitManager,
    private val bnaLocations: BnaLocations
) : IFgoAutomataApi by api {

    /**
     * Performs a battle, starting on the Team selection screen.
     * Return True on Victory, False on loss, and null on incomplete/uncertain
     *
     * @param useTeamSelectScreen Whether to use the team select screen (3 teams) or the default team building screen
     * @param shouldSkip Whether to click the skip button during battle
     * @param pollInterval How long to wait between result checks.
     * @param maxAttempts Max number of result-check polls before timing out
     */
    fun performBattle(
        useTeamSelectScreen: Boolean = false,
        shouldSkip: Boolean = false,
        pollInterval: Duration = 0.5.seconds,
        maxAttempts: Int = 120,
    ): Boolean? {
        exitManager.checkExitRequested()
        if (useTeamSelectScreen) {
            val startResult = startBattleFromTeamListPopup()
            if (!startResult){
                return null
            }
        }
        else {
            // We are on the Team Building screen here.
            // Search for the filter button, then click up for the battle button
            0.2.seconds.wait()
            findAndClick(
                images[Images.TeamFilterButton],
                bnaLocations.teamFilterButtonRegion,
                exitManager,
                clickXOffset = -344,
                clickYOffset = -377,
            )
        }

        // Check if results already visible (auto-skip case)
        pollInterval.wait()

        // Else Handle skipping and/or waiting for results
        if (shouldSkip) {
            findAndClick(
                images[Images.BattleSkipButton],
                bnaLocations.battleSkipButtonRegion,
                exitManager,
            )
            pollInterval.wait()
        }

        // Step 3 (retry): poll until the result screen appears
        val result = waitForResult(pollInterval, maxAttempts)

        // We usually can find the victory a fair bit of time before we actually are able to click the continue, so just pre-emptively wait
        1.seconds.wait()

        // Step 4: click continue after reading result
        val clickResult = findAndClick(
            images[Images.BattleContinueIcon],
            bnaLocations.battleContinueIconRegion,
            exitManager,
            retryDelay = 100.milliseconds,
            maxRetries = 50
        )
        if (clickResult) {
            return result
        }
        return null
    }

    private fun startBattleFromTeamListPopup(): Boolean {
        return findAndClick(
            images[Images.MapBattleTeamConfirm],
            bnaLocations.mapBattleTeamConfirmRegion,
            exitManager,
        )
    }

    /**
     * Polls the victory/defeat regions until one matches or a timeout is reached.
     * Returns True on Victory, False on loss, and null on incomplete/uncertain
     */
    private fun waitForResult(pollInterval: Duration, maxAttempts: Int): Boolean? {
        repeat(maxAttempts) {
            exitManager.checkExitRequested()
            val result = checkForResult()
            if (result != null) return result
            pollInterval.wait()
        }
        val result = checkForResult()
        if (result != null) return result
        return null
    }

    /**
     * Returns True on Victory, False on loss, and null on incomplete/uncertain
     */
    private fun checkForResult(): Boolean? {
        // The victory screen has a title bar which is yellow on victory and grey on loss, seemingly on every screen.
        // The grey is the exact greyscale of the yellow, but it gets us out of having to match the animated victory/defeat splash.
        return useColor {
            useSameSnapIn {
                when {
                    bnaLocations.victoryDefeatRegion.exists(images[Images.BnaVictory], similarity = .97) -> true
                    bnaLocations.victoryDefeatRegion.exists(images[Images.BnaDefeat], similarity = .97) -> false
                    else -> null
                }
            }
        }
    }
}
