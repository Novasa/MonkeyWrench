package com.novasa.monkeywrench.finder

import android.net.Uri
import com.novasa.monkeywrench.schematic.ClickSchematic


class HrefMatch(schematic: ClickSchematic, finder: Finder, sequence: CharSequence, p0: Int, p1: Int, hrefString: String) : Match(schematic, finder, sequence, p0, p1) {
    override val openLength: Int = "<a href=$hrefString>".length
    val uri: Uri = Uri.parse(hrefString.trim('"', '\''))

    override fun toString(): String = "${super.toString()} - $uri"
}