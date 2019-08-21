package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic

/**
 * Represents a match in the input, found by a [Finder].
 */
open class Match(val schematic: Schematic, val input: CharSequence, val p0: Int, val output: CharSequence)
