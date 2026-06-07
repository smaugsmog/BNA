package io.github.lib_automata

interface PlatformPrefs {
    val debugMode: Boolean

    /**
     * The default minimum similarity used for image comparisons.
     */
    val minSimilarity: Double

    val waitMultiplier: Double

    val swipeMultiplier: Double

    /**
     * Set of scale factors that have successfully matched, stored as percentages (50-150).
     * Empty means uncalibrated - will use wide range.
     */
    var matchingScales: MutableSet<Int>

    /**
     * Whether the next search should test wide range to discover new working scales.
     */
    var pendingRecalibration: Boolean
}