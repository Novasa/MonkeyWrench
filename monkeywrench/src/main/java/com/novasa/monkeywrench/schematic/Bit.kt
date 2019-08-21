package com.novasa.monkeywrench.schematic

import android.text.TextPaint
import com.novasa.monkeywrench.finder.Match

interface Bit {
    fun apply(paint: TextPaint, match: Match)
}