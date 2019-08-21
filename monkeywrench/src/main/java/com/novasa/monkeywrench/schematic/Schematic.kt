package com.novasa.monkeywrench.schematic

import android.text.TextPaint
import android.widget.TextView
import com.novasa.monkeywrench.finder.Finder
import com.novasa.monkeywrench.finder.GlobalMatch
import com.novasa.monkeywrench.finder.Match
import com.novasa.monkeywrench.span.Span

open class Schematic {

    open class Output(val sequence: CharSequence, val span: Any?)

    private val finders = ArrayList<Finder>()
    private val mutaters = ArrayList<Mutater>()
    private val bits = ArrayList<Bit>()

    fun addFinder(finder: Finder): Schematic = this.also {
        finders.add(finder)
    }

    fun addMutater(mutater: Mutater): Schematic = this.also {
        mutaters.add(mutater)
    }

    fun addBit(bit: Bit): Schematic = this.also {
        bits.add(bit)
    }

    open fun getOutput(match: Match): Output {

        var sequence = match.output

        // Mutaters can manipulate the input sequence before applying bits
        for (mutater in mutaters) {
            sequence = mutater.apply(sequence)
        }

        val span = createSpan(match)

        return Output(sequence, span)
    }

    open fun createSpan(match: Match): Any? {
        return Span(match)
    }

    internal fun findMatches(input: CharSequence): List<Match> {
        if (finders.isEmpty()) {
            return listOf(GlobalMatch(this, input))
        }
        return finders.flatMap {
            it.findMatches(this, input)
        }
    }

    open fun apply(paint: TextPaint, match: Match) {
        for (bit in bits) {
            bit.apply(paint, match)
        }
    }

    open fun setupTextView(textView: TextView) {}
}
