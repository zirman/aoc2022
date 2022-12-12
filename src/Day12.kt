data class Sqr(val rowIndex: Int, val colIndex: Int)

fun main() {
    fun parseMap(input: List<String>): List<List<Int>> {
        return input.map { line ->
            line.map { c ->
                if (c == 'S') 0 else c - 'a'
            }
        }
    }

    fun search(
        map: List<List<Int>>,
        nextSqrs: Set<Sqr>,
        nextPredicate: (Int, Int) -> Boolean,
        endPredicate: (Sqr, Int) -> Boolean,
    ): Int {
        val rowIndices = map.indices
        val colIndices = map.first().indices

        tailrec fun search(searchDepth: Int, nextSqrs: Set<Sqr>, visitedSqrs: Set<Sqr>): Int {
            return when {
                nextSqrs.any { endPredicate(it, map[it.rowIndex][it.colIndex]) } -> searchDepth + 1
                else -> {
                    search(
                        searchDepth + 1,
                        nextSqrs
                            .flatMap { (ri, ci) ->
                                val height = map[ri][ci]
                                buildList {
                                    if (rowIndices.contains(ri - 1) && nextPredicate(height, map[ri - 1][ci])) {
                                        val sqr = Sqr(ri - 1, ci)
                                        if (visitedSqrs.contains(sqr).not()) add(sqr)
                                    }
                                    if (rowIndices.contains(ri + 1) && nextPredicate(height, map[ri + 1][ci])) {
                                        val sqr = Sqr(ri + 1, ci)
                                        if (visitedSqrs.contains(sqr).not()) add(sqr)
                                    }
                                    if (colIndices.contains(ci - 1) && nextPredicate(height, map[ri][ci - 1])) {
                                        val sqr = Sqr(ri, ci - 1)
                                        if (visitedSqrs.contains(sqr).not()) add(sqr)
                                    }
                                    if (colIndices.contains(ci + 1) && nextPredicate(height, map[ri][ci + 1])) {
                                        val sqr = Sqr(ri, ci + 1)
                                        if (visitedSqrs.contains(sqr).not()) add(sqr)
                                    }
                                }
                            }
                            .toSet(),
                        visitedSqrs.union(nextSqrs)
                    )
                }
            }
        }

        return search(0, nextSqrs, emptySet())
    }

    fun findEnd(map: List<List<Int>>): Sqr {
        val rowIndices = map.indices
        val colIndices = map.first().indices

        return rowIndices
            .flatMap { rowIndex ->
                colIndices.map { colIndex ->
                    Triple(rowIndex, colIndex, map[rowIndex][colIndex])
                }
            }
            .maxBy { (_, _, h) -> h }
            .let { (r, c) -> Sqr(r, c) }
    }

    fun part1(input: List<String>): Int {
        val map = parseMap(input)

        val start = input.asSequence()
            .flatMapIndexed { rowIndex, row ->
                row.mapIndexed { colIndex, s ->
                    Pair(s, Sqr(rowIndex, colIndex))
                }
            }
            .first { (s) -> s == 'S' }
            .second

        val end = findEnd(map)

        return search(
            map = map,
            nextSqrs = setOf(start),
            nextPredicate = { height, nextHeight -> height + 1 >= nextHeight },
            endPredicate = { sqr, _ -> sqr == end },
        )
    }

    fun part2(input: List<String>): Int {
        val map = parseMap(input)
        val end = findEnd(map)

        return search(
            map = map,
            nextSqrs = setOf(end),
            endPredicate = { _, height -> height == 0 },
            nextPredicate = { height, nextHeight -> nextHeight >= height - 1 },
        )
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    val input = readInput("Day12")
    println(part1(input))
    check(part2(testInput) == 29)
    println(part2(input))
}
