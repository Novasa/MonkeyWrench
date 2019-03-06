package com.novasa.monkeywrench.schematic

import android.net.Uri
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import com.novasa.monkeywrench.MonkeyWrench
import com.novasa.monkeywrench.finder.HrefFinder
import com.novasa.monkeywrench.finder.HrefMatch
import com.novasa.monkeywrench.finder.Match
import com.novasa.monkeywrench.span.ClickSpan

class ClickSchematic : Schematic(HrefFinder()) {

    private var onClickEvent: ((Uri) -> Unit)? = null

    override fun createSpan(match: Match): Any? {
        return ClickSpan(match, (match as HrefMatch).uri)
    }

    fun onClick(onClick: (Uri) -> Unit): ClickSchematic {
        this.onClickEvent = onClick
        return this
    }

    internal fun onClick(uri: Uri) {
        onClickEvent?.let {
            it(uri)
        } ?: Log.w(MonkeyWrench.TAG, "Click Wrench was poked, but no handler has been supplied. Do the onClick() thing in the setup please.")
    }

    override fun setupTextView(textView: TextView) {
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}