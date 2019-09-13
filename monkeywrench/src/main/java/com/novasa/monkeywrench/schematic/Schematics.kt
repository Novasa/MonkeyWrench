@file:Suppress("unused")

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

    private var onClickEvent: ((Uri) -> Unit)? = null

    override fun createSpan(match: Match): Any? {
        val uri: Uri = Uri.parse(
            if (match is ValueMatch) match.value
            else match.output.toString()
        )

        return ClickSpan(match, uri)
    }

    fun onClick(onClick: (Uri) -> Unit): ClickSchematic {
        this.onClickEvent = onClick
        return this
    }

    internal fun onClick(uri: Uri) {
        onClickEvent?.let {
            it(uri)
        } ?: Log.w(
            MonkeyWrench.TAG,
            "Click Wrench was poked, but no handler has been supplied. Do the onClick() thing in the setup please."
        )
    }

    override fun setupTextView(textView: TextView) {
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

// endregion


// region JVM Statics

object Schematics {

    @JvmStatic
    fun create(): Schematic = Schematic()

    @JvmStatic
    fun createClickable(): ClickSchematic = ClickSchematic()

    @JvmStatic
    fun createHtmlALink(): ClickSchematic = createClickable().apply {
        addFinder(Finders.createHtmlTagAttribute("a", "href"))
    }

    @JvmStatic
    fun createHttpLink(): ClickSchematic = createClickable().apply {
        addFinder(Finders.createHttpLink())
    }

    @JvmStatic
    fun createHtmlFontColor(): Schematic = create().apply {
        addFinder(HtmlTagAttributeFinder("font", "color"))
        addBit(object : Bit {
            override fun apply(paint: TextPaint, match: Match) {
                paint.color = Color.parseColor((match as ValueMatch).value)
            }
        })
    }
}

// endregion


// region MonkeyWrench Extensions

fun MonkeyWrench.addSchematicClickable(setup: ClickSchematic.() -> Unit) =
    addSchematic(Schematics.createClickable(), setup)

fun MonkeyWrench.addSchematicHtmlALink(setup: ClickSchematic.() -> Unit) =
    addSchematic(Schematics.createHtmlALink(), setup)

fun MonkeyWrench.addSchematicHttpLink(setup: ClickSchematic.() -> Unit) =
    addSchematic(Schematics.createHttpLink(), setup)

fun MonkeyWrench.addSchematicHtmlFontColor() = addSchematic(Schematics.createHtmlFontColor())

fun MonkeyWrench.simpleHtml() {
    addSchematic {
        addFinderHtmlBold()
        addBitFakeBold()
    }
    addSchematic {
        addFinderHtmlUnderline()
        addBitUnderline()
    }

    addSchematicHtmlFontColor()
}

// endregion
