package io.github.lib_automata

import kotlin.math.roundToInt

class ScaleCalibrator(private val prefs: PlatformPrefs) {
    companion object {
        const val WIDE_MIN = 0.50
        const val WIDE_MAX = 1.50
        const val WIDE_STEP = 0.05
    }

    val wideScales: List<Double> = generateSequence(WIDE_MIN) {
        (it + WIDE_STEP).takeIf { it <= WIDE_MAX + 0.001 }
    }.toList()

    fun getScales(): List<Double> {
        if (!prefs.matchingScaleCalibrated) return wideScales

        val min = prefs.matchingScaleMin / 100.0
        val max = prefs.matchingScaleMax / 100.0

        if (min == max) return listOf(min)

        return generateSequence(min) {
            (it + WIDE_STEP).takeIf { it <= max + 0.001 }
        }.toList()
    }

    fun calibrate(bestScale: Double) {
        val pct = (bestScale * 100).roundToInt().coerceIn(1, 200)

        if (!prefs.matchingScaleCalibrated) {
            prefs.matchingScaleCalibrated = true
            prefs.matchingScaleMin = pct
            prefs.matchingScaleMax = pct
        } else {
            if (pct < prefs.matchingScaleMin) prefs.matchingScaleMin = pct
            if (pct > prefs.matchingScaleMax) prefs.matchingScaleMax = pct
        }
    }

    fun recalibrate() {
        prefs.matchingScaleCalibrated = false
    }

    val isCalibrating: Boolean
        get() = !prefs.matchingScaleCalibrated
}
