package com.novasa.monkeywrench

import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.MetricAffectingSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
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
        fun interval(vararg intervals: Interval): Schematic = Schematic(IntervalFinder(intervals))

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
    fun interval(vararg intervals: Pair<Int, Int>, setup: Schematic.() -> Unit) = schematic(interval(*(intervals.map { Interval(it.first, it.second) }).toTypedArray()), setup)
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

            // Start from end of previous match, until before the opening tag of the match
            val start = match.p0 - match.openLength

            // Append text before the match with no span
            if (start > end) {
                // If start is after end, we have overlapping matches
                builder.append(input.subSequence(end, start))
            }

            val p0 = builder.length

            // Append the sequence
            builder.append(match.sequence)

            val p1 = builder.length

            // Set the span
            builder.setSpan(match.getSpan(), p0, p1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

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

    open class Schematic(val finders: List<Finder>) {

        constructor(vararg finders: Finder) : this(finders.asList())

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

        open fun getSpan(): CharacterStyle {
            return Span(this)
        }

        internal fun findMatches(input: CharSequence): List<Match> = finders.flatMap {
            it.findMatches(input, this)
        }

        open fun apply(paint: Paint) {

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
    }

    class ClickSchematic : Schematic(HrefFinder()) {

        private var onClickEvent: ((Uri) -> Unit)? = null

        fun onClick(onClick: (Uri) -> Unit): ClickSchematic {
            this.onClickEvent = onClick
            return this
        }

        internal fun onClick(uri: Uri) {
            onClickEvent?.let {
                it(uri)
            } ?: Log.w(TAG, "Click Wrench was poked, but no handler has been supplied. Do the onClick() thing in the setup please.")
        }

        override fun setupTextView(textView: TextView) {
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    /** Finds [Match]es in the input, that the [Schematic] is applied to. */
    abstract class Finder {

        /** Must return the length of any opening tag that is not included in the output. Example: <b> = 3 */
        open val openLength: Int = 0

        /** Must return the length of any closing tag that is not included in the output. Example: </b> = 4 */
        open val closeLength: Int = 0

        abstract fun findMatches(input: CharSequence, schematic: Schematic): List<Match>
    }

    class GlobalFinder : Finder() {
        override fun findMatches(input: CharSequence, schematic: Schematic): List<Match> {
            return listOf(Match(schematic, this, input.subSequence(0, input.length), 0, input.length))
        }
    }

    open class RegexFinder(protected val regex: String) : Finder() {

        /** If the regex includes groups, this is the group index of the desired output. Default is 0, which is the entire regex match. */
        open var outputGroupIndex = 0

        override fun findMatches(input: CharSequence, schematic: Schematic): List<Match> {
            val result = ArrayList<Match>()

            val matcher = Pattern.compile(regex).matcher(input)
            while (matcher.find()) {
                val interval = onMatch(schematic, matcher)
                result.add(interval)
            }

            return result
        }

        fun onMatch(schematic: Schematic, matcher: Matcher): Match {
            val sequence = matcher.group(outputGroupIndex)
            val p0 = matcher.start(outputGroupIndex)
            val p1 = matcher.end(outputGroupIndex)

            return onMatch(schematic, matcher, sequence, p0, p1)
        }

        open fun onMatch(schematic: Schematic, matcher: Matcher, sequence: String, p0: Int, p1: Int): Match {
            return Match(schematic, this, sequence, p0, p1)
        }
    }

    /** Use for simple open/close tag matches, like html tags. */
    open class TagFinder(open: CharSequence, close: CharSequence) : RegexFinder("$open(.*?)$close") {
        override val openLength: Int = open.length
        override val closeLength: Int = close.length
        override var outputGroupIndex: Int = 1
    }

    class HrefFinder : TagFinder("<a href=(.*?)>", "</a>") {

        override var outputGroupIndex: Int = 2

        override fun onMatch(schematic: Schematic, matcher: Matcher, sequence: String, p0: Int, p1: Int): Match {
            val href = matcher.group(1)
            return HrefMatch(schematic as ClickSchematic, this, sequence, p0, p1, href)
        }
    }

    open class IntervalFinder(protected val intervals: Array<out Interval>) : Finder() {

        override fun findMatches(input: CharSequence, schematic: Schematic): List<Match> {
            val result = ArrayList<Match>(intervals.size)

            intervals.forEach { interval ->
                val p0 = interval.i0
                val p1 = interval.i1
                if (p0 < 0 || p0 > p1 || p1 > input.length) {
                    throw IllegalArgumentException("Bad interval: $p0 - $p1. Length was ${input.length}.")
                }

                val sequence = input.subSequence(p0, p1)
                result.add(Match(schematic, this, sequence, p0, p1))
            }

            return result
        }
    }

    /**
     * Represents a match in the input, found by a [Finder].
     */
    open class Match(val schematic: Schematic, finder: Finder, val sequence: CharSequence, val p0: Int, val p1: Int) {
        open val openLength: Int = finder.openLength
        open val closeLength: Int = finder.closeLength

        /** Must return a [CharacterStyle] that calls [Match.schematic]'s [Schematic.apply] in the [CharacterStyle.updateDrawState] method */
        open fun getSpan(): CharacterStyle = schematic.getSpan()

        override fun toString(): String = "$p0 - $p1 (o: $openLength, c: $closeLength)"
    }

    class HrefMatch(schematic: ClickSchematic, finder: Finder, sequence: CharSequence, p0: Int, p1: Int, hrefString: String) : Match(schematic, finder, sequence, p0, p1) {
        override val openLength: Int = "<a href=$hrefString>".length
        private val uri: Uri = Uri.parse(hrefString.trim('"', '\''))

        override fun getSpan(): CharacterStyle = ClickSpan(uri, schematic as ClickSchematic)

        override fun toString(): String = "${super.toString()} - $uri"
    }

    open class Span(val schematic: Schematic) : MetricAffectingSpan() {

        override fun updateDrawState(ds: TextPaint) {
            schematic.apply(ds)
        }

        override fun updateMeasureState(paint: TextPaint) {
            schematic.apply(paint)
        }
    }

    open class ClickSpan(val uri: Uri, val schematic: ClickSchematic) : ClickableSpan() {

        override fun onClick(widget: View) {
            schematic.onClick(uri)
        }

        override fun updateDrawState(ds: TextPaint) {
            schematic.apply(ds)
        }
    }

    data class Interval(val i0: Int, val i1: Int) {
        infix fun Int.to(that: Int): Interval = Interval(this, that)
    }
}