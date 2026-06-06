package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.fate_grand_automata.scripts.bna.BnaBattleProcess
import io.github.fate_grand_automata.scripts.bna.BnaLocations
import io.github.fate_grand_automata.scripts.bna.findAndClick
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.Location
import io.github.lib_automata.Region
import io.github.lib_automata.Swiper
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class Map @Inject constructor(
    exitManager: ExitManager,
    api: IFgoAutomataApi,
    private val battleProcess: BnaBattleProcess,
    private val bnaLocations: BnaLocations,
    private val swipe: Swiper
) : EntryPoint(exitManager), IFgoAutomataApi by api {

    data class ExitState(
        val boxesCollected: Int,
        val battlesFought: Int,
        val offerAvailable: Boolean
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
        var offerAvailable = false

        try {
            // Step 1: Collect supplies
            bnaLocations.mapSupplies.click()
            findAndClick(
                image = images[Images.PopupConfirmButton],
                searchRegion = bnaLocations.popupConfirmButtonRegion,
                exitManager,
                clickXOffset = 200,
            )
            0.5.seconds.wait()

            // Step 2: Check for active badge
            val badgeResult = useSameSnapIn {
                val badgeFound = findAndClick(
                    image = images[Images.MapActiveBadge],
                    searchRegion = bnaLocations.mapBadgeSearchRegion,
                    exitManager,
                    clickXOffset = -177,
//                    similarity = 92.0,
                    confirmsRequired = 0,
                )

                if (!badgeFound) {
                    val locked = images[Images.MapAreaLocked] in bnaLocations.areaLockedIconRegion

                    if (locked) {
                        throw ExitException(ExitReason.Completed, ExitState(boxes, battles, offerAvailable))
                    }
                }
            }
            1.seconds.wait()

            // Step 3: Main mission scanning loop
            val mapArea = bnaLocations.mapSearchArea
            val safeArea = Region(
                mapArea.x + mapArea.width / 6,
                mapArea.y + mapArea.height / 6,
                mapArea.width * 2 / 3,
                mapArea.height * 2 / 3
            )

            // Scroll around the map to look for offscreen missions.
            // Usually overkill, but handles different screens and helps with battle missions potentially jumping the screen around unpredictably
            val scrolls = listOf(
                null to null, // Start search in default center
                Location(safeArea.right, safeArea.center.y) to Location(safeArea.left, safeArea.center.y),
                Location(safeArea.center.x, safeArea.bottom) to Location(safeArea.center.x, safeArea.top),
                Location(safeArea.center.x, safeArea.bottom) to Location(safeArea.center.x, safeArea.top),
                Location(safeArea.center.x, safeArea.bottom) to Location(safeArea.center.x, safeArea.top),
                Location(safeArea.left, safeArea.center.y) to Location(safeArea.right, safeArea.center.y),
                Location(safeArea.center.x, safeArea.top) to Location(safeArea.center.x, safeArea.bottom),
                Location(safeArea.center.x, safeArea.top) to Location(safeArea.center.x, safeArea.bottom),
                Location(safeArea.center.x, safeArea.top) to Location(safeArea.center.x, safeArea.bottom),
                Location(safeArea.right, safeArea.center.y) to Location(safeArea.left, safeArea.center.y),
            )

            for ((scrollStart, scrollEnd) in scrolls) {
                scrollStart?.let { scrollEnd?.let {
                    swipe(scrollStart, scrollEnd)
                    0.1.seconds.wait()
                } }

                // Look for the mission icons
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

                        if (!offerAvailable){
                            // This won't buy anything, so we are just logging if the user needs to check.
                            // So we only need it once, and we can check last.
                            val offer = area.find(images[Images.MapMissionOffer])
                            if (offer != null) {
                                offerAvailable = true
                            }
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
                            val clicked = findAndClick(
                                image = images[Images.PopupConfirmButton],
                                searchRegion = bnaLocations.popupConfirmButtonRegion,
                                exitManager,
                                clickXOffset = 200,
                            )
                            if (!clicked){
                                bnaLocations.popupConfirmButton.click()
                            }
                        }

                        is MissionFound.Gun -> {
                            battles++
                            detectedMission.location.click()
                            0.5.seconds.wait()
                            bnaLocations.mapClaimBattleButton.click()
                            0.5.seconds.wait()

                            val battleResult = battleProcess.performBattle(
                                useTeamSelectScreen = true,
                                pollInterval = 2.seconds,
                                maxAttempts = 200
                            )
                            if (battleResult == false) {
                                throw ExitException(ExitReason.Failure, ExitState(boxes, battles, offerAvailable))
                            }
                        }

                        null -> break
                    }

                    3.seconds.wait()
                }
            }

            throw ExitException(ExitReason.Completed, ExitState(boxes, battles, offerAvailable))
        } catch (e: ExitException) {
            throw e
        } catch (e: Exception) {
            throw ExitException(ExitReason.Unexpected(e), ExitState(boxes, battles, offerAvailable))
        }
    }
}
