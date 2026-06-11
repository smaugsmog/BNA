package io.github.fate_grand_automata.ui.dailies_settings

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.fate_grand_automata.prefs.core.PrefsCore
import io.github.fate_grand_automata.scripts.enums.DailiesStep
import javax.inject.Inject

@HiltViewModel
class DailiesSettingsViewModel @Inject constructor(
    private val prefsCore: PrefsCore
) : ViewModel() {
    val allSteps: List<DailiesStep> = DailiesStep.entries

    val enabledSteps: SnapshotStateList<DailiesStep> by lazy {
        prefsCore.dailiesEnabledSteps.get().toMutableStateList()
    }

    fun save() {
        prefsCore.dailiesEnabledSteps.set(enabledSteps.toList())
    }
}
