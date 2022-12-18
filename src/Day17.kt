//package me.peckb.aoc._2022.calendar.day17

//import me.peckb.aoc._2022.calendar.day17.Day17.Direction.LEFT
//import me.peckb.aoc._2022.calendar.day17.Day17.Direction.RIGHT

//import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min
import kotlin.system.measureTimeMillis

sealed interface Direction {
    object LEFT : Direction
    object RIGHT : Direction
}

class Day17 {
//    fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
//        val jetPushes = asJetDirections(input)
//        dropRocks(jetPushes, 2022).first.size
//    }

    fun partTwo() = readInput("Day17").take(1).also { println(it) }.map { input ->
        println("input")
        val jetPushes = asJetDirections(input)

        val rocks = 10000
        val cavesWithProbableCycle = dropRocks(jetPushes, rocks).first
        var cycle: List<String> = emptyList()
        // there is "probably" a cycle in the 1k -> 5k range
        run earlyReturn@{
            (5000 downTo 1000).forEach { possibleCycleSize ->
                cavesWithProbableCycle.chunked(possibleCycleSize)
                    .windowed(2)
                    .forEach { (first, last) ->
                        if (first == last) {
                            cycle = first
                            return@earlyReturn
                        }
                    }
            }
        }

        var previousCaves = mutableListOf<String>()
        var previousJetIndex = 0
        val minRocksForCycle = (0 until 10000).first { previouslyDroppedRocks ->
            dropRocks(jetPushes, 1, previousCaves, previouslyDroppedRocks, previousJetIndex).also {
                previousCaves = it.first
                previousJetIndex = it.second
            }
            previousCaves.takeLast(cycle.size) == cycle
        } + 1 // add one since the counter is technically the previously dropped rock count

        val nextRocksForCycle = (minRocksForCycle until 10000).first { previouslyDroppedRocks ->
            dropRocks(jetPushes, 1, previousCaves, previouslyDroppedRocks, previousJetIndex).also {
                previousCaves = it.first
                previousJetIndex = it.second
            }
            previousCaves.takeLast(cycle.size) == cycle
        } + 1 // add one since the counter is technically the previously dropped rock count

        val rocksCycleLength = nextRocksForCycle - minRocksForCycle
        val sizeAtFirstCycle = dropRocks(jetPushes, minRocksForCycle).first.size
        val sizeAtSecondCycle = dropRocks(jetPushes, nextRocksForCycle).first.size

        val heightGrownEachCycle = sizeAtSecondCycle - sizeAtFirstCycle

        val numberOfCycles = (ALL_THE_ROCKS - minRocksForCycle) / rocksCycleLength
        val rockCountBeforeACyclePushesUsOver = minRocksForCycle + (numberOfCycles * rocksCycleLength)
        val rocksRemainingToPlace = ALL_THE_ROCKS - rockCountBeforeACyclePushesUsOver

        val cavesAfterAddingRemainingRocks = dropRocks(jetPushes, minRocksForCycle + rocksRemainingToPlace.toInt())
        val heightAdded = cavesAfterAddingRemainingRocks.first.size - sizeAtFirstCycle

        sizeAtFirstCycle.toLong() + (heightGrownEachCycle * numberOfCycles) + heightAdded
    }

    private fun dropRocks(
        jetPushes: List<Direction>,
        rocksToDrop: Int,
        cavern: MutableList<String> = mutableListOf(),
        previousRocksDropped: Int = 0,
        previousJetsUsed: Int = 0
    ): Pair<MutableList<String>, Int> {
        var rockIndex = previousRocksDropped % ROCKS.size
        var jetPushesIndex = previousJetsUsed % jetPushes.size

        repeat(rocksToDrop) {
            val nextRock = ROCKS[rockIndex]
            val rockEdges = RockEdges(
                leftEdgeIndexOfRock = 2,
                bottomEdgeOfRock = cavern.size + 3, // three blank spaces above the top of the current cavern
                rightEdgeIndexOfRock = 2 + (nextRock.width - 1)
            )

            var rockHadSettled = false
            while (!rockHadSettled) {
                // try and push the rock
                when (jetPushes[jetPushesIndex]) {
                    Direction.LEFT -> tryMoveRockLeft(cavern, nextRock, rockEdges)
                    Direction.RIGHT -> tryMoveRockRight(cavern, nextRock, rockEdges)
                }

                rockHadSettled = checkForDropCollision(cavern, nextRock, rockEdges)

                // if so, we have a collision and merge it into our cave system
                if (rockHadSettled) {
                    if (rockEdges.bottomEdgeOfRock == cavern.size) { // we landed just above the bottom do just add ourselves to the cavern
                        landRockOnTop(cavern, nextRock, rockEdges)
                    } else { // we landed "inside" the cavern and need to merge our data
                        landRockInsideCavern(cavern, nextRock, rockEdges)
                    }
                } else { // if not, we should drop the rock by one index
                    rockEdges.bottomEdgeOfRock--
                }

                jetPushesIndex = (jetPushesIndex + 1) % jetPushes.size
            }

            rockIndex = (rockIndex + 1) % ROCKS.size
        }

        return cavern to jetPushesIndex
    }

    private fun landRockInsideCavern(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
        val numRowsToMerge = min(cavern.size - rockEdges.bottomEdgeOfRock, nextRock.height)

        val rockRowsToMerge = nextRock.data.takeLast(numRowsToMerge)
        rockRowsToMerge.forEachIndexed { rowOffset, rockRow ->
            val emptyBefore = ".".repeat(rockEdges.leftEdgeIndexOfRock)
            val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rockEdges.rightEdgeIndexOfRock)
            val myRow = "$emptyBefore$rockRow$emptyAfter"
            val cavernIndex = (rockEdges.bottomEdgeOfRock + (numRowsToMerge - 1) - rowOffset)
            val cavernRow = cavern[cavernIndex]
            val newRow = myRow.zip(cavernRow).map { (me, them) ->
                if (me == '.') them else me
            }.joinToString("")
            cavern[cavernIndex] = newRow
        }
        nextRock.data.dropLast(numRowsToMerge).reversed().forEach { rockRow ->
            val emptyBefore = ".".repeat(rockEdges.leftEdgeIndexOfRock)
            val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rockEdges.rightEdgeIndexOfRock)
            cavern.add("$emptyBefore$rockRow$emptyAfter")
        }
    }

    private fun landRockOnTop(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
        nextRock.data.reversed().forEach { rockRow ->
            val emptyBefore = ".".repeat(rockEdges.leftEdgeIndexOfRock)
            val emptyAfter = ".".repeat(MAX_CHAMBER_INDEX - rockEdges.rightEdgeIndexOfRock)
            cavern.add("$emptyBefore$rockRow$emptyAfter")
        }
    }

    private fun checkForDropCollision(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges): Boolean {
        // is any "solid" part of our rock directly above a "solid" part of the cave?
        // TODO: this should be reversed, going from bottom to top for rock data
        return nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
            val cavernIndexOfRockIndex =
                rockEdges.bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned - 1
            if (0 <= cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
                val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)

                rockRow.withIndex().any { (rowIndex, rockSpace) ->
                    val rockRowToCollideIntoIndex = rockEdges.leftEdgeIndexOfRock + rowIndex
                    rockSpace != '.' && rockRowToCollideInto[rockRowToCollideIntoIndex] != '.'
                }
            } else {
                false
            }
        } || rockEdges.bottomEdgeOfRock == 0
    }

    private fun tryMoveRockRight(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
        // check for an edge of the cavern stoppage
        if (rockEdges.rightEdgeIndexOfRock < MAX_CHAMBER_INDEX) {
            // check for hitting some existing solid ground
            val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
                val cavernIndexOfRockIndex =
                    rockEdges.bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

                if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
                    rockRow.withIndex().any { (rockRowIndex, collisionCheckPortion) ->
                        if (collisionCheckPortion != '.') {
                            val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
                            rockRowToCollideInto[rockEdges.rightEdgeIndexOfRock + 1 - (nextRock.width - 1 - rockRowIndex)] != '.'
                        } else {
                            false
                        }
                    }
                } else {
                    false
                }
            }
            if (!collision) {
                rockEdges.leftEdgeIndexOfRock++
                rockEdges.rightEdgeIndexOfRock++
            }
        }
    }

    private fun tryMoveRockLeft(cavern: MutableList<String>, nextRock: Rock, rockEdges: RockEdges) {
        // check for an edge of the cavern stoppage
        if (rockEdges.leftEdgeIndexOfRock > 0) {
            // check for hitting some existing solid ground
            val collision = nextRock.data.withIndex().any { (rockHeightIndexTopAligned, rockRow) ->
                val cavernIndexOfRockIndex =
                    rockEdges.bottomEdgeOfRock + (nextRock.height - 1) - rockHeightIndexTopAligned

                if (0 < cavernIndexOfRockIndex && cavernIndexOfRockIndex < cavern.size) {
                    rockRow.withIndex().any { (rockRowIndex, collisionCheckPortion) ->
                        if (collisionCheckPortion != '.') {
                            val rockRowToCollideInto = cavern.elementAt(cavernIndexOfRockIndex)
                            rockRowToCollideInto[rockEdges.leftEdgeIndexOfRock - 1 + (rockRowIndex)] != '.'
                        } else {
                            false
                        }
                    }
                } else {
                    false
                }
            }
            if (!collision) {
                rockEdges.leftEdgeIndexOfRock--
                rockEdges.rightEdgeIndexOfRock--
            }
        }
    }

    private fun asJetDirections(input: String): List<Direction> = input.map {
        if (it == '>') Direction.RIGHT else Direction.LEFT
    }

    data class Rock(val height: Int, val width: Int, val data: List<String>)
    data class RockEdges(var leftEdgeIndexOfRock: Int, var bottomEdgeOfRock: Int, var rightEdgeIndexOfRock: Int)
    enum class Direction { LEFT, RIGHT }

    companion object {
        private const val MAX_CHAMBER_INDEX = 6

        private const val ALL_THE_ROCKS = 1000000000000

        private val ROCKS = listOf(
            Rock(1, 4, listOf("1111")),
            Rock(3, 3, listOf(".2.", "222", ".2.")),
            Rock(3, 3, listOf("..3", "..3", "333")),
            Rock(4, 1, listOf("4", "4", "4", "4")),
            Rock(2, 2, listOf("55", "55")),
        )
    }
}


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

        return totalHeight
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput, 2022) == 3068)
    val input = readInput("Day17")
    println(part1(input, 2021))
    check(part2(testInput, 1_000_000_000_000L) == 1514285714288L)
    println(measureTimeMillis { part2(input, 1_000_000_000_000L) })

    println(Day17().partTwo())
}
