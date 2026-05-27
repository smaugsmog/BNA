package io.github.fate_grand_automata.ui.launcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.fate_grand_automata.ui.FgaScreen

@Composable
fun BnaScriptLauncher(
    onResponse: (BnaScript?) -> Unit
) {
    FgaScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "BNA",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

//                TextButton(
//                    onClick = { onResponse(BnaScript.Dailies) },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Dailies")
//                }

                TextButton(
                    onClick = { onResponse(BnaScript.Map) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Map Missions")
                }

                TextButton(
                    onClick = { onResponse(BnaScript.Metaspace) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Metaspace")
                }

                TextButton(
                    onClick = { onResponse(BnaScript.VoidMirror) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Void Mirror")
                }
            }

            HorizontalDivider()

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { onResponse(null) }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        }
    }
}
