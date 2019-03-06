package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.MonkeyWrench
import com.novasa.monkeywrench.schematic.Schematic
import java.util.regex.Matcher
import java.util.regex.Pattern

open class RegexFinder(protected val regex: String) : Finder() {

    /** If the regex includes groups, this is the group index of the desired output. Default is 0, which is the entire regex match. */
    open var outputGroupIndex = 0

    override fun findMatches(input: CharSequence, schematic: Schematic): List<MonkeyWrench.Match> {
        val result = ArrayList<MonkeyWrench.Match>()

        val matcher = Pattern.compile(regex).matcher(input)
        while (matcher.find()) {
            val interval = onMatch(schematic, matcher)
            result.add(interval)
        }

        return result
    }

    fun onMatch(schematic: Schematic, matcher: Matcher): MonkeyWrench.Match {
        val sequence = matcher.group(outputGroupIndex)
        val p0 = matcher.start(outputGroupIndex)
        val p1 = matcher.end(outputGroupIndex)

        return onMatch(schematic, matcher, sequence, p0, p1)
    }

    open fun onMatch(schematic: Schematic, matcher: Matcher, sequence: String, p0: Int, p1: Int): MonkeyWrench.Match {
        return MonkeyWrench.Match(schematic, this, sequence, p0, p1)
    }
}