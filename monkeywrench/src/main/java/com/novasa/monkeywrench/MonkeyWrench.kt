package com.novasa.monkeywrench

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.widget.TextView
import com.novasa.monkeywrench.finder.*
import com.novasa.monkeywrench.schematic.ClickSchematic
import com.novasa.monkeywrench.schematic.Schematic
import java.util.*
import kotlin.collections.ArrayList

@Suppress("unused", "MemberVisibilityCanBePrivate")
class MonkeyWrench private constructor() {

    companion object {

        const val TAG = "MonkeyWrench"

        @JvmStatic
        fun build(): MonkeyWrench = MonkeyWrench()

        @JvmStatic
        fun allOfIt(): Schematic = Schematic(GlobalFinder())

        @JvmStatic
        fun tag(open: CharSequence, close: CharSequence): Schematic = Schematic(TagFinder(open, close))

        @JvmStatic
        fun html(tag: String): Schematic = tag("<$tag>", "</$tag>")

        @JvmStatic
        fun htmlBold(): Schematic = html("b")

        @JvmStatic
        fun htmlLink(): ClickSchematic = ClickSchematic()

        @JvmStatic
        fun interval(vararg intervals: IntervalFinder.Interval): Schematic = Schematic(IntervalFinder(intervals))

        @JvmStatic
        fun regex(regex: String): Schematic = Schematic(RegexFinder(regex))

        fun span(input: CharSequence, setup: MonkeyWrench.() -> Unit): CharSequence {
            val instance = build()
            setup(instance)
            return instance.doTheThing(input)
        }

        fun span(textView: TextView, setup: MonkeyWrench.() -> Unit) {
            span(textView.text, textView, setup)
        }

        fun span(input: CharSequence, textView: TextView, setup: MonkeyWrench.() -> Unit) {
            val instance = MonkeyWrench()
            setup(instance)
            instance.doTheThingUnto(input, textView)
        }
    }

    private val schematics = ArrayList<Schematic>()

    fun addSchematic(schematic: Schematic): MonkeyWrench {
        schematics.add(schematic)
        return this
    }

    // region Kotlin builder methods

    fun <T : Schematic> schematic(schematic: T, setup: T.() -> Unit) {
        addSchematic(schematic)
        setup(schematic)
    }

    fun allOfIt(setup: Schematic.() -> Unit) = schematic(allOfIt(), setup)
    fun tag(open: CharSequence, close: CharSequence, setup: Schematic.() -> Unit) = schematic(tag(open, close), setup)
    fun html(tag: String, setup: Schematic.() -> Unit) = schematic(tag("<$tag>", "</$tag>"), setup)
    fun htmlBold(setup: Schematic.() -> Unit) = schematic(html("b"), setup)
    fun htmlLink(setup: ClickSchematic.() -> Unit) = schematic(htmlLink(), setup)
    fun interval(vararg intervals: Pair<Int, Int>, setup: Schematic.() -> Unit) = schematic(interval(*(intervals.map { IntervalFinder.Interval(it.first, it.second) }).toTypedArray()), setup)
    fun regex(regex: String, setup: Schematic.() -> Unit) = schematic(regex(regex), setup)

    // endregion

    /**
     * Does the thing
     */
    fun doTheThing(input: CharSequence): CharSequence {

        val matches = ArrayList<Match>()

        schematics.forEach { schematic ->
            matches.addAll(schematic.findMatches(input))
        }

        matches.sortWith(Comparator { o1, o2 ->
            if (o1.p0 < o2.p0) -1 else if (o1.p0 > o2.p0) 1 else 0
        })

        val builder = SpannableStringBuilder()
        var end = 0

        for (match in matches) {

            val output = match.schematic.getOutput(match)

            // Start from end of previous match, until before the opening tag of the match
            val start = match.p0 - match.openLength

            // Append text before the match with no span
            if (start > end) {
                // If start is after end, we have overlapping matches
                builder.append(input.subSequence(end, start))
            }

            // Position before appending the output
            val p0 = builder.length

            // Append the sequence
            builder.append(output.sequence)

            output.span?.let { span ->
                // Set the span
                builder.setSpan(span, p0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // Save the position after the closing tag, so we can append the next piece of text before the next match
            end = match.p1 + match.closeLength
        }

        // Append remaining text after the final match, or everything if there were no matches
        builder.append(input.subSequence(end, input.length))

        return builder
    }

    /**
     * Does the thing to the text view
     */
    fun doTheThingUnto(input: CharSequence, textView: TextView) {
        textView.text = doTheThing(input)
        schematics.forEach { span ->
            span.setupTextView(textView)
        }
    }
}
