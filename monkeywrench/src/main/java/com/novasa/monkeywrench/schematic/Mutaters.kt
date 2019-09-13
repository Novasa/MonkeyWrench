@file:Suppress("unused")

package com.novasa.monkeywrench.schematic

import java.util.*


// region Mutaters

/** Deletes everything found by the matcher */
class DeleteMutater : Mutater {
    override fun apply(input: CharSequence): CharSequence = ""
}

class UpperCaseMutater: Mutater {
    override fun apply(input: CharSequence): CharSequence = input.toString().toUpperCase(Locale.getDefault())
}

class LowerCaseMutater: Mutater {
    override fun apply(input: CharSequence): CharSequence = input.toString().toLowerCase(Locale.getDefault())
}

// endregion


// region JVM Statics

object Mutaters {

    @JvmStatic
    fun createDelete(): Mutater = DeleteMutater()

    @JvmStatic
    fun createUpperCase(): Mutater = UpperCaseMutater()

    @JvmStatic
    fun createLowerCase(): Mutater = LowerCaseMutater()
}

// endregion


// region Schematic Extensions

fun Schematic.addMutaterDelete() {
    addMutater(Mutaters.createDelete())
}

fun Schematic.addMutaterUpperCase() {
    addMutater(Mutaters.createUpperCase())
}

fun Schematic.addMutaterLowerCase() {
    addMutater(Mutaters.createLowerCase())
}

// endregion
