package io.github.fate_grand_automata.ui.dailies_settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fate_grand_automata.scripts.enums.DailiesStep
import io.github.fate_grand_automata.ui.Heading

@Composable
fun DailiesSettingsView(vm: DailiesSettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Heading("Dailies")

        vm.allSteps.forEach { step ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Checkbox(
                    checked = step in vm.enabledSteps,
                    onCheckedChange = { checked ->
                        if (checked) {
                            vm.enabledSteps.add(step)
                        } else {
                            vm.enabledSteps.remove(step)
                        }
                    }
                )

                Text(
                    text = step.displayName,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider()
        }
    }
}
