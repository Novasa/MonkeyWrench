package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.MonkeyWrench
import com.novasa.monkeywrench.schematic.ClickSchematic
import com.novasa.monkeywrench.schematic.Schematic
import java.util.regex.Matcher

class HrefFinder : TagFinder("<a href=(.*?)>", "</a>") {

    override var outputGroupIndex: Int = 2

    override fun onMatch(schematic: Schematic, matcher: Matcher, sequence: String, p0: Int, p1: Int): MonkeyWrench.Match {
        val href = matcher.group(1)
        return MonkeyWrench.HrefMatch(schematic as ClickSchematic, this, sequence, p0, p1, href)
    }
}