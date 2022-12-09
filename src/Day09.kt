import kotlin.math.absoluteValue

data class Knot(val row: Int, val col: Int)

fun main() {
    fun iterateTailKnot(h: Knot, t: Knot): Knot {
        val (hr, hc) = h
        var (tr, tc) = t
        val dr = hr - tr
        val dc = hc - tc
        val m = dr.absoluteValue + dc.absoluteValue

        if ((m == 1 || (m == 2 && dc.absoluteValue == 1 && dr.absoluteValue == 1)).not()) {
            if (dc > 0) {
                tc += 1
            } else if (dc < 0) {
                tc -= 1
            }

            if (dr > 0) {
                tr += 1
            } else if (dr < 0) {
                tr -= 1
            }
        }

        return Knot(tr, tc)
    }

    fun part1(input: List<String>): Int {
        val visited = mutableSetOf<Knot>()
        var headKnot = Knot(0, 0)
        var tailKnot = Knot(0, 0)
        visited.add(Knot(0, 0))

        input.forEach { line ->
            val (dir, numStr) = line.split(" ")
            val n = numStr.toInt()

            fun iterate(n: Int, knot: Knot) {
                repeat(n) {
                    headKnot = knot
                    tailKnot = iterateTailKnot(headKnot, tailKnot)
                    visited.add(tailKnot)
                }
            }

            when (dir) {
                "L" -> {
                    iterate(n, headKnot.copy(col = headKnot.col - 1))
                }

                "R" -> {
                    iterate(n, headKnot.copy(col = headKnot.col + 1))
                }

                "D" -> {
                    iterate(n, headKnot.copy(row = headKnot.row + 1))
                }

                "U" -> {
                    iterate(n, headKnot.copy(row = headKnot.row - 1))
                }
            }
        }

        return visited.size
    }

    fun part2(input: List<String>): Int {
        val visited = mutableSetOf<Knot>()
        var knots = (1..10).map { Knot(0, 0) }
        visited.add(Knot(0, 0))

        fun iterateTailKnots(n: Int, knot: Knot) {
            repeat(n) {
                knots = buildList {
                    var tailKnot = knot
                    add(tailKnot)

                    for (i in 1 until knots.size) {
                        tailKnot = iterateTailKnot(tailKnot, knots[i])
                        add(tailKnot)
                    }
                }
            }

            visited.add(knots.last())
        }

        input.forEach { line ->
            val (dir, numstr) = line.split(" ")
            val n = numstr.toInt()

            when (dir) {
                "L" -> {
                    iterateTailKnots(n, knots[0].copy(col = knots[0].col - 1))
                }

                "R" -> {
                    iterateTailKnots(n, knots[0].copy(row = knots[0].col + 1))
                }

                "D" -> {
                    iterateTailKnots(n, knots[0].copy(row = knots[0].row + 1))
                }

                "U" -> {
                    iterateTailKnots(n, knots[0].copy(row = knots[0].row - 1))
                }
            }
        }

        return visited.size
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day09_test")) == 13)
    val input = readInput("Day09")
    println(part1(input))

    check(part2(readInput("Day09_test2")) == 36)
    println(part2(input))
}
