fun main() {
    fun score(char: Char): Long {
        return if (char.isLowerCase()) {
            char - 'a' + 1L
        } else {
            char - 'A' + 27L
        }
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { pack ->
            pack.slice(0 until pack.length / 2).toSet()
                .intersect(pack.slice(pack.length / 2 until pack.length).toSet())
                .sumOf { score(it) }
        }
    }

    fun part2(input: List<String>): Long {
        return input.windowed(3, 3)
            .sumOf { pack ->
                pack.map { it.toSet() }
                    .reduce { a, b -> a.intersect(b) }
                    .sumOf { score(it) }
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157L)
    check(part2(testInput) == 70L)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
