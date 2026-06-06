package io.github.fate_grand_automata.scripts.bna

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.lib_automata.ExitManager
import io.github.lib_automata.Location
import io.github.lib_automata.Pattern
import io.github.lib_automata.Region
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun IFgoAutomataApi.findAndClick(
    image: Pattern,
    searchRegion: Region,
    exitManager: ExitManager? = null,
    clickXOffset: Int = 0,
    clickYOffset: Int = 0,
    similarity: Double? = null,
    retryDelay: Duration = 100.milliseconds,
    maxRetries: Int = 30,
    clickDelay: Duration = 250.milliseconds,
    confirmsRequired: Int = 1,
): Boolean {
    var found = false
    var confirms = 0

    for (i in 1..maxRetries) {
        exitManager?.checkExitRequested()
        val match = searchRegion.find(image, similarity)
        if (match != null) {
            Location(
                match.region.center.x + clickXOffset,
                match.region.center.y + clickYOffset
            ).click()

            if (confirmsRequired == 0) {
                return true
            }
            found = true
            clickDelay.wait()
        } else if (found) {
            confirms += 1
            if (confirms >= confirmsRequired){
                return true
            }
        }
        if (i != maxRetries) {
            retryDelay.wait()
        }
    }
    return false
}
