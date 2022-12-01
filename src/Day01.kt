fun main() {
    fun splitOn(separator: String, input: List<String>): List<List<String>> {
        tailrec fun splitOn(acc: List<List<String>>, input: List<String>): List<List<String>> {
            val index = input.indexOf(separator)

            return if (index == -1) {
                acc.plusElement(input)
            } else {
                splitOn(
                    acc = acc.plusElement(input.slice(0 until index)),
                    input = input.slice((index + 1) until input.size)
                )
            }
        }

        return splitOn(emptyList(), input)
    }

    fun part1(input: List<String>): Long {
        val elfs = splitOn("", input)
        return elfs.map { elf -> elf.sumOf { it.toLong() } }.max()
    }

    fun part2(input: List<String>): Long {
        val elfs = splitOn("", input)
        return elfs.map { elf -> elf.sumOf { it.toLong() } }.asSequence().sortedDescending().take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000L)
    check(part2(testInput) == 45000L)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
