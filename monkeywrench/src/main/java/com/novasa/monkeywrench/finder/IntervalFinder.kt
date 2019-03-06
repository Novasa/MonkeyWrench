package com.novasa.monkeywrench.finder

import com.novasa.monkeywrench.schematic.Schematic


open class IntervalFinder(protected val intervals: Array<out Interval>) : Finder() {

    override fun findMatches(input: CharSequence, schematic: Schematic): List<Match> {
        val result = ArrayList<Match>(intervals.size)

        intervals.forEach { interval ->
            val p0 = interval.i0
            val p1 = interval.i1
            if (p0 < 0 || p0 > p1 || p1 > input.length) {
                throw IllegalArgumentException("Bad interval: $p0 - $p1. Length was ${input.length}.")
            }

            val sequence = input.subSequence(p0, p1)
            result.add(Match(schematic, this, sequence, p0, p1))
        }

        return result
    }

    data class Interval(val i0: Int, val i1: Int)
}