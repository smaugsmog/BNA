package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.bna.MetaspaceLogic
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

@ScriptScope
class Metaspace @Inject constructor(
    exitManager: ExitManager,
    private val metaspaceLogic: MetaspaceLogic
) : EntryPoint(exitManager) {

    override fun script(): Nothing {
        metaspaceLogic.metaspaceLogic()
    }
}