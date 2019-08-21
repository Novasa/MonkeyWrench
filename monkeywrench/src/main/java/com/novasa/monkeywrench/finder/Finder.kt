package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic

/** Finds [Match]es in the input, that the [Schematic] is applied to. */
interface Finder {
    fun findMatches(schematic: Schematic, input: CharSequence): List<Match>
}