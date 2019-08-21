package com.novasa.monkeywrench.schematic

import android.graphics.Color
import android.net.Uri
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import com.novasa.monkeywrench.MonkeyWrench
import com.novasa.monkeywrench.finder.*
import com.novasa.monkeywrench.span.ClickSpan

// region Subclasses

class ClickSchematic : Schematic() {

    private var onClickEvent: ((Uri?) -> Unit)? = null

    override fun createSpan(match: Match): Any? {
        val uri: Uri? = if (match is ValueMatch) {
            Uri.parse(match.value)
        } else null

        return ClickSpan(match, uri)
    }

    fun onClick(onClick: (Uri?) -> Unit): ClickSchematic {
        this.onClickEvent = onClick
        return this
    }

    internal fun onClick(uri: Uri?) {
        onClickEvent?.let {
            it(uri)
        } ?: Log.w(MonkeyWrench.TAG, "Click Wrench was poked, but no handler has been supplied. Do the onClick() thing in the setup please.")
    }

    override fun setupTextView(textView: TextView) {
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

// endregion


// region JVM Statics

object Schematics {

    @JvmStatic
    fun allOfIt(): Schematic = Schematic()

    @JvmStatic
    fun withFinder(finder: Finder): Schematic = Schematic().apply {
        addFinder(finder)
    }

    @JvmStatic
    fun interval(vararg intervals: IntervalFinder.Interval): Schematic = withFinder(IntervalFinder(intervals))

    @JvmStatic
    fun regex(regex: String): Schematic = withFinder(RegexFinder(regex))

    @JvmStatic
    fun tag(open: CharSequence, close: CharSequence): Schematic = withFinder(TagFinder(open, close))

    @JvmStatic
    fun html(tag: String): Schematic = withFinder(HtmlTagFinder(tag))

    @JvmStatic
    fun htmlBold(): Schematic = html("b")

    @JvmStatic
    fun htmlUnderline(): Schematic = html("u")

    @JvmStatic
    fun htmlLink(): ClickSchematic = ClickSchematic().apply {
        addFinder(HtmlTagAttributeFinder("a", "href"))
    }

    @JvmStatic
    fun htmlFontColor(): Schematic = Schematic().apply {
        addFinder(HtmlTagAttributeFinder("font", "color"))
        addBit(object : Bit {
            override fun apply(paint: TextPaint, match: Match) {
                paint.color = Color.parseColor((match as ValueMatch).value)
            }
        })
    }
}

// endregion


// region Extensions

fun MonkeyWrench.allOfIt(setup: Schematic.() -> Unit) = addSchematic(Schematics.allOfIt(), setup)
fun MonkeyWrench.interval(vararg intervals: IntervalFinder.Interval, setup: Schematic.() -> Unit) = addSchematic(Schematics.interval(*intervals), setup)
fun MonkeyWrench.regex(regex: String, setup: Schematic.() -> Unit) = addSchematic(Schematics.regex(regex), setup)
fun MonkeyWrench.tag(open: CharSequence, close: CharSequence, setup: Schematic.() -> Unit) = addSchematic(Schematics.tag(open, close), setup)
fun MonkeyWrench.html(tag: String, setup: Schematic.() -> Unit) = addSchematic(Schematics.html(tag), setup)
fun MonkeyWrench.htmlBold(setup: Schematic.() -> Unit) = addSchematic(Schematics.htmlBold(), setup)
fun MonkeyWrench.htmlUnderline(setup: Schematic.() -> Unit) = addSchematic(Schematics.htmlUnderline(), setup)
fun MonkeyWrench.htmlLink(setup: ClickSchematic.() -> Unit) = addSchematic(Schematics.htmlLink(), setup)
fun MonkeyWrench.htmlFontColor() = addSchematic(Schematics.htmlFontColor())

fun MonkeyWrench.simpleHtml() {
    addSchematic(Schematics.htmlBold()) {
        fakeBold()
    }
    addSchematic(Schematics.htmlUnderline()) {
        underline()
    }
}

// endregion