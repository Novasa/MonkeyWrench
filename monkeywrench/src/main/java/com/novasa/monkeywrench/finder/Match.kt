package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic

/**
 * Represents a match in the input, found by a [Finder].
 * The output might have been altered by the [Finder], e.g. if the finder is based on surrounding html tags,
 * The tags will not be included in the output.
 * @param input The matched (partial) input in the original input.
 * @param output The output, which may differ from the input, for example if html tags are removed from the matching text.
 * @param p0 The start position of the matched input in the original input (NOT the output, if it differs).
 */
open class Match(val input: CharSequence, val output: CharSequence, val p0: Int) {
    internal lateinit var schematic: Schematic
}
