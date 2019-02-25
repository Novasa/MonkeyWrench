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
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.TextView
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

@Suppress("unused", "MemberVisibilityCanBePrivate")
class MonkeyWrench(val input: CharSequence) {

    companion object {
        private const val COLOR_UNDEFINED = 1

        fun span(input: CharSequence, setup: MonkeyWrench.() -> Unit): CharSequence {
            val instance = MonkeyWrench(input)
            setup(instance)
            return instance.doTheThing()
        }

        fun span(input: CharSequence, textView: TextView, setup: MonkeyWrench.() -> Unit) {
            val instance = MonkeyWrench(input)
            setup(instance)
            instance.doTheThingUnto(textView)
        }
    }

    private val builder = SpannableStringBuilder()

    private val spans = ArrayList<Wrench>()

    fun <T : Wrench> addWrench(span: T, setup: T.() -> Unit): MonkeyWrench {
        setup(span)
        spans.add(span)
        return this
    }

    fun tag(open: CharSequence, close: CharSequence, setup: Wrench.() -> Unit): MonkeyWrench {
        val wrench = Wrench(TagFinder(open, close))
        return addWrench(wrench, setup)
    }

    fun html(tag: String, setup: Wrench.() -> Unit): MonkeyWrench {
        return tag("<$tag>", "</$tag>", setup)
    }

    fun htmlBold(setup: Wrench.() -> Unit): MonkeyWrench {
        return html("b", setup)
    }

    fun htmlLink(onClick: (Uri) -> Unit, setup: ClickWrench.() -> Unit): MonkeyWrench {
        return addWrench(ClickWrench(onClick), setup)
    }

    fun interval(vararg intervals: Pair<Int, Int>, setup: Wrench.() -> Unit): MonkeyWrench {
        val wrench = Wrench(IntervalFinder(intervals))
        return addWrench(wrench, setup)
    }

    fun regex(regex: String, setup: Wrench.() -> Unit): MonkeyWrench {
        val wrench = Wrench(RegexFinder(regex))
        return addWrench(wrench, setup)
    }

    /**
     * Does the thing
     */
    fun doTheThing(): CharSequence {

        val intervals = ArrayList<Interval>()

        spans.forEach { span ->
            intervals.addAll(span.createIntervals(input))
        }

        intervals.sortWith(Comparator { o1, o2 ->
            if (o1.p0 < o2.p0) -1 else if (o1.p0 > o2.p0) 1 else 0
        })

        var end = 0

        for (interval in intervals) {

            // Start from end of previous interval, until before the opening tag of the interval
            val start = interval.p0 - interval.openLength

            // Append text before the interval with no span
            if (start > end) {
                // If start is after end, we have overlapping intervals
                builder.append(input.subSequence(end, start))
            }

            val p0 = builder.length

            // Append the interval
            builder.append(interval.sequence)

            val p1 = builder.length

            // Set the span
            builder.setSpan(interval.getSpan(), p0, p1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Save the position after the closing tag, so we can append the next piece of text before the next interval
            end = interval.p1 + interval.closeLength
        }

        // Append remaining text after the final interval, or everything if there were no intervals
        builder.append(input.subSequence(end, input.length))

        return builder
    }

    /**
     * Does the thing to the text view
     */
    fun doTheThingUnto(textView: TextView) {
        textView.text = doTheThing()
        spans.forEach { span ->
            span.setupTextView(textView)
        }
    }

    override fun toString(): String {
        return builder.toString()
    }

    open class Wrench(val finder: Finder) {

        protected var typeFace = Typeface.DEFAULT
        protected var color = COLOR_UNDEFINED
        protected var bgColor = COLOR_UNDEFINED
        protected var scale = 1f
        protected var underline = false

        fun typeFace(typeface: Typeface): Wrench {
            typeFace = typeface
            return this
        }

        fun color(color: Int): Wrench {
            this.color = color
            return this
        }

        fun backgroundColor(color: Int): Wrench {
            this.bgColor = color
            return this
        }

        fun scale(scale: Float): Wrench {
            this.scale = scale
            return this
        }

        fun underline(): Wrench {
            this.underline = true
            return this
        }

        open fun getSpan(): CharacterStyle {
            return CustomTypefaceSpan(this)
        }

        internal fun createIntervals(input: CharSequence): List<Interval> {
            return finder.createIntervals(input, this)
        }

        open fun apply(paint: Paint) {

            paint.typeface = typeFace

            if (color != COLOR_UNDEFINED) {
                paint.color = color
            }

            if (paint is TextPaint && bgColor != COLOR_UNDEFINED) {
                paint.bgColor = bgColor
            }

            if (scale != 1f) {
                paint.textSize = paint.textSize * scale
            }

            paint.isUnderlineText = underline
        }

        open fun setupTextView(textView: TextView) {

        }
    }

    class ClickWrench(val onClick: (Uri) -> Unit) : Wrench(HrefFinder()) {
        override fun setupTextView(textView: TextView) {
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    abstract class Finder {
        open val openLength: Int = 0
        open val closeLength: Int = 0

        abstract fun createIntervals(input: CharSequence, wrench: Wrench): List<Interval>
    }

    open class RegexFinder(protected val regex: String): Finder() {

        open var outputGroupIndex = 0

        override fun createIntervals(input: CharSequence, wrench: Wrench): List<Interval> {
            val result = ArrayList<Interval>()

            val matcher = Pattern.compile(regex).matcher(input)
            while (matcher.find()) {
                val interval = onMatch(wrench, matcher)
                result.add(interval)
            }

            return result
        }

        fun onMatch(wrench: Wrench, matcher: Matcher): Interval {
            val sequence = matcher.group(outputGroupIndex)
            val p0 = matcher.start(outputGroupIndex)
            val p1 = matcher.end(outputGroupIndex)

            return onMatch(wrench, matcher, sequence, p0, p1)
        }

        open fun onMatch(wrench: Wrench, matcher: Matcher, sequence: String, p0: Int, p1: Int): Interval {
            return Interval(wrench, sequence, p0, p1)
        }
    }

    open class TagFinder(open: CharSequence, close: CharSequence) : RegexFinder("$open(.*?)$close") {
        override val openLength: Int = open.length
        override val closeLength: Int = close.length
        override var outputGroupIndex: Int = 1
    }

    class HrefFinder : TagFinder("<a href=(.*?)>", "</a>") {

        override var outputGroupIndex: Int = 2

        override fun onMatch(wrench: Wrench, matcher: Matcher, sequence: String, p0: Int, p1: Int): Interval {
            val href = matcher.group(1)
            return HrefInterval(wrench as ClickWrench, sequence, p0, p1, href)
        }
    }

    open class IntervalFinder(protected val intervals: Array<out Pair<Int, Int>>): Finder() {

        override fun createIntervals(input: CharSequence, wrench: Wrench): List<Interval> {
            val result = ArrayList<Interval>(intervals.size)

            intervals.forEach { interval ->
                val p0 = interval.first
                val p1 = interval.second
                if (p0 < 0 || p0 > p1 || p1 > input.length) {
                    throw IllegalArgumentException("Bad interval: $p0 - $p1. Length was ${input.length}.")
                }

                val sequence = input.subSequence(p0, p1)
                result.add(Interval(wrench, sequence, p0, p1))
            }

            return result
        }
    }

    open class Interval internal constructor(val wrench: Wrench, val sequence: CharSequence, val p0: Int, val p1: Int) {
        open val openLength: Int = wrench.finder.openLength
        open val closeLength: Int = wrench.finder.closeLength

        open fun getSpan(): CharacterStyle = wrench.getSpan()

        override fun toString(): String = "$p0 - $p1 (o: $openLength, c: $closeLength)"
    }

    internal class HrefInterval(wrench: ClickWrench, sequence: CharSequence, p0: Int, p1: Int, hrefString: String) : Interval(wrench, sequence, p0, p1) {
        override val openLength: Int = "<a href=$hrefString>".length
        private val uri: Uri = Uri.parse(hrefString.trim('"', '\''))

        override fun getSpan(): CharacterStyle = ClickSpan(uri, wrench as ClickWrench)

        override fun toString(): String = "${super.toString()} - $uri"
    }

    private class CustomTypefaceSpan internal constructor(private val wrench: Wrench) : TypefaceSpan("") {

        override fun updateDrawState(ds: TextPaint) {
            wrench.apply(ds)
        }

        override fun updateMeasureState(paint: TextPaint) {
            wrench.apply(paint)
        }
    }

    private class ClickSpan(val uri: Uri, val wrench: ClickWrench) : ClickableSpan() {

        override fun onClick(widget: View) {
            wrench.onClick(uri)
        }

        override fun updateDrawState(ds: TextPaint) {
            wrench.apply(ds)
        }
    }
}