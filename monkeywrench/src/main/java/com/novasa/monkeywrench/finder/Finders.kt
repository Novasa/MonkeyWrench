package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.ClickSchematic
import com.novasa.monkeywrench.schematic.Schematic
import java.util.regex.Matcher
import java.util.regex.Pattern

open class IntervalFinder(protected val intervals: Array<out Interval>) : Finder {

    override fun findMatches(schematic: Schematic, input: CharSequence): List<Match> {
        val result = ArrayList<Match>(intervals.size)

        intervals.forEach { interval ->
            val p0 = interval.i0
            val p1 = interval.i1
            if (p0 < 0 || p0 > p1 || p1 > input.length) {
                throw IllegalArgumentException("Bad interval: $p0 - $p1. Length was ${input.length}.")
            }

            val output = input.subSequence(p0, p1)
            result.add(Match(schematic, output, p0, output))
        }

        return result
    }

    data class Interval(val i0: Int, val i1: Int)
}

infix fun Int.to(that: Int): IntervalFinder.Interval = IntervalFinder.Interval(this, that)

/**
 * @property outputGroupIndex If the regex includes groups, this is the group index of the desired output. Default is 0, which is the entire regex match.
 */
open class RegexFinder(val regex: String, val outputGroupIndex: Int = 0) : Finder {

    override fun findMatches(schematic: Schematic, input: CharSequence): List<Match> {
        val result = ArrayList<Match>()

        val matcher = Pattern.compile(regex).matcher(input)
        while (matcher.find()) {
            val match = onMatch(schematic, matcher)
            result.add(match)
        }

        return result
    }

    private fun onMatch(schematic: Schematic, matcher: Matcher): Match {
        val input = matcher.group(0)
        val p0 = matcher.start(0)
        val output = matcher.group(outputGroupIndex)

        return onMatch(matcher, schematic, input, p0, output)
    }

    open fun onMatch(matcher: Matcher, schematic: Schematic, input: String, p0: Int, output: String): Match = Match(schematic, input, p0, output)
}

/** Use for simple open/close tag matches, like html tags. */
open class TagFinder(open: CharSequence, close: CharSequence, outputGroupIndex: Int = 1) : RegexFinder("$open(.*?)$close", outputGroupIndex)

/** Use for html tags. */
open class HtmlTagFinder(tag: CharSequence, outputGroupIndex: Int = 1) : TagFinder("<$tag>", "</$tag>", outputGroupIndex)

/** Use for html tags that need to extract a attribute value. */
open class HtmlTagAttributeFinder(tag: CharSequence, attribute: CharSequence) : TagFinder("<$tag $attribute=(.*?)>", "</$tag>", 2) {

    override fun onMatch(matcher: Matcher, schematic: Schematic, input: String, p0: Int, output: String): Match {
        val attributeValue = matcher.group(1).trim('\"', '\'')
        return onMatch(matcher, schematic, input, p0, output, attributeValue)
    }

    open fun onMatch(matcher: Matcher, schematic: Schematic, input: String, p0: Int, output: String, attributeValue: String): Match {
        return ValueMatch(schematic, input, p0, output, attributeValue)
    }
}
