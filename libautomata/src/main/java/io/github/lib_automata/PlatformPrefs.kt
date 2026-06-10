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
     * Minimum scale factor for template matching, stored as percentage (50-150).
     */
    var matchingScaleMin: Int

    /**
     * Maximum scale factor for template matching, stored as percentage (50-150).
     */
    var matchingScaleMax: Int
}