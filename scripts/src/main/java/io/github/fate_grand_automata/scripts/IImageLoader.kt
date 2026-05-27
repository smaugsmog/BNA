package io.github.fate_grand_automata.scripts

import io.github.fate_grand_automata.SupportImageKind
import io.github.fate_grand_automata.scripts.enums.GameServer
import io.github.fate_grand_automata.scripts.enums.MaterialEnum
import io.github.lib_automata.Pattern

interface IImageLoader {
    operator fun get(img: Images, gameServer: GameServer? = null, masked: Boolean = false): Pattern

    /**
     * Loads an image template whose non-icon background has been filled with a flat color.
     * The top-left pixel is treated as the background color and masked out during matching.
     */
    fun getMasked(img: Images, gameServer: GameServer? = null): Pattern =
        get(img, gameServer, masked = true)

    fun loadSupportPattern(kind: SupportImageKind, name: String): List<Pattern>

    fun loadMaterial(material: MaterialEnum): Pattern

    fun clearImageCache()

    fun clearSupportCache()
}