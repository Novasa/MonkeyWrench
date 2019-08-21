package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic

/** A match that represents the entire input */
class GlobalMatch(schematic: Schematic, input: CharSequence) : Match(schematic, input, 0, input)
