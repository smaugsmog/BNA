package io.github.fate_grand_automata.ui.void_mirror

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VoidMirrorScreen(
    vm: VoidMirrorViewModel = viewModel()
) {
    VoidMirrorView(vm = vm)

    DisposableEffect(vm) {
        onDispose {
            vm.save()
        }
    }
}
