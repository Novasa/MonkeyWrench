package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.MonkeyWrench.Match
import com.novasa.monkeywrench.schematic.Schematic

/** Finds [Match]es in the input, that the [Schematic] is applied to. */
abstract class Finder {

    /** Must return the length of any opening tag that is not included in the output. Example: <b> = 3 */
    open val openLength: Int = 0

    /** Must return the length of any closing tag that is not included in the output. Example: </b> = 4 */
    open val closeLength: Int = 0

    abstract fun findMatches(input: CharSequence, schematic: Schematic): List<Match>
}