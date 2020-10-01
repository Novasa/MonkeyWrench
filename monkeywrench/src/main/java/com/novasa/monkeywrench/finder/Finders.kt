@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic
import java.util.regex.Matcher
import java.util.regex.Pattern


// region Implementations

open class IntervalFinder(protected val intervals: Array<out Interval>) : Finder {

    override fun findMatches(input: CharSequence): List<Match> {
        val result = ArrayList<Match>(intervals.size)

        intervals.forEach { interval ->
            val p0 = interval.i0
            val p1 = interval.i1

            require(!(p0 < 0 || p0 > p1 || p1 > input.length)) {
                "Bad interval: $p0 - $p1. Length was ${input.length}."
            }

            val output = input.subSequence(p0, p1)
            result.add(Match(output, output, p0))
        }

        return result
    }

    data class Interval(val i0: Int, val i1: Int)
}

infix fun Int.to(that: Int): IntervalFinder.Interval = IntervalFinder.Interval(this, that)

/**
 * @param outputGroupIndex If the regex includes groups, this is the group index of the desired output. Default is 0, which is the entire regex match.
 */
open class RegexFinder(val regex: String, val outputGroupIndex: Int = 0) : Finder {

    override fun findMatches(input: CharSequence): List<Match> {
        val result = ArrayList<Match>()

        val matcher = Pattern.compile(regex).matcher(input)
        while (matcher.find()) {
            val match = onMatch(matcher)
            result.add(match)
        }

        return result
    }

    private fun onMatch(matcher: Matcher): Match {
        val input = matcher.group(0)
        val p0 = matcher.start(0)
        val output = matcher.group(outputGroupIndex)

        return onMatch(matcher, input, p0, output)
    }

    open fun onMatch(matcher: Matcher, input: String, p0: Int, output: String): Match = Match(input, output, p0)
}

/** Use for simple open/close tag matches, like html tags. */
open class TagFinder(open: CharSequence, close: CharSequence, outputGroupIndex: Int = 1) : RegexFinder("$open(.*?)$close", outputGroupIndex)

/** Use for html tags. */
open class HtmlTagFinder(tag: CharSequence, outputGroupIndex: Int = 1) : TagFinder("<$tag>", "</$tag>", outputGroupIndex)

/** Use for html tags that need to extract an attribute value. */
open class HtmlTagAttributeFinder(tag: CharSequence, attribute: CharSequence) : TagFinder("<$tag\\s+(?:[^>]*?\\s+)?$attribute=([\"'])(.*?)\\1.*?>", "</$tag>", 3) {

    override fun onMatch(matcher: Matcher, input: String, p0: Int, output: String): Match {
        val attributeValue = matcher.group(2).trim('\"', '\'')
        return onMatch(matcher, input, p0, output, attributeValue)
    }

    open fun onMatch(matcher: Matcher, input: String, p0: Int, output: String, attributeValue: String): Match {
        return ValueMatch(input, p0, output, attributeValue)
    }
}

// endregion


// region JVM Statics

object Finders {

    @JvmStatic
    fun createInterval(vararg intervals: IntervalFinder.Interval) = IntervalFinder(intervals)

    @JvmStatic
    fun createRegex(regex: String, outputGroupIndex: Int = 0) = RegexFinder(regex, outputGroupIndex)

    @JvmStatic
    fun createTag(open: CharSequence, close: CharSequence, outputGroupIndex: Int = 1) = TagFinder(open, close, outputGroupIndex)

    @JvmStatic
    fun createHtmlTag(tag: CharSequence, outputGroupIndex: Int = 1) = HtmlTagFinder(tag, outputGroupIndex)

    @JvmStatic
    fun createHtmlTagAttribute(tag: CharSequence, attribute: CharSequence) = HtmlTagAttributeFinder(tag, attribute)

    @JvmStatic
    fun createHtmlBold() = createHtmlTag("b")

    @JvmStatic
    fun createHtmlUnderline() = createHtmlTag("u")

    @JvmStatic
    fun createHtmlALink() = createHtmlTagAttribute("a", "href")

    @JvmStatic
    fun createHttpLink() = createRegex("https?:\\/\\/+[^\\s]+[\\w]")
}

// endregion


// region Schematic Extensions

fun Schematic.addFinderIntervals(vararg intervals: IntervalFinder.Interval) = addFinder(Finders.createInterval(*intervals))
fun Schematic.addFinderRegex(regex: String, outputGroupIndex: Int = 0) = addFinder(Finders.createRegex(regex, outputGroupIndex))
fun Schematic.addFinderTag(open: CharSequence, close: CharSequence, outputGroupIndex: Int = 1) = addFinder(Finders.createTag(open, close, outputGroupIndex))
fun Schematic.addFinderHtmlTag(tag: CharSequence, outputGroupIndex: Int = 1) = addFinder(Finders.createHtmlTag(tag, outputGroupIndex))
fun Schematic.addFinderHtmlTagAttribute(tag: CharSequence, attribute: CharSequence) = addFinder(Finders.createHtmlTagAttribute(tag, attribute))
fun Schematic.addFinderHtmlBold() = addFinder(Finders.createHtmlBold())
fun Schematic.addFinderHtmlUnderline() = addFinder(Finders.createHtmlUnderline())
fun Schematic.addFinderHtmlALink() = addFinder(Finders.createHtmlALink())
fun Schematic.addFinderHttpLink() = addFinder(Finders.createHttpLink())

// endregion
