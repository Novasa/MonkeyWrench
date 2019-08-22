package com.novasa.monkeywrench.schematic

interface Mutater {
    fun apply(input: CharSequence): CharSequence
}