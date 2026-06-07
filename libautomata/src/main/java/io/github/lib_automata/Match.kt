package io.github.lib_automata

/**
 * Represents an image search match, containing the match area and the matching score.
 *
 * @property scale the template scale factor that produced this match (default 1.0)
 */
data class Match(val region: Region, val score: Double, val scale: Double = 1.0) : Comparable<Match> {
    override fun compareTo(other: Match) =
        region.compareTo(other.region)
}