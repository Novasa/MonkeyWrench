package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.MonkeyWrench
import com.novasa.monkeywrench.schematic.Schematic

class GlobalFinder : Finder() {
    override fun findMatches(input: CharSequence, schematic: Schematic): List<MonkeyWrench.Match> {
        return listOf(MonkeyWrench.Match(schematic, this, input.subSequence(0, input.length), 0, input.length))
    }
}