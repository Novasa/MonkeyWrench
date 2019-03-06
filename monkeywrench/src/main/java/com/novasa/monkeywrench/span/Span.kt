package com.novasa.monkeywrench.span

import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import com.novasa.monkeywrench.finder.Match


open class Span(val match: Match) : MetricAffectingSpan() {

    override fun updateDrawState(ds: TextPaint) {
        match.schematic.apply(ds, match.sequence)
    }

    override fun updateMeasureState(paint: TextPaint) {
        match.schematic.apply(paint, match.sequence)
    }
}