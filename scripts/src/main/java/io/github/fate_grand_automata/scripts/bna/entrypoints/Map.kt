package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.fate_grand_automata.scripts.bna.BnaBattleProcess
import io.github.fate_grand_automata.scripts.bna.BnaLocations
import io.github.fate_grand_automata.scripts.bna.findAndClick
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.Location
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class Map @Inject constructor(
    exitManager: ExitManager,
    api: IFgoAutomataApi,
    private val battleProcess: BnaBattleProcess,
    private val bnaLocations: BnaLocations
) : EntryPoint(exitManager), IFgoAutomataApi by api {

    data class ExitState(
        val boxesCollected: Int,
        val battlesFought: Int,
    )

    sealed class ExitReason(val cause: Exception? = null) {
        data object Completed : ExitReason()
        data object Failure : ExitReason()
        class Unexpected(cause: Exception) : ExitReason(cause)
    }

    class ExitException(val reason: ExitReason, val state: ExitState) : Exception()

    private sealed class MissionFound {
        data class Gift(val location: Location) : MissionFound()
        data class Gun(val location: Location) : MissionFound()
    }

    override fun script(): Nothing {
        var boxes = 0
        var battles = 0

        try {
            // Step 1: Collect supplies
            bnaLocations.mapSupplies.click()
            findAndClick(
                image = images[Images.PopupConfirmButton],
                searchRegion = bnaLocations.popupConfirmButtonRegion,
                clickXOffset = 200,
            )
            1.seconds.wait()

            // Step 2: Check for active badge
            val badgeResult = useSameSnapIn {
                val badgeFound = images[Images.MapActiveBadge] in bnaLocations.mapBadgeSearchRegion

                if (badgeFound) {
                    bnaLocations.mapNavClickFromBadge.click()
                } else {
                    val locked = images[Images.MapAreaLocked] in bnaLocations.areaLockedIconRegion

                    if (locked) {
                        throw ExitException(ExitReason.Completed, ExitState(boxes, battles))
                    }
                }
            }
            3.seconds.wait()

            // Step 3: Main mission scanning loop
            while (true) {
                exitManager.checkExitRequested()

                val detectedMission = useSameSnapIn {
                    val area = bnaLocations.mapSearchArea

                    val gift = area.find(images[Images.MapMissionGift])
                    if (gift != null) {
                        return@useSameSnapIn MissionFound.Gift(gift.region.center)
                    }

                    val gun = area.find(
                        listOf(
                            images[Images.MapMissionGunBlue],
                            images[Images.MapMissionGunPurple],
                            images[Images.MapMissionGunYellow]
                        )
                    )
                    if (gun != null) {
                        return@useSameSnapIn MissionFound.Gun(gun.region.center)
                    }

                    null
                }

                exitManager.checkExitRequested()

                when (detectedMission) {
                    is MissionFound.Gift -> {
                        boxes++
                        detectedMission.location.click()
                        1.seconds.wait()
                        bnaLocations.mapClaimBattleButton.click()
                        1.seconds.wait()
                        bnaLocations.popupConfirmButton.click()
                    }
                    is MissionFound.Gun -> {
                        battles++
                        detectedMission.location.click()
                        1.seconds.wait()
                        bnaLocations.mapClaimBattleButton.click()
                        2.seconds.wait()

                        val battleResult = battleProcess.performBattle(
                            useTeamSelectScreen = true,
                            pollInterval = 2.seconds,
                            maxAttempts = 200
                        )
                        if (battleResult == false){
                            throw ExitException(ExitReason.Failure, ExitState(boxes, battles))
                        }
                    }
                    null -> throw ExitException(ExitReason.Completed, ExitState(boxes, battles))

                    // TODO: Needs to scroll
                }

                3.seconds.wait()
            }
        } catch (e: ExitException) {
            throw e
        } catch (e: Exception) {
            throw ExitException(ExitReason.Unexpected(e), ExitState(boxes, battles))
        }
    }
}
