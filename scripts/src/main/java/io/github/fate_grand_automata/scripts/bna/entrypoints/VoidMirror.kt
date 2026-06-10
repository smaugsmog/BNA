package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.bna.VoidMirrorLogic
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

@ScriptScope
class VoidMirror @Inject constructor(
    exitManager: ExitManager,
    private val voidMirrorLogic: VoidMirrorLogic
) : EntryPoint(exitManager) {

    override fun script(): Nothing {
        voidMirrorLogic.voidMirrorLogic()
    }
}