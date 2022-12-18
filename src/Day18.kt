sealed interface LavaGrid {
    object Lava : LavaGrid
    object Air : LavaGrid
    object Visited : LavaGrid
}

fun main() {
    fun part1(input: List<String>): Int {
        val drops = input.map { line ->
            val (x, y, z) = line.split(",")
            Triple(x.toInt(), y.toInt(), z.toInt())
        }

        val minX = drops.minOf { (x, y, z) -> x }
        val maxX = drops.maxOf { (x, y, z) -> x }
        val minY = drops.minOf { (x, y, z) -> y }
        val maxY = drops.maxOf { (x, y, z) -> y }
        val minZ = drops.minOf { (x, y, z) -> z }
        val maxZ = drops.maxOf { (x, y, z) -> z }

        val widthX = maxX - minX + 3
        val widthY = maxY - minY + 3
        val widthZ = maxZ - minZ + 3

        val grid = List(widthZ) { List(widthY) { MutableList<LavaGrid>(widthX) { LavaGrid.Air } } }

        drops.forEach { (x, y, z) ->
            grid[z - minZ + 1][y - minY + 1][x - minX + 1] = LavaGrid.Lava
        }

        val d = DeepRecursiveFunction<Triple<Int, Int, Int>, Int> { (x: Int, y: Int, z: Int) ->
            if (x in 0 until widthX &&
                y in 0 until widthY &&
                z in 0 until widthZ
            ) {
                when (grid[z][y][x]) {
                    LavaGrid.Visited -> {
                        0
                    }

                    LavaGrid.Air -> {
                        grid[z][y][x] = LavaGrid.Visited
                        callRecursive(Triple(x - 1, y, z)) +
                                callRecursive(Triple(x + 1, y, z)) +
                                callRecursive(Triple(x, y - 1, z)) +
                                callRecursive(Triple(x, y + 1, z)) +
                                callRecursive(Triple(x, y, z - 1)) +
                                callRecursive(Triple(x, y, z + 1))
                    }

                    LavaGrid.Lava -> {
                        1
                    }
                }
            } else {
                0
            }
        }

        fun flood(x: Int, y: Int, z: Int): Int {
            return if (x in 0 until widthX &&
                y in 0 until widthY &&
                z in 0 until widthZ
            ) {
                when (grid[z][y][x]) {
                    LavaGrid.Visited -> {
                        0
                    }

                    LavaGrid.Air -> {
                        grid[z][y][x] = LavaGrid.Visited
                        flood(x - 1, y, z) +
                                flood(x + 1, y, z) +
                                flood(x, y - 1, z) +
                                flood(x, y + 1, z) +
                                flood(x, y, z - 1) +
                                flood(x, y, z + 1)
                    }

                    LavaGrid.Lava -> {
                        1
                    }
                }
            } else {
                0
            }
        }

        return (0 until widthZ).sumOf { z ->
            (0 until widthY).sumOf { y ->
                (0 until widthX).sumOf { x ->
                    if (grid[z][y][x] == LavaGrid.Air) {
                        d(Triple(x, y, z))
                    } else {
                        0
                    }
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val drops = input.map { line ->
            val (x, y, z) = line.split(",")
            Triple(x.toInt(), y.toInt(), z.toInt())
        }

        val minX = drops.minOf { (x, y, z) -> x }
        val maxX = drops.maxOf { (x, y, z) -> x }
        val minY = drops.minOf { (x, y, z) -> y }
        val maxY = drops.maxOf { (x, y, z) -> y }
        val minZ = drops.minOf { (x, y, z) -> z }
        val maxZ = drops.maxOf { (x, y, z) -> z }

        val widthX = maxX - minX + 3
        val widthY = maxY - minY + 3
        val widthZ = maxZ - minZ + 3

        val grid = List(widthZ) { List(widthY) { MutableList<LavaGrid>(widthX) { LavaGrid.Air } } }

        drops.forEach { (x, y, z) ->
            grid[z - minZ + 1][y - minY + 1][x - minX + 1] = LavaGrid.Lava
        }

        val d = DeepRecursiveFunction<Triple<Int, Int, Int>, Int> { (x: Int, y: Int, z: Int) ->
            if (x in 0 until widthX &&
                y in 0 until widthY &&
                z in 0 until widthZ
            ) {
                when (grid[z][y][x]) {
                    LavaGrid.Visited -> {
                        0
                    }

                    LavaGrid.Air -> {
                        grid[z][y][x] = LavaGrid.Visited
                        callRecursive(Triple(x - 1, y, z)) +
                                callRecursive(Triple(x + 1, y, z)) +
                                callRecursive(Triple(x, y - 1, z)) +
                                callRecursive(Triple(x, y + 1, z)) +
                                callRecursive(Triple(x, y, z - 1)) +
                                callRecursive(Triple(x, y, z + 1))
                    }

                    LavaGrid.Lava -> {
                        1
                    }
                }
            } else {
                0
            }
        }

        fun flood(x: Int, y: Int, z: Int): Int {
            return if (x in 0 until widthX &&
                y in 0 until widthY &&
                z in 0 until widthZ
            ) {
                when (grid[z][y][x]) {
                    LavaGrid.Visited -> {
                        0
                    }

                    LavaGrid.Air -> {
                        grid[z][y][x] = LavaGrid.Visited
                        flood(x - 1, y, z) +
                                flood(x + 1, y, z) +
                                flood(x, y - 1, z) +
                                flood(x, y + 1, z) +
                                flood(x, y, z - 1) +
                                flood(x, y, z + 1)
                    }

                    LavaGrid.Lava -> {
                        1
                    }
                }
            } else {
                0
            }
        }

        return (0 until widthZ).sumOf { z ->
            (0 until widthY).sumOf { y ->
                (0 until widthX).sumOf { x ->
                    if (grid[z][y][x] == LavaGrid.Air && (x == 0 || y == 0 || z == 0)) {
                        d(Triple(x, y, z))
                    } else {
                        0
                    }
                }
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    val input = readInput("Day18")
    println(part1(input))
    check(part2(testInput) == 58)
    println(part2(input))
}
