package io.github.fate_grand_automata.ui.void_mirror

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.fate_grand_automata.R
import io.github.fate_grand_automata.scripts.enums.VoidMirrorBuff
import io.github.fate_grand_automata.ui.Heading
import io.github.fate_grand_automata.ui.dialog.FgaDialog
import io.github.fate_grand_automata.ui.dialog.multiChoiceList
import io.github.fate_grand_automata.ui.drag_sort.DragSort
import io.github.fate_grand_automata.ui.drag_sort.DragSortAdapter

@Composable
fun VoidMirrorView(vm: VoidMirrorViewModel) {
    val dialog = FgaDialog()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Heading(stringResource(R.string.p_void_mirror))

            Button(
                onClick = { dialog.show() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(stringResource(R.string.vm_select_buffs))
            }

            if (vm.enabledBuffs.isEmpty()) {
                Text(
                    stringResource(R.string.vm_no_buffs_selected),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                key(vm.enabledBuffs.joinToString()) {
                    DragSort(
                        items = vm.enabledBuffs,
                        viewConfigGrabber = { buff ->
                            DragSortAdapter.ItemViewConfig(
                                foregroundColor = Color.WHITE,
                                backgroundColor = buff.backgroundColor(context),
                                text = buff.name

                            )
                        }
                    )
                }
            }
        }
    }

    dialog.build {
        title(stringResource(R.string.vm_select_buffs))

        multiChoiceList(
            selected = vm.allBuffs.filter { it.buff in vm.enabledBuffs }.toSet(),
            items = vm.allBuffs,
            onSelectedChange = { newSelected ->
                val selectedBuffs = newSelected.map { it.buff }.toSet()
                vm.enabledBuffs.removeAll { it !in selectedBuffs }
                val existing = vm.enabledBuffs.toSet()
                vm.enabledBuffs.addAll(vm.allBuffs.filter { it.buff in selectedBuffs && it.buff !in existing }.map { it.buff })
            },
            template = { resolved: ResolvedBuff ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = resolved.imageUri,
                        contentDescription = resolved.buff.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(resolved.buff.name)
                }
            }
        )

        buttons(onSubmit = { })
    }
}

private fun VoidMirrorBuff.backgroundColor(context: Context): Int {
    val res = when (this) {
        VoidMirrorBuff.HP -> R.color.colorPrimaryDark
        VoidMirrorBuff.Attack -> R.color.colorServant1
        VoidMirrorBuff.Burn -> R.color.colorServant3
        VoidMirrorBuff.CritDmg -> R.color.colorServant2
        VoidMirrorBuff.Thunder -> R.color.colorAccent
        VoidMirrorBuff.Inspiration -> R.color.colorQuick
        VoidMirrorBuff.RageCritDmg -> R.color.colorBuster
        VoidMirrorBuff.RageAttack -> R.color.colorMasterSkill
    }
    return context.getColor(res)
}
