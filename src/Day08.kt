fun main() {
    fun part1(input: List<String>): Int {
        val trees = input.map { row -> row.map { it.digitToInt() } }
        val visible = trees.map { it.indices.map { false }.toMutableList() }

        for (c in trees.first().indices) {
            var h = -1
            for (r in trees.indices) {
                if (trees[r][c] > h) {
                    visible[r][c] = true
                    h = trees[r][c]
                }
            }
        }

        for (c in trees.first().indices) {
            var h = -1
            for (r in trees.indices.reversed()) {
                if (trees[r][c] > h) {
                    visible[r][c] = true
                    h = trees[r][c]
                }
            }
        }

        for (r in trees.indices) {
            var h = -1
            for (c in trees.first().indices) {
                if (trees[r][c] > h) {
                    visible[r][c] = true
                    h = trees[r][c]
                }
            }
        }

        for (r in trees.indices) {
            var h = -1
            for (c in trees.first().indices.reversed()) {
                if (trees[r][c] > h) {
                    visible[r][c] = true
                    h = trees[r][c]
                }
            }
        }

        return visible.sumOf { row -> row.count { it } }
    }

    fun part2(input: List<String>): Int {
        val trees = input.map { row -> row.map { it.digitToInt() } }

        val scenicScore = trees.indices.map { r ->
            trees.first().indices.map { c ->
                val max = trees[r][c]

                tailrec fun searchNorth(r: Int, acc: Int): Int {
                    return when {
                        r < 0 -> acc
                        trees[r][c] >= max -> acc + 1
                        else -> searchNorth(r - 1, acc + 1)
                    }
                }

                tailrec fun searchSouth(r: Int, acc: Int): Int {
                    return when {
                        r >= trees.size -> acc
                        trees[r][c] >= max -> acc + 1
                        else -> searchSouth(r + 1, acc + 1)
                    }
                }

                tailrec fun searchEast(c: Int, acc: Int): Int {
                    return when {
                        c >= trees.size -> acc
                        trees[r][c] >= max -> acc + 1
                        else -> searchEast(c + 1, acc + 1)
                    }
                }

                tailrec fun searchWest(c: Int, acc: Int): Int {
                    return when {
                        c < 0 -> acc
                        trees[r][c] >= max -> acc + 1
                        else -> searchWest(c - 1, acc + 1)
                    }
                }

                searchNorth(r - 1, 0) *
                        searchSouth(r + 1, 0) *
                        searchEast(c + 1, 0) *
                        searchWest(c - 1, 0)
            }
        }

        return scenicScore.maxOf { row -> row.maxOf { it } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    val input = readInput("Day08")
    println(part1(input))
    check(part2(testInput) == 8)
    println(part2(input))
}
