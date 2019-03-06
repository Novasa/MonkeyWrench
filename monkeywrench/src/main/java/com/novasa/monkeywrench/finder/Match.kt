package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic

/**
 * Represents a match in the input, found by a [Finder].
 */
open class Match(val schematic: Schematic, finder: Finder, val sequence: CharSequence, val p0: Int, val p1: Int) {
    open val openLength: Int = finder.openLength
    open val closeLength: Int = finder.closeLength

    override fun toString(): String = "$p0 - $p1 (o: $openLength, c: $closeLength)"


}
