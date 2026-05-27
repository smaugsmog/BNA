package io.github.fate_grand_automata.ui.void_mirror

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.fate_grand_automata.prefs.core.PrefsCore
import io.github.fate_grand_automata.scripts.enums.VoidMirrorBuff
import javax.inject.Inject

data class ResolvedBuff(val buff: VoidMirrorBuff)

@HiltViewModel
class VoidMirrorViewModel @Inject constructor(
    private val prefsCore: PrefsCore
) : ViewModel() {
    val allBuffs: List<ResolvedBuff> = VoidMirrorBuff.entries.map { ResolvedBuff(it) }

    val enabledBuffs: SnapshotStateList<VoidMirrorBuff> by lazy {
        prefsCore.voidMirrorBuffPriority.get().toMutableStateList()
    }

    fun save() {
        prefsCore.voidMirrorBuffPriority.set(enabledBuffs.toList())
    }
}
