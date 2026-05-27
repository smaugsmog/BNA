package io.github.fate_grand_automata.scripts.enums

import io.github.fate_grand_automata.scripts.Images

enum class VoidMirrorBuff(val image: Images) {
    HP(Images.VoidBuffHP),
    Attack(Images.VoidBuffAttack),
    Burn(Images.VoidBuffBurn),
    CritDmg(Images.VoidBuffCritDmg),
    Thunder(Images.VoidBuffThunder),
    Inspiration(Images.VoidBuffInspiration),
    RageCritDmg(Images.VoidRageCritDmg),
    RageAttack(Images.VoidRageAttack);
}
