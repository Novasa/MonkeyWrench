package com.novasa.monkeywrench.span

import android.net.Uri
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.novasa.monkeywrench.finder.Match
import com.novasa.monkeywrench.schematic.ClickSchematic

open class ClickSpan(val match: Match, val uri: Uri?) : ClickableSpan() {

    override fun onClick(widget: View) {
        (match.schematic as ClickSchematic).onClick(uri)
    }

    override fun updateDrawState(ds: TextPaint) {
        match.schematic.apply(ds, match)
    }
}
