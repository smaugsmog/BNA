package io.github.fate_grand_automata.scripts.bna

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.fate_grand_automata.scripts.enums.DailiesStep
import io.github.lib_automata.ExitManager
import io.github.lib_automata.Location
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class DailiesLogic @Inject constructor(
    private val exitManager: ExitManager,
    private val api: IFgoAutomataApi,
    private val battleProcess: BnaBattleProcess,
    private val bnaLocations: BnaLocations,
    private val mapLogic: MapLogic,
) : IFgoAutomataApi by api {

    data class ExitState(val stepsCompleted: Int)

    sealed class ExitReason(val cause: Exception? = null) {
        data object Completed : ExitReason()
        class Unexpected(cause: Exception) : ExitReason(cause)
    }

    class ExitException(val reason: ExitReason, val state: ExitState) : Exception()

    private val enabledSteps: List<DailiesStep>
        get() = prefs.dailiesEnabledSteps

    private sealed class FlowerAction {
        data class Claim(val location: Location) : FlowerAction()
        data class Send(val location: Location) : FlowerAction()
    }

    fun dailiesLogic(): Nothing {
        var steps = 0

        try {
            if (DailiesStep.GoldCollection in enabledSteps) {
                collectGold()
                steps++
            }

            if (DailiesStep.SendFlowers in enabledSteps) {
                sendFlowers()
                steps++
            }

            if (DailiesStep.Hypermarket in enabledSteps) {
                collectHypermarket()
                steps++
            }

            if (DailiesStep.Map in enabledSteps) {
                try {
                    runMap()
                } catch (e: MapLogic.ExitException) {
                    when (e.reason) {
                        is MapLogic.ExitReason.Completed -> {
                            bnaLocations.mapBackButton.click()
                            1.seconds.wait()
                        }
                        else -> throw ExitException(ExitReason.Unexpected(e), ExitState(steps))
                    }
                }
                steps++
            }

            // TODO: Add support for:
            //   - Gear selling
            //   - Arena battles
            //   - Auto-navigating to metaspace / void mirror

            throw ExitException(ExitReason.Completed, ExitState(steps))
        } catch (e: ExitException) {
            throw e
        } catch (e: Exception) {
            throw ExitException(ExitReason.Unexpected(e), ExitState(steps))
        }
    }

    // ================================================================
    // Step 1 – Gold collection
    // ================================================================

    private fun collectGold() {
        val goldClicked = findAndClick(
            image = images[Images.DailiesGoldIcon],
            searchRegion = bnaLocations.dailiesGoldIconRegion,
            exitManager,
            maxRetries = 10,
            confirmsRequired = 0,
        )

        if (goldClicked) {
            0.5.seconds.wait()

            findAndClick(
                image = images[Images.DailiesGoldCollect],
                searchRegion = bnaLocations.dailiesGoldCollectRegion,
                exitManager,
            )

            findAndClick(
                image = images[Images.PopupConfirmButton],
                searchRegion = bnaLocations.popupConfirmButtonRegion,
                exitManager,
                clickXOffset = 200,
            )
        }

        bnaLocations.dailiesClosePopupLocation.click()
        0.5.seconds.wait()
    }

    // ================================================================
    // Step 2 – Send / receive flowers
    // ================================================================

    private fun sendFlowers() {
        bnaLocations.dailiesFlowersButton.click()
        1.seconds.wait()

        while (true) {
            exitManager.checkExitRequested()

            val action = useSameSnapIn {
                val claim = bnaLocations.dailiesFlowersButtonRegion.find(images[Images.DailiesFlowerClaimAll])
                if (claim != null) {
                    return@useSameSnapIn FlowerAction.Claim(claim.region.center)
                }

                val send = bnaLocations.dailiesFlowersButtonRegion.find(images[Images.DailiesFlowerSendAll])
                if (send != null) {
                    return@useSameSnapIn FlowerAction.Send(send.region.center)
                }

                null
            }

            when (action) {
                is FlowerAction.Claim -> {
                    action.location.click()
                    findAndClick(
                        image = images[Images.PopupConfirmButton],
                        searchRegion = bnaLocations.popupConfirmButtonRegion,
                        exitManager,
                        clickXOffset = 200,
                    )
                    0.5.seconds.wait()
                }

                is FlowerAction.Send -> {
                    action.location.click()
                    findAndClick(
                        image = images[Images.PopupConfirmButton],
                        searchRegion = bnaLocations.popupConfirmButtonRegion,
                        exitManager,
                        clickXOffset = 200,
                    )
                    findAndClick(
                        image = images[Images.PopupConfirmButton],
                        searchRegion = bnaLocations.popupConfirmButtonRegion,
                        exitManager,
                        clickXOffset = 200,
                    )
                    0.5.seconds.wait()
                }

                null -> break
            }
        }

        bnaLocations.dailiesClosePopupLocation.click()
        0.5.seconds.wait()
    }

    // ================================================================
    // Step 3 – Hypermarket (off by default – high misclick risk)
    // ================================================================

    private fun collectHypermarket() {
        findAndClick(
            image = images[Images.DailiesHypermarketIcon],
            searchRegion = bnaLocations.dailiesHypermarketSearchRegion,
            exitManager,
            maxRetries = 10,
            retryDelay = 200.milliseconds,
            confirmsRequired = 0,
        )
        0.5.seconds.wait()

        findAndClick(
            image = images[Images.DailiesHypermarketDailyDeal],
            searchRegion = bnaLocations.dailiesHypermarketSearchRegion,
            exitManager,
            maxRetries = 10,
        )
        0.5.seconds.wait()

        findAndClick(
            image = images[Images.DailiesHypermarketFree],
            searchRegion = bnaLocations.dailiesHypermarketSearchRegion,
            exitManager,
            maxRetries = 10,
        )
        0.5.seconds.wait()

        findAndClick(
            image = images[Images.DailiesHypermarketExit],
            searchRegion = bnaLocations.dailiesHypermarketSearchRegion,
            exitManager,
            maxRetries = 10,
            confirmsRequired = 0,
        )
        0.5.seconds.wait()
    }

    // ================================================================
    // Step 4 – Map
    // ================================================================

    private fun runMap() {
        findAndClick(
            image = images[Images.DailiesMapEntry],
            searchRegion = bnaLocations.dailiesMapEntryRegion,
            exitManager,
            maxRetries = 20,
            confirmsRequired = 0,
        )
        2.seconds.wait()

        mapLogic.mapLogic()
    }
}
