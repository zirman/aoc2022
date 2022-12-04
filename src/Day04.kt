fun main() {
    fun ranges(pair: String): List<IntRange> {
        return pair.split(',')
            .map {
                it.split('-')
                    .let { (start, end) -> start.toInt()..end.toInt() }
            }
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { pair ->
            ranges(pair)
                .let { (range1, range2) ->
                    val units = range1.intersect(range2).size
                    units == range1.last + 1 - range1.first ||
                            units == range2.last + 1 - range2.first
                }
                .let { if (it) 1L else 0L }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { pair ->
            ranges(pair)
                .let { (a, b) -> a.intersect(b).isNotEmpty() }
                .let { if (it) 1L else 0L }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2L)
    check(part2(testInput) == 4L)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
