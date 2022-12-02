fun main() {
    fun gameScore(me: Long, them: Long): Long {
        return me + 1 + when (me) {
            them -> 3
            (them + 1) % 3 -> 6
            else -> 0
        }
    }

    fun part1(input: List<String>): Long {
        return input.sumOf {
            val round = it.split(" ")
            val them = (round[0][0] - 'A').toLong()
            val me = (round[1][0] - 'X').toLong()
            gameScore(me, them)
        }
    }


    fun part2(input: List<String>): Long {
        fun move(me: Long, them: Long): Long {
            return when (me) {
                0L -> (them + 2) % 3
                1L -> them
                else -> (them + 1) % 3
            }
        }

        return input.sumOf {
            val round = it.split(" ")
            val them = (round[0][0] - 'A').toLong()
            val me = (round[1][0] - 'X').toLong()
            gameScore(move(me, them), them)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15L)
    check(part2(testInput) == 12L)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
