fun main() {
    fun part1(input: List<String>): String {
        val (stacksInput, movesInput) = input.joinToString("\n").split("\n\n")
        val stacks = stacksInput.split("\n").map { it.map { it } }
        val cs = stacks.last().mapIndexedNotNull { i, c -> if (c.isDigit()) i else null }

        fun parseStack(c: Int): List<Char> {
            fun parseColumn(r: Int): List<Char> {

                return if (stacks.indices.contains(r).not() ||
                    stacks[r].indices.contains(c).not() ||
                    stacks[r][c].isLetter().not()
                ) {
                    emptyList()
                } else {
                    listOf(stacks[r][c]).plus(parseColumn(r - 1))
                }
            }

            return parseColumn(stacks.size - 2)
        }

        val parsedStacks = cs.map { parseStack(it) }.toTypedArray()

        val parsedMoves = movesInput
            .split("\n")
            .map {
                val (countStr, moveStr) = it.substringAfter("move ").split(" from ")
                val (fromStr, toStr) = moveStr.split(" to ")
                Triple(countStr.toInt(), fromStr.toInt() - 1, toStr.toInt() - 1)
            }

        parsedMoves.forEach { (count, from, to) ->
            parsedStacks[to] = parsedStacks[to].plus(parsedStacks[from].takeLast(count).reversed())
            parsedStacks[from] = parsedStacks[from].take(parsedStacks[from].size - count)
        }

        return parsedStacks.joinToString("") { "${it.last()}" }
    }

    fun part2(input: List<String>): String {
        val (stacksInput, movesInput) = input.joinToString("\n").split("\n\n")
        val stacks = stacksInput.split("\n").map { it.map { it } }
        val cs = stacks.last().mapIndexedNotNull { i, c -> if (c.isDigit()) i else null }

        fun parseStack(c: Int): List<Char> {
            fun parseColumn(r: Int): List<Char> {

                return if (stacks.indices.contains(r).not() ||
                    stacks[r].indices.contains(c).not() ||
                    stacks[r][c].isLetter().not()
                ) {
                    emptyList()
                } else {
                    listOf(stacks[r][c]).plus(parseColumn(r - 1))
                }
            }

            return parseColumn(stacks.size - 2)
        }

        val parsedStacks = cs.map { parseStack(it) }.toTypedArray()

        val parsedMoves = movesInput
            .split("\n")
            .map {
                val (countStr, moveStr) = it.substringAfter("move ").split(" from ")
                val (fromStr, toStr) = moveStr.split(" to ")
                Triple(countStr.toInt(), fromStr.toInt() - 1, toStr.toInt() - 1)
            }

        parsedMoves.forEach { (count, from, to) ->
            parsedStacks[to] = parsedStacks[to].plus(parsedStacks[from].takeLast(count))
            parsedStacks[from] = parsedStacks[from].take(parsedStacks[from].size - count)
        }

        return parsedStacks.joinToString("") { "${it.last()}" }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")

    val input = readInput("Day05")
    println(part1(input))

    check(part2(testInput) == "MCD")
    println(part2(input))
}
