package com.novasa.monkeywrench.schematic

import android.graphics.Paint

interface Painter {
    fun apply(paint: Paint, sequence: CharSequence)
}