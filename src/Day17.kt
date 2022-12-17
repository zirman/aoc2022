data class Pos(val x: Int, val y: Int)

fun main() {
    val width = 7

    operator fun List<List<Boolean>>.get(x: Int, y: Int): Boolean {
        return when {
            (x in 0 until width).not() || y < 0 -> true
            y >= this.size -> false
            else -> this[y][x]
        }
    }

    operator fun MutableList<MutableList<Boolean>>.set(x: Int, y: Int, b: Boolean) {
        if (y >= size) {
            addAll(
                List(y - size + 1) {
                    MutableList(width) { false }
                }
            )
        }

        this[y][x] = b
    }

    fun part1(input: List<String>, totalRocks: Int): Int {
        val rocks = mutableListOf<MutableList<Boolean>>()

        fun List<List<Boolean>>.prettyPrint(): String {
            return asReversed().joinToString(
                separator = "\n",
                prefix = "+-------+\n",
                postfix = "\n+-------+",
            ) { line ->
                line.joinToString(
                    separator = "",
                    prefix = "|",
                    postfix = "|",
                ) { if (it) "#" else " " }
            }
        }

        fun String.parseRock(): List<List<Boolean>> {
            return trimMargin().split("\n").asReversed().map { line -> line.map { it == '@' } }
        }

        val minus = """
            |@@@@""".parseRock()

        val plus = """
            | @
            |@@@
            | @""".parseRock()

        val ell = """
            |  @
            |  @
            |@@@""".parseRock()

        val aye = """
            |@
            |@
            |@
            |@""".parseRock()

        val sqr = """
            |@@
            |@@""".parseRock()

        fun List<List<Boolean>>.newRockLocation(): Pos {
            return Pos(2, this.size + 3)
        }

        fun List<List<Boolean>>.checkCollision(rock: List<List<Boolean>>, pos: Pos): Boolean {
            return rock.indices.any { ry ->
                rock[ry].indices.any { rx ->
                    rock[ry][rx] && this[pos.x + rx, pos.y + ry]
                }
            }
        }

        fun MutableList<MutableList<Boolean>>.placeRock(rock: List<List<Boolean>>, pos: Pos) {
            rock.indices.forEach { ry ->
                rock[ry].indices.forEach { rx ->
                    this[pos.x + rx, pos.y + ry] = rock[ry][rx]
                }
            }
        }

        val moveIter = sequence {
            while (true) {
                yieldAll(input[0].toList())
            }
        }.iterator()

        sequence {
            while (true) {
                yieldAll(listOf(minus, plus, ell, aye, sqr))
            }
        }.take(totalRocks).forEach { rock ->
            var pos = rocks.newRockLocation()

            while (true) {
                when (moveIter.next()) {
                    '>' -> {
                        if (rocks.checkCollision(rock, pos.copy(x = pos.x + 1)).not()) {
                            pos = pos.copy(x = pos.x + 1)
                        }
                    }

                    '<' -> {

                        if (rocks.checkCollision(rock, pos.copy(x = pos.x - 1)).not()) {
                            pos = pos.copy(x = pos.x - 1)
                        }
                    }

                    else -> throw Exception()
                }

                if (rocks.checkCollision(rock, pos.copy(y = pos.y - 1))) {
                    rocks.placeRock(rock, pos)
                    break
                }

                pos = pos.copy(y = pos.y - 1)
            }

//            println(rocks.prettyPrint())
        }

        return rocks.size
    }

    fun part2(input: List<String>, totalRocks: Long): Long {
        val rocks = mutableListOf<MutableList<Boolean>>()

        fun List<List<Boolean>>.prettyPrintImprint(): String {
            return joinToString(
                separator = "\n",
                prefix = "+-------+\n",
                postfix = "\n+-------+",
            ) { line ->
                line.joinToString(
                    separator = "",
                    prefix = "|",
                    postfix = "|",
                ) { if (it) "#" else " " }
            }
        }

        fun List<List<Boolean>>.prettyPrintRocks(): String {
            return asReversed().joinToString(
                separator = "\n",
                prefix = "+-------+\n",
                postfix = "\n+-------+",
            ) { line ->
                line.joinToString(
                    separator = "",
                    prefix = "|",
                    postfix = "|",
                ) { if (it) "#" else " " }
            }
        }

        fun String.parseRock(): List<List<Boolean>> {
            return trimMargin().split("\n").asReversed().map { line -> line.map { it == '@' } }
        }

        val minus = """
            |@@@@""".parseRock()

        val plus = """
            | @
            |@@@
            | @""".parseRock()

        val ell = """
            |  @
            |  @
            |@@@""".parseRock()

        val aye = """
            |@
            |@
            |@
            |@""".parseRock()

        val sqr = """
            |@@
            |@@""".parseRock()

        fun List<List<Boolean>>.newRockLocation(): Pos {
            return Pos(2, this.size + 3)
        }

        fun List<List<Boolean>>.checkCollision(rock: List<List<Boolean>>, pos: Pos): Boolean {
            return rock.indices.any { ry ->
                rock[ry].indices.any { rx ->
                    rock[ry][rx] && this[pos.x + rx, pos.y + ry]
                }
            }
        }

        fun MutableList<MutableList<Boolean>>.placeRock(rock: List<List<Boolean>>, pos: Pos) {
            rock.indices.forEach { ry ->
                rock[ry].indices.forEach { rx ->
                    this[pos.x + rx, pos.y + ry] = rock[ry][rx]
                }
            }
        }

        fun List<List<Boolean>>.getImprint(): List<List<Boolean>> {
            val imprint = mutableListOf<MutableList<Boolean>>()

            fun floodFill(x: Int, y: Int) {
                val iy = (size - 1) - y
                val a = this[x, y]
                val b = imprint[x, iy]
                if (a || b) return
                imprint[x, iy] = true
                floodFill(x, y - 1)
                floodFill(x - 1, y)
                floodFill(x + 1, y)
            }

            (0 until width).forEach { x ->
                floodFill(x, size - 1)
            }

            return imprint.map { it.toList() }
        }

        val rockList = listOf(minus, plus, ell, aye, sqr)
        val moveList = input[0].toList()

        var rockIndex = 0
        var moveIndex = 0

        val imprintSet = mutableMapOf<Triple<Int, List<List<Boolean>>, List<List<Boolean>>>, Pair<Long, Long>>()

        var index = 0L
        var totalHeight = 0L

        // iterate until we detect a cycle
        while (index < totalRocks) {
            val rock = rockList[rockIndex]

            val imprint = rocks.getImprint()
//            println("imprint")
//            println(imprint.prettyPrintImprint())
//            println(rocks.prettyPrintRocks())

//            if (index == 28L) {
//                println(rocks.prettyPrintRocks())
//            }

            val cycleKey = Triple(moveIndex, rock, imprint)

            if (imprintSet.contains(cycleKey)) {
                val (indexCycle, heightCycle) = imprintSet.getValue(cycleKey)
//                println("cycle $indexCycle")
//                println(cycleKey.third.prettyPrintImprint())
//                println(rocks.prettyPrintRocks())
                val dIndex = index - indexCycle
                val dHeight = rocks.size - heightCycle
                val cycleCount = (totalRocks - index) / dIndex
                totalHeight = (cycleCount * dHeight) + rocks.size
                index += cycleCount * dIndex
                break
            }

            imprintSet[cycleKey] = Pair(index, rocks.size.toLong())

            var pos = rocks.newRockLocation()

            while (true) {
                val move = moveList[moveIndex]
                moveIndex = (moveIndex + 1) % moveList.size

                when (move) {
                    '>' -> if (rocks.checkCollision(rock, pos.copy(x = pos.x + 1)).not()) {
                        pos = pos.copy(x = pos.x + 1)
                    }

                    '<' -> if (rocks.checkCollision(rock, pos.copy(x = pos.x - 1)).not()) {
                        pos = pos.copy(x = pos.x - 1)
                    }

                    else -> throw Exception()
                }

                if (rocks.checkCollision(rock, pos.copy(y = pos.y - 1))) {
                    rocks.placeRock(rock, pos)
                    break
                }

                pos = pos.copy(y = pos.y - 1)
            }

            rockIndex = (rockIndex + 1) % rockList.size
            index += 1
        }

        while (index < totalRocks) {
            val rock = rockList[rockIndex]
            var pos = rocks.newRockLocation()

            val currentHeight = rocks.size

            while (true) {
                val move = moveList[moveIndex]
                moveIndex = (moveIndex + 1) % moveList.size

                when (move) {
                    '>' -> if (rocks.checkCollision(rock, pos.copy(x = pos.x + 1)).not()) {
                        pos = pos.copy(x = pos.x + 1)
                    }

                    '<' -> if (rocks.checkCollision(rock, pos.copy(x = pos.x - 1)).not()) {
                        pos = pos.copy(x = pos.x - 1)
                    }

                    else -> throw Exception()
                }

                if (rocks.checkCollision(rock, pos.copy(y = pos.y - 1))) {
                    rocks.placeRock(rock, pos)
                    break
                }

                pos = pos.copy(y = pos.y - 1)
            }

            totalHeight += rocks.size - currentHeight
            rockIndex = (rockIndex + 1) % rockList.size
            index += 1
        }

        return totalHeight//.also { println(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput, 2022) == 3068)
    val input = readInput("Day17")
//    check(part1(input, 2021) == 3157)

//    println(part1(testInput, 100))
//    val totalRocks = 20L //
//    println(part2(listOf(">"), 20L))
//    for (i in 1_000_000..1_000_010) {
//        val a = part1(input, i).toLong()
//        val b = part2(input, i.toLong())
//        println("$i $a $b")
//        check(a == b)
//    }

    check(part2(testInput, 1_000_000_000_000L) == 1514285714288L)
//                           1_000_000_000_000L
    println(part2(input, 1_000_000_000_000L))
}
