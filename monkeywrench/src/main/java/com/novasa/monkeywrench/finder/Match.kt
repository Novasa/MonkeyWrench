package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic

/**
 * Represents a match in the input, found by a [Finder].
 * The output might have been altered by the [Finder], e.g. if the finder is based on surrounding html tags,
 * The tags will not be included in the output.
 */
open class Match(val schematic: Schematic, val input: CharSequence, val p0: Int, val output: CharSequence)
