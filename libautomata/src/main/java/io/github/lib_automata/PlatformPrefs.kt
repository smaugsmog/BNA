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
     * Whether the scale calibrator has locked onto a scale range.
     */
    var matchingScaleCalibrated: Boolean

    /**
     * The minimum scale factor to try, stored as percentage (50-150).
     */
    var matchingScaleMin: Int

    /**
     * The maximum scale factor to try, stored as percentage (50-150).
     */
    var matchingScaleMax: Int
}