package io.github.fate_grand_automata.di.script

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import io.github.fate_grand_automata.scripts.bna.entrypoints.Dailies
import io.github.fate_grand_automata.scripts.bna.entrypoints.Metaspace
import io.github.fate_grand_automata.scripts.bna.entrypoints.Map
import io.github.fate_grand_automata.scripts.bna.entrypoints.VoidMirror
import io.github.fate_grand_automata.scripts.entrypoints.*

@EntryPoint
@InstallIn(ScriptComponent::class)
interface ScriptEntryPoint {
    fun metaspace(): Metaspace
    fun map(): Map
    fun voidMirror(): VoidMirror
    fun dailies(): Dailies

    fun battle(): AutoBattle
    fun fp(): AutoFriendGacha
    fun giftBox(): AutoGiftBox
    fun lottery(): AutoLottery
    fun supportImageMaker(): SupportImageMaker
    fun ceBomb(): AutoCEBomb

    fun servantLevel(): AutoServantLevel

    fun autoDetect(): AutoDetect
}