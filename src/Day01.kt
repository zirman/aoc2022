fun main() {
    fun part1(input: List<String>): Long {
        val food = mutableMapOf<Int, Long>()

        var i = 0
        for (k in input) {
            if (k.isNotEmpty()) {
                food[i] = food.getOrDefault(i, 0) + k.toLong()
            } else {
                i++
            }
        }

        return food.values.max()
    }

    fun part2(input: List<String>): Long {
        val food = mutableMapOf<Int, Long>()

        var i = 0
        for (k in input) {
            if (k.isNotEmpty()) {
                food[i] = food.getOrDefault(i, 0) + k.toLong()
            } else {
                i++
            }
        }

        return food.values.asSequence().sortedDescending().take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000L)
    check(part2(testInput) == 45000L)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
