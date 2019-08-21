package com.novasa.monkeywrench.schematic

import android.graphics.Typeface
import android.text.TextPaint
import androidx.annotation.ColorInt
import com.novasa.monkeywrench.finder.Match

// region Bits

class TypefaceBit(private val typeface: Typeface) : Bit {
    override fun apply(paint: TextPaint, match: Match) {
        paint.typeface = typeface
    }
}

class TextColorBit(@ColorInt private val color: Int) : Bit {
    override fun apply(paint: TextPaint, match: Match) {
        paint.color = color
    }
}

class BackgroundColorBit(@ColorInt private val color: Int) : Bit {
    override fun apply(paint: TextPaint, match: Match) {
        paint.bgColor = color
    }
}

class ScaleBit(private val scale: Float) : Bit {
    override fun apply(paint: TextPaint, match: Match) {
        paint.textSize *= scale
    }
}

class UnderlineBit : Bit {
    override fun apply(paint: TextPaint, match: Match) {
        paint.isUnderlineText = true
    }
}

class StrikeThroughBit : Bit {
    override fun apply(paint: TextPaint, match: Match) {
        paint.isStrikeThruText = true
    }
}

class FakeBoldBit : Bit {
    override fun apply(paint: TextPaint, match: Match) {
        paint.isFakeBoldText = true
    }
}

// endregion


// region JVM Statics

object Bits {

    @JvmStatic
    fun typeface(typeface: Typeface): Bit = TypefaceBit(typeface)

    @JvmStatic
    fun textColor(@ColorInt color: Int): Bit = TextColorBit(color)

    @JvmStatic
    fun backgroundColor(@ColorInt color: Int): Bit = BackgroundColorBit(color)

    @JvmStatic
    fun scale(scale: Float): Bit = ScaleBit(scale)

    @JvmStatic
    fun underline(): Bit = UnderlineBit()

    @JvmStatic
    fun strikeThrough(): Bit = StrikeThroughBit()

    @JvmStatic
    fun fakeBold(): Bit = FakeBoldBit()
}

// endregion


// region Schematic Extensions

fun Schematic.typeface(typeface: Typeface) = addBit(Bits.typeface(typeface))
fun Schematic.textColor(@ColorInt color: Int) = addBit(Bits.textColor(color))
fun Schematic.backgroundColor(@ColorInt color: Int) = addBit(Bits.backgroundColor(color))
fun Schematic.scale(scale: Float) = addBit(Bits.scale(scale))
fun Schematic.underline() = addBit(Bits.underline())
fun Schematic.strikeThrough() = addBit(Bits.strikeThrough())
fun Schematic.fakeBold() = addBit(Bits.fakeBold())

// endregion