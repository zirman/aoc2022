fun main() {
    fun part1(input: List<String>): Long {
        return input.size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 1L)
    check(part2(testInput) == 1L)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
