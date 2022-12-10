fun main() {
    fun part1(input: List<String>): Int {
        return input
            .scan(listOf(1)) { signals, line ->
                val x = signals.last()
                val s = line.split(" ")

                when (s[0]) {
                    "addx" -> listOf(x, x + s[1].toInt())
                    "noop" -> listOf(x)
                    else -> throw Exception()
                }
            }
            .flatten()
            .mapIndexedNotNull { index, x ->
                when (index + 1) {
                    20, 60, 100, 140, 180, 220 -> x * (index + 1)
                    else -> null
                }
            }
            .sum()
    }

    fun part2(input: List<String>): String {
        return input
            .scan(listOf(1)) { signals, line ->
                val x = signals.last()
                val s = line.split(" ")

                when (s[0]) {
                    "addx" -> listOf(x, x + s[1].toInt())
                    "noop" -> listOf(x)
                    else -> throw Exception()
                }
            }
            .flatten()
            .mapIndexed { index, x -> if (x in (index % 40) - 1..(index % 40) + 1) "#" else "." }
            .windowed(40, 40, false)
            .map { line -> line.joinToString("") { it } }
            .joinToString("\n") { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    val input = readInput("Day10")
    println(part1(input))

    check(
        part2(testInput) == """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
        """.trimIndent()
    )

    println(part2(input))
}
