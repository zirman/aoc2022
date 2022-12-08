fun main() {
    fun part1(input: List<String>): Int {
        val trees = input.map { row -> row.map { it - '0' } }
        val visible = trees.map { it.indices.map { false }.toMutableList() }

        trees.first().indices.forEach { c ->
            fun searchNorth(r: Int, h: Int) {
                if (r >= trees.size) {
                    return
                } else if (trees[r][c] > h) {
                    visible[r][c] = true
                    searchNorth(r + 1, trees[r][c])
                } else {
                    searchNorth(r + 1, h)
                }
            }

            searchNorth(0, -1)
        }

        trees.first().indices.forEach { c ->
            fun searchSouth(r: Int, h: Int) {
                if (r < 0) {
                    return
                } else if (trees[r][c] > h) {
                    visible[r][c] = true
                    searchSouth(r - 1, trees[r][c])
                } else {
                    searchSouth(r - 1, h)
                }
            }

            searchSouth(trees.size - 1, -1)
        }

        trees.indices.forEach { r ->
            fun searchWest(c: Int, h: Int) {
                if (c >= trees.size) {
                    return
                } else if (trees[r][c] > h) {
                    visible[r][c] = true
                    searchWest(c + 1, trees[r][c])
                } else {
                    searchWest(c + 1, h)
                }
            }

            searchWest(0, -1)
        }

        trees.indices.forEach { r ->
            fun searchEast(c: Int, h: Int) {
                if (c < 0) {
                    return
                } else if (trees[r][c] > h) {
                    visible[r][c] = true
                    searchEast(c - 1, trees[r][c])
                } else {
                    searchEast(c - 1, h)
                }
            }

            searchEast(trees.size - 1, -1)
        }

        return visible.sumOf { it.count { it } }
    }

    fun part2(input: List<String>): Int {
        val trees = input.map { row -> row.map { it - '0' } }

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
