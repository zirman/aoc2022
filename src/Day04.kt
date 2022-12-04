fun main() {
    fun ranges(pair: String): List<LongRange> {
        return pair.split(',')
            .map {
                it.split('-')
                    .let { (a, b) -> (a.toLong()..b.toLong()) }
            }
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { pair ->
            ranges(pair)
                .let { (a, b) ->
                    val s = a.intersect(b).size.toLong()
                    s == a.last - a.first + 1 || s == b.last - b.first + 1
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
