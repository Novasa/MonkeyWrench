package com.novasa.monkeywrench.schematic

interface Mutater {
    fun apply(sequence: CharSequence): CharSequence
}