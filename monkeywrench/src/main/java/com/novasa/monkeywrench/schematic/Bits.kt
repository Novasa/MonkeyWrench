@file:Suppress("unused")

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
    fun createTypeface(typeface: Typeface): Bit = TypefaceBit(typeface)

    @JvmStatic
    fun createTextColor(@ColorInt color: Int): Bit = TextColorBit(color)

    @JvmStatic
    fun createBackgroundColor(@ColorInt color: Int): Bit = BackgroundColorBit(color)

    @JvmStatic
    fun createScale(scale: Float): Bit = ScaleBit(scale)

    @JvmStatic
    fun createUnderline(): Bit = UnderlineBit()

    @JvmStatic
    fun createStrikeThrough(): Bit = StrikeThroughBit()

    @JvmStatic
    fun createFakeBold(): Bit = FakeBoldBit()
}

// endregion


// region Schematic Extensions

fun Schematic.addBitTypeface(typeface: Typeface) = addBit(Bits.createTypeface(typeface))
fun Schematic.addBitTextColor(@ColorInt color: Int) = addBit(Bits.createTextColor(color))
fun Schematic.addBitBackgroundColor(@ColorInt color: Int) = addBit(Bits.createBackgroundColor(color))
fun Schematic.addBitScale(scale: Float) = addBit(Bits.createScale(scale))
fun Schematic.addBitUnderline() = addBit(Bits.createUnderline())
fun Schematic.addBitStrikeThrough() = addBit(Bits.createStrikeThrough())
fun Schematic.addBitFakeBold() = addBit(Bits.createFakeBold())

// endregion
