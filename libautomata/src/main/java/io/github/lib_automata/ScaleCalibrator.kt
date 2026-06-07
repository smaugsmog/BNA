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
        val stored = prefs.matchingScales
        if (stored.isEmpty()) return wideScales

        return stored.map { it / 100.0 }.sorted()
    }

    fun addWorkingScale(scale: Double) {
        val pct = (scale * 100).roundToInt().coerceIn(50, 150)
        prefs.matchingScales.add(pct)
    }

    fun recalibrate() {
        prefs.pendingRecalibration = true
    }

    fun finalizeRecalibration(workingScales: List<Double>) {
        val existing = prefs.matchingScales.map { it / 100.0 }
        val newScales = workingScales.filter { scale ->
            existing.none { it == scale }
        }
        if (newScales.isNotEmpty()) {
            val bestNew = newScales.maxByOrNull { it }!!
            addWorkingScale(bestNew)
        }
        prefs.pendingRecalibration = false
    }

    val isCalibrating: Boolean
        get() = prefs.matchingScales.isEmpty()

    val isRecalibrating: Boolean
        get() = prefs.pendingRecalibration
}