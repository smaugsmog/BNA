package io.github.fate_grand_automata.ui.void_mirror

import android.net.Uri
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.fate_grand_automata.prefs.core.PrefsCore
import io.github.fate_grand_automata.scripts.enums.VoidMirrorBuff
import javax.inject.Inject

data class ResolvedBuff(
    val buff: VoidMirrorBuff,
    val imageUri: Uri?
)

@HiltViewModel
class VoidMirrorViewModel @Inject constructor(
    private val prefsCore: PrefsCore
) : ViewModel() {
    private val rootUriString = prefsCore.dirRoot.get()

    val allBuffs: List<ResolvedBuff> = VoidMirrorBuff.entries.map {
        ResolvedBuff(it, resolveImageUri(it))
    }

    val enabledBuffs: SnapshotStateList<VoidMirrorBuff> by lazy {
        prefsCore.voidMirrorBuffPriority.get().toMutableStateList()
    }

    fun save() {
        prefsCore.voidMirrorBuffPriority.set(enabledBuffs.toList())
    }

    private fun resolveImageUri(buff: VoidMirrorBuff): Uri? {
        if (rootUriString.isBlank()) return null
        val treeUri = Uri.parse(rootUriString)
        val treeDocId = android.provider.DocumentsContract.getTreeDocumentId(treeUri)
        val childDocId = "$treeDocId/${buff.image.path}"
        return android.provider.DocumentsContract.buildDocumentUriUsingTree(treeUri, childDocId)
    }
}
