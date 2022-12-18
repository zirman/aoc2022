sealed interface LavaGrid {
    object Lava : LavaGrid
    object Air : LavaGrid
    object Visited : LavaGrid
}

//data class(val widthX, val width)
typealias LavaGridInfo = Pair<List<List<MutableList<LavaGrid>>>, Triple<Int, Int, Int>>

fun main() {
    fun parseLavaGridInfo(input: List<String>): LavaGridInfo {
        val drops = input.map { line ->
            val (x, y, z) = line.split(",")
            Triple(x.toInt(), y.toInt(), z.toInt())
        }

        val minX = drops.minOf { (x, _, _) -> x }
        val maxX = drops.maxOf { (x, _, _) -> x }
        val minY = drops.minOf { (_, y, _) -> y }
        val maxY = drops.maxOf { (_, y, _) -> y }
        val minZ = drops.minOf { (_, _, z) -> z }
        val maxZ = drops.maxOf { (_, _, z) -> z }

        val widthX = maxX - minX + 3
        val widthY = maxY - minY + 3
        val widthZ = maxZ - minZ + 3

        val grid = List(widthZ) { List(widthY) { MutableList<LavaGrid>(widthX) { LavaGrid.Air } } }

        drops.forEach { (x, y, z) ->
            grid[z - minZ + 1][y - minY + 1][x - minX + 1] = LavaGrid.Lava
        }

        return Pair(grid, Triple(widthX, widthY, widthZ))
    }

    fun floodFillSearch(state: LavaGridInfo): DeepRecursiveFunction<Triple<Int, Int, Int>, Int> {
        val (grid, widths) = state
        val (widthX, widthY, widthZ) = widths

        return DeepRecursiveFunction { (x: Int, y: Int, z: Int) ->
            if (x !in 0 until widthX ||
                y !in 0 until widthY ||
                z !in 0 until widthZ
            ) return@DeepRecursiveFunction 0

            when (grid[z][y][x]) {
                LavaGrid.Visited -> 0
                LavaGrid.Lava -> 1

                LavaGrid.Air -> {
                    grid[z][y][x] = LavaGrid.Visited

                    callRecursive(Triple(x - 1, y, z)) +
                            callRecursive(Triple(x + 1, y, z)) +
                            callRecursive(Triple(x, y - 1, z)) +
                            callRecursive(Triple(x, y + 1, z)) +
                            callRecursive(Triple(x, y, z - 1)) +
                            callRecursive(Triple(x, y, z + 1))
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val state = parseLavaGridInfo(input)
        val (grid, widths) = parseLavaGridInfo(input)
        val (widthX, widthY, widthZ) = widths
        val search = floodFillSearch(state)

        return (0 until widthZ).sumOf { z ->
            (0 until widthY).sumOf { y ->
                (0 until widthX).sumOf { x ->
                    if (grid[z][y][x] == LavaGrid.Air) {
                        search(Triple(x, y, z))
                    } else {
                        0
                    }
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val state = parseLavaGridInfo(input)
        val search = floodFillSearch(state)
        return search(Triple(0, 0, 0))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    val input = readInput("Day18")
    println(part1(input))
    check(part2(testInput) == 58)
    println(part2(input))
}
