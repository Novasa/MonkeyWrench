package com.novasa.monkeywrench.schematic

import com.novasa.monkeywrench.finder.Finder
import com.novasa.monkeywrench.finder.Match
/**
 * A Mutater is able to change a [Match] found by a [Finder] before the [Bit]s are applied.
 */
interface Mutater {
    fun apply(input: CharSequence): CharSequence
}