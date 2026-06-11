package io.github.fate_grand_automata.ui.dailies_settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DailiesSettingsScreen(
    vm: DailiesSettingsViewModel = viewModel()
) {
    DailiesSettingsView(vm = vm)

    DisposableEffect(vm) {
        onDispose {
            vm.save()
        }
    }
}
