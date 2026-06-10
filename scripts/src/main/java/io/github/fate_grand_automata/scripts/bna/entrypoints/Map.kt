package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.bna.MapLogic
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

@ScriptScope
class Map @Inject constructor(
    exitManager: ExitManager,
    private val mapLogic: MapLogic
) : EntryPoint(exitManager) {

    override fun script(): Nothing {
        mapLogic.mapLogic()
    }
}