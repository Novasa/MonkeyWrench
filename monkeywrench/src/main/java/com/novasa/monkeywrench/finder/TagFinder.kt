package com.novasa.monkeywrench.finder


/** Use for simple open/close tag matches, like html tags. */
open class TagFinder(open: CharSequence, close: CharSequence) : RegexFinder("$open(.*?)$close") {
    override val openLength: Int = open.length
    override val closeLength: Int = close.length
    override var outputGroupIndex: Int = 1
}