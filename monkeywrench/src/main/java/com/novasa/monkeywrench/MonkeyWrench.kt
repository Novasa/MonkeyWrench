package com.novasa.monkeywrench

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.widget.TextView
import com.novasa.monkeywrench.finder.Finder
import com.novasa.monkeywrench.finder.Match
import com.novasa.monkeywrench.schematic.Bit
import com.novasa.monkeywrench.schematic.Mutater
import com.novasa.monkeywrench.schematic.Schematic
import com.novasa.monkeywrench.schematic.Schematics
import java.util.*
import kotlin.collections.ArrayList

/**
 * [MonkeyWrench] overview:
 *
 * An instance consists of one or more [Schematic]s.
 *
 * A [Schematic] consists of three types of parts:
 * 1. [Finder]s find [Match]es in the input text, that the [Schematic] will work on.
 *      If no finders are supplied, the schematic will work on the entire input.
 * 2. [Mutater]s mutate the input text found by the [Finder]s.
 * 3. [Bit]s apply effects to the output text, like text color, scale or clickable links.
 */
class MonkeyWrench private constructor() {

    companion object {

        const val TAG = "MonkeyWrench"

        @JvmStatic
        fun create(): MonkeyWrench = MonkeyWrench()

        fun create(setup: MonkeyWrench.() -> Unit): MonkeyWrench = create().also(setup)

        fun work(input: CharSequence, setup: MonkeyWrench.() -> Unit): CharSequence = create(setup).work(input)

        fun workOn(textViews: Array<TextView>, setup: MonkeyWrench.() -> Unit) {
            create(setup).apply {
                for (textView in textViews) {
                    workOn(textView)
                }
            }
        }

        fun workOn(textView: TextView, setup: MonkeyWrench.() -> Unit) {
            workOn(textView.text, textView, setup)
        }

        fun workOn(input: CharSequence, textView: TextView, setup: MonkeyWrench.() -> Unit) {
            val instance = MonkeyWrench()
            setup(instance)
            instance.workOn(input, textView)
        }
    }

    private val schematics = ArrayList<Schematic>()

    fun addSchematic(schematic: Schematic): MonkeyWrench {
        schematics.add(schematic)
        return this
    }

    fun addSchematic(setup: Schematic.() -> Unit) {
        addSchematic(Schematics.create(), setup)
    }

    fun <T : Schematic> addSchematic(schematic: T, setup: T.() -> Unit) {
        addSchematic(schematic.also(setup))
    }

    /**
     * Does the thing
     */
    fun work(input: CharSequence): CharSequence {

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
            val start = match.p0

            // Append text before the match with no span
            if (start > end) {
                // If start is after end, we have overlapping matches
                builder.append(input.subSequence(end, start))
            }

            // Position before appending the output
            val p0 = builder.length

            // Append the output
            builder.append(output.sequence)

            output.span?.let { span ->
                // Set the span
                builder.setSpan(span, p0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // Save the position after the closing tag, so we can append the next piece of text before the next match
            end = match.p0 + match.input.length
        }

        // Append remaining text after the final match, or everything if there were no matches
        builder.append(input.subSequence(end, input.length))

        return builder
    }

    fun workOn(textView: TextView) {
        workOn(textView.text, textView)
    }

    fun workOn(input: CharSequence, textView: TextView) {
        textView.text = work(input)
        schematics.forEach { s: Schematic ->
            s.setupTextView(textView)
        }
    }
}
