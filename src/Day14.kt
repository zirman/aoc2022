import kotlin.math.max
import kotlin.math.min

sealed interface Mat {
    object Air : Mat
    object Rock : Mat
    object Sand : Mat
}

fun main() {
    fun part1(input: List<String>): Int {
        val rockRanges = input
            .map { line ->
                line.split(" -> ").map { sqrStr ->
                    val (x, y) = sqrStr.split(",")
                    Sqr(y.toInt(), x.toInt())
                }
            }

        val maxY = max(rockRanges.maxOf { row -> row.maxOf { it.rowIndex } }, 0) + 2

        val minX = min(rockRanges.minOf { row -> row.minOf { it.colIndex } }, 500)
        val maxX = max(rockRanges.maxOf { row -> row.maxOf { it.colIndex } }, 500)
        val minY = min(rockRanges.minOf { row -> row.minOf { it.rowIndex } }, 0)
        val width = maxX - minX + 1
        val height = maxY - minY + 1

        val xRange = minX..maxX
        val yRange = minY..maxY

        operator fun Array<Mat>.get(x: Int, y: Int): Mat {
            return if (x in xRange && y in yRange) this[width * (y - minY) + (x - minX)]
            else Mat.Air
        }

        operator fun <T> Array<T>.set(x: Int, y: Int, t: T) {
            this[width * (y - minY) + (x - minX)] = t
        }

        val rocks = Array<Mat>(width * height) { Mat.Air }

        fun Array<Mat>.toString(width: Int): String {
            return toList().chunked(width).joinToString("\n") { row ->
                row.joinToString("") {
                    when (it) {
                        Mat.Air -> "."
                        Mat.Rock -> "#"
                        Mat.Sand -> "o"
                    }
                }
            }
        }

        rockRanges.forEach { r ->
            r.windowed(2, 1).forEach { (a, b) ->
                val (y1, x1) = a
                val (y2, x2) = b
                if (y1 == y2) {
                    (min(x1, x2)..max(x1, x2)).forEach { x ->
                        rocks[x, y1] = Mat.Rock
                    }
                } else if (x1 == x2) {
                    (min(y1, y2)..max(y1, y2)).forEach { y ->
                        rocks[x1, y] = Mat.Rock
                    }
                }
            }
        }

        tailrec fun addSand(x: Int, y: Int): Boolean {
            if ((x in xRange).not() || (y in yRange).not() || rocks[x, y] != Mat.Air) return false

            return when (Mat.Air) {
                rocks[x, y + 1] -> {
                    addSand(x, y + 1)
                }

                rocks[x - 1, y + 1] -> {
                    addSand(x - 1, y + 1)
                }

                rocks[x + 1, y + 1] -> {
                    addSand(x + 1, y + 1)
                }

                else -> {
                    rocks[x, y] = Mat.Sand
                    true
                }
            }
        }

        while (addSand(500, 0));

        return rocks.count { it == Mat.Sand }
    }

    fun part2(input: List<String>): Int {
        val rockRanges = input
            .map { line ->
                line.split(" -> ").map { sqrStr ->
                    val (x, y) = sqrStr.split(",")
                    Sqr(y.toInt(), x.toInt())
                }
            }

        val maxY = max(rockRanges.maxOf { row -> row.maxOf { it.rowIndex } }, 0) + 2

        val minX = min(min(rockRanges.minOf { row -> row.minOf { it.colIndex } }, 500), 500 - maxY)
        val maxX = max(max(rockRanges.maxOf { row -> row.maxOf { it.colIndex } }, 500), 500 + maxY)
        val minY = min(rockRanges.minOf { row -> row.minOf { it.rowIndex } }, 0)
        val width = maxX - minX + 1
        val height = maxY - minY + 1

        val xRange = minX..maxX
        val yRange = minY..maxY

        operator fun Array<Mat>.get(x: Int, y: Int): Mat {
            return if (x in xRange && y in yRange) this[width * (y - minY) + (x - minX)]
            else Mat.Air
        }

        operator fun <T> Array<T>.set(x: Int, y: Int, t: T) {
            this[width * (y - minY) + (x - minX)] = t
        }

        val rocks = Array<Mat>(width * height) { Mat.Air }

        xRange.forEach {
            rocks[it, maxY] = Mat.Rock
        }

        fun Array<Mat>.toString(width: Int): String {
            return toList().chunked(width).joinToString("\n") { row ->
                row.joinToString("") {
                    when (it) {
                        Mat.Air -> "."
                        Mat.Rock -> "#"
                        Mat.Sand -> "o"
                    }
                }
            }
        }

        rockRanges.forEach { r ->
            r.windowed(2, 1).forEach { (a, b) ->
                val (y1, x1) = a
                val (y2, x2) = b
                if (y1 == y2) {
                    (min(x1, x2)..max(x1, x2)).forEach { x ->
                        rocks[x, y1] = Mat.Rock
                    }
                } else if (x1 == x2) {
                    (min(y1, y2)..max(y1, y2)).forEach { y ->
                        rocks[x1, y] = Mat.Rock
                    }
                }
            }
        }

        tailrec fun addSand(x: Int, y: Int): Boolean {
            if ((x in xRange).not() || (y in yRange).not() || rocks[x, y] != Mat.Air) return false

            return when (Mat.Air) {
                rocks[x, y + 1] -> {
                    addSand(x, y + 1)
                }

                rocks[x - 1, y + 1] -> {
                    addSand(x - 1, y + 1)
                }

                rocks[x + 1, y + 1] -> {
                    addSand(x + 1, y + 1)
                }

                else -> {
                    rocks[x, y] = Mat.Sand
                    true
                }
            }
        }

        while (addSand(500, 0));

        return rocks.count { it == Mat.Sand }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    val input = readInput("Day14")
    println(part1(input))
    check(part2(testInput) == 93)
    println(part2(input))
}
