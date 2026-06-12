package io.github.fate_grand_automata.scripts.bna

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.fate_grand_automata.scripts.Images
import io.github.lib_automata.ExitManager
import io.github.lib_automata.Location
import io.github.lib_automata.Pattern
import io.github.lib_automata.Swiper
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@ScriptScope
class TeamBuildingUtil @Inject constructor(
    private val api: IFgoAutomataApi,
    private val exitManager: ExitManager,
    private val bnaLocations: BnaLocations,
    private val swipe: Swiper
) : IFgoAutomataApi by api {

    enum class Faction(val image: Images) {
        Tamer(Images.Tamer),
        Nature(Images.FactionNature),
        Machine(Images.FactionMachine),
        Martial(Images.FactionMartial),
        Hacker(Images.FactionHacker),
        Fallen(Images.FactionFallen)
    }

    private var teamSlots: List<Location>? = null

    fun calibrateTeamSlots(): List<Location> {
        val matches = useSameSnapIn {
            bnaLocations.teamSlotsSearchRegion.findAll(images[Images.EmptyTeamSlot]).toList()
        }
        teamSlots = matches.map { it.region.center }
        return teamSlots!!
    }

    fun addHeroBySlotPosition(slotIndex: Int) {
        // TODO: tap the hero portrait at slotIndex in the bottom selection bar
    }

    fun addHeroByImage(heroImage: Pattern) {
        // TODO: match heroImage in heroSelectionBarRegion and tap it
    }

    fun removeHeroFromSlot(slotIndex: Int) {
        val slots = teamSlots ?: calibrateTeamSlots()
        slots[slotIndex].click()
        200.milliseconds.wait()
    }

    fun clearTeam() {
        val slots = teamSlots ?: calibrateTeamSlots()
        slots.forEach { slot ->
            slot.click()
            200.milliseconds.wait()
        }
    }

    fun swapHeroSlots(from: Int, to: Int) {
        val slots = teamSlots ?: calibrateTeamSlots()
        swipe(slots[from], slots[to])
    }

    fun getFactionRequirement(): Faction? {
        return useSameSnapIn {
            Faction.entries.firstOrNull { faction ->
                bnaLocations.metaspaceFactionRequirementRegion.exists(images[faction.image])
            }
        }
    }

    fun applyFactionFilter(faction: Faction) {
        findAndClick(
            image = images[Images.TeamFilterButton],
            searchRegion = bnaLocations.teamFilterButtonRegion,
            exitManager,
            confirmsRequired = 0,
        )
        0.5.seconds.wait()

        bnaLocations.filterWindowFactionRegion.find(images[faction.image])?.region?.center?.click()
        0.3.seconds.wait()

        findAndClick(
            image = images[Images.PopupConfirmButton],
            searchRegion = bnaLocations.popupConfirmButtonRegion,
            exitManager,
        )
    }

    fun buildMetaspaceTeam() {
        addHeroBySlotPosition(1)
        val faction = getFactionRequirement() ?: return
        applyFactionFilter(faction)
        for (slot in 2..6) {
            addHeroBySlotPosition(slot)
        }
    }
}
