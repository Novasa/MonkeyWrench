package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic


class ValueMatch(schematic: Schematic, input: CharSequence, p0: Int, output: CharSequence, val value: String) : Match(schematic, input, p0, output)
