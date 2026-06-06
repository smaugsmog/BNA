package io.github.fate_grand_automata.scripts.bna

import io.github.fate_grand_automata.scripts.locations.IScriptAreaTransforms
import io.github.lib_automata.Location
import io.github.lib_automata.Region
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

/**
 * BNA location constants in script space (fixed height 1440px, width varies by device. see CONTRIBUTING.md for a how-to).
 * Example 2400x1080 (with 50px left and right black bars) -> 1533x720 (images) -> 3066x1440 (regions)
 */
@ScriptScope
class BnaLocations @Inject constructor(
    scriptAreaTransforms: IScriptAreaTransforms
) : IScriptAreaTransforms by scriptAreaTransforms {

    // ===== Map Screen =====

    /** Region where the map-active badge appears (sticky to top-left). */
    val mapBadgeSearchRegion = Region(780, 0, 155, 125)

    /** Derives the map-nav-button click from a matched badge. */
    val mapNavClickFromBadge = Location(mapBadgeSearchRegion.x - 107, 34)

    /** Region for scanning map mission icons. */
    val mapSearchArea = Region(125, 139, 1928, 1169)

    /** Back button at the top-left of the map screen. */
    val mapBackButton = Location(80, 33)

    /** Opens the map's team selector (bottom-right locked). */
    val mapClaimBattleButton: Location
        get() = Location(-514, -147)
            .xFromRight()
            .yFromBottom()

    /** Region to image-match the MapBattleTeamConfirm button (centered). */
    val mapBattleTeamConfirmRegion: Region
        get() = Region(473, -949, 101, 256)
            .xFromCenter()
            .yFromBottom()

    /** Supplies display on the map screen (bottom-left locked). */
    val mapSupplies: Location
        get() = Location(892, -65)
            .yFromBottom()

    /** Confirm button on the map popup dialogs (centered).
     * Note that if the box expands, it expands vertically and the button will be lower on the screen.
     * Prefer matching with findAndClick
     */
    val popupConfirmButton: Location
        get() = Location(0, 340)
            .xFromCenter()
            .yFromCenter()

    /** Confirm button on the supply collection dialog (centered). */
    val popupConfirmButtonRegion = Region(-230, 0, 55, 750)
        .xFromCenter()
        .yFromCenter()

    /** Region to search for the area-locked icon, centered within mapSearchArea. */
    val areaLockedIconRegion = Region(mapSearchArea.center.x - 70, mapSearchArea.center.y - 70, 140, 140)

    // ===== Metaspace Screen =====

    /** Bottom-right button on the metaspace screen that opens the team/battle prep screen. */
    val metaspaceBattleButton = Region(-804, -206, 62, 100)
        .xFromRight()
        .yFromBottom()

    val metaspaceBattleButtonClick = Location(metaspaceBattleButton.x - 120, metaspaceBattleButton.y + 40)

    // ===== Void Mirror Screen =====

    /** Region where void buff selection icons appear. */
    val voidBuffSelectionRegion = Region(-622, -374, 142, 630)
        .xFromCenter()
        .yFromCenter()

    /** Confirm button for void buff selection. */
    val voidFirstBuff = Location(0, -260)
        .xFromCenter()
        .yFromCenter()

    /** Confirm button for void buff selection. */
    // Pixel 1210x860
    val voidBuffConfirm: Location
        get() = Location(0, 420)
            .xFromCenter()
            .yFromCenter()

    /** Confirm button for void buff selection. */
    val voidBuffConfirmRegion = Region(-156, 360, 50, 140)
        .xFromCenter()
        .yFromCenter()

    /** Battle start button that opens buff screen or restarts the battle. */
    val voidStartBattle: Location
        get() = Location(-380, -260)
            .xFromCenter()
            .yFromBottom()

    /** Exit button at the top-right of the void mirror screen. */
    val voidExitButton: Location
        get() = Location(-382, 163)
            .xFromRight()

    // ===== Team / Battle Prep Screen =====

    /** Bottom-right battle start button on the team screen. */
    val teamScreenBattleButton: Location
        get() = Location(-574, -427)
            .xFromRight()
            .yFromBottom()

    /** Area of the in-battle buttons which includes the skip button */
    val battleSkipButtonRegion = Region(-350, -100, 350, 100)
        .xFromRight()
        .yFromBottom()

    // ===== Battle Results Screen =====

    /** Bottom-right continue button on the victory/defeat results screen. */
    val continueClick: Location
        get() = Location(-474, -227)
            .xFromRight()
            .yFromBottom()

    /** Bottom-right continue button on the victory/defeat results screen. */
    val battleContinueIconRegion = Region(-386, -268, 82, 96)
        .xFromRight()
        .yFromBottom()

    /** Region where the small colored title bar that show VICTORY or DEFEAT is. Yellow on victory and grey on defeat, so color comparisons are required. */
    val victoryDefeatRegion: Region
        get() = Region(-88, 216, 88, 88)
            .xFromRight()
}
