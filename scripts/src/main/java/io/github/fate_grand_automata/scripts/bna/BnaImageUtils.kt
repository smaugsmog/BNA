package io.github.fate_grand_automata.scripts.bna

import io.github.fate_grand_automata.scripts.IFgoAutomataApi
import io.github.lib_automata.Location
import io.github.lib_automata.Pattern
import io.github.lib_automata.Region
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun IFgoAutomataApi.findAndClick(
    image: Pattern,
    searchRegion: Region,
    clickXOffset: Int = 0,
    clickYOffset: Int = 0,
    retryDelay: Duration = 100.milliseconds,
    maxRetries: Int = 30
): Boolean {
    var found = false

    for (i in 1..maxRetries) {
        val match = searchRegion.find(image)
        if (match != null) {
            Location(
                match.region.center.x + clickXOffset,
                match.region.center.y + clickYOffset
            ).click()
            found = true
        } else if (found) {
            return true
        }
        if (i != maxRetries) {
            retryDelay.wait()
        }
    }
    return false
}
