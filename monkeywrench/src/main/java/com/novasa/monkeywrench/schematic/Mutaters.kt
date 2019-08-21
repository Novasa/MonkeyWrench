package com.novasa.monkeywrench.schematic


// region Mutaters

/** Deletes everything found by the matcher */
class DeleteMutater : Mutater {
    override fun apply(sequence: CharSequence): CharSequence {
        return ""
    }
}

// endregion


// region JVM Statics

object Mutaters {

    @JvmStatic
    fun delete(): Mutater = DeleteMutater()
}

// endregion


// region Schematic Extensions

fun Schematic.delete() {
    addMutater(Mutaters.delete())
}

// endregion
