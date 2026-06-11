package io.github.fate_grand_automata.scripts.bna.entrypoints

import io.github.fate_grand_automata.scripts.bna.DailiesLogic
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ExitManager
import io.github.lib_automata.dagger.ScriptScope
import javax.inject.Inject

@ScriptScope
class Dailies @Inject constructor(
    exitManager: ExitManager,
    private val dailiesLogic: DailiesLogic
) : EntryPoint(exitManager) {

    override fun script(): Nothing {
        dailiesLogic.dailiesLogic()
    }
}
