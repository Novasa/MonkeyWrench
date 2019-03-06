package com.novasa.monkeywrench.schematic

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.widget.TextView
import com.novasa.monkeywrench.finder.Finder
import com.novasa.monkeywrench.finder.Match
import com.novasa.monkeywrench.span.Span

open class Schematic(val finders: List<Finder>) {

    constructor(vararg finders: Finder) : this(finders.asList())

    private val mutaters = ArrayList<Mutater>()
    private val painters = ArrayList<Painter>()

    open fun getOutput(match: Match): Output {
        var sequence = match.sequence
        mutaters.forEach {
            sequence = it.apply(sequence)
        }

        val span = createSpan(match)

        return Output(sequence, span)
    }

    open fun createSpan(match: Match): Any? {
        return Span(match)
    }

    protected var typeFace: Typeface? = null
    protected var color: Int? = null
    protected var bgColor: Int? = null
    protected var scale: Float? = null
    protected var underline = false
    protected var strikethrough = false
    protected var fakeBold = false

    fun typeFace(typeface: Typeface): Schematic {
        typeFace = typeface
        return this
    }

    fun color(color: Int): Schematic {
        this.color = color
        return this
    }

    fun backgroundColor(color: Int): Schematic {
        this.bgColor = color
        return this
    }

    fun scale(scale: Float): Schematic {
        this.scale = scale
        return this
    }

    fun underline(): Schematic {
        this.underline = true
        return this
    }

    fun strikethrough(): Schematic {
        this.strikethrough = true
        return this
    }

    fun fakeBold(): Schematic {
        this.fakeBold = true
        return this
    }

    internal fun findMatches(input: CharSequence): List<Match> = finders.flatMap {
        it.findMatches(input, this)
    }

    open fun apply(paint: Paint, sequence: CharSequence) {

        typeFace?.let {
            paint.typeface = it
        }

        color?.let {
            paint.color = it
        }

        bgColor?.let {
            if (paint is TextPaint) {
                paint.bgColor = it
            }
        }

        scale?.let {
            paint.textSize *= it
        }

        paint.isUnderlineText = underline
        paint.isStrikeThruText = strikethrough
        paint.isFakeBoldText = fakeBold
    }

    open fun setupTextView(textView: TextView) {

    }

    open class Output(val sequence: CharSequence, val span: Any?)
}
