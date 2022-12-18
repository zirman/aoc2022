import kotlin.system.measureTimeMillis

fun <T> permutation(xs: List<T>): List<List<T>> {
    return if (xs.isEmpty()) listOf(emptyList())
    else xs.flatMapIndexed { index, s ->
        permutation(buildList {
            addAll(xs.subList(0, index))
            addAll(xs.subList(index + 1, xs.size))
        }).map { tail ->
            buildList {
                add(s)
                addAll(tail)
            }
        }
    }
}

fun <T> groupPermutation(xs: List<T>): List<List<T>> {
    return (0 until xs.indices.fold(1) { acc, _ -> acc * 2 }).map { bits ->
        bits.toString(2).reversed().flatMapIndexed { index, c ->
            if (c == '1') listOf(xs[index])
            else emptyList()
        }
    }
}

fun <T> partitionPermutation(xs: List<T>): List<Pair<List<T>, List<T>>> {
    return (0 until xs.indices.fold(1) { acc, _ -> acc * 2 }).map { bits ->
        val (g1, g2) = bits
            .toString(2)
            .toList()
            .reversed()
            .let { s ->
                buildList {
                    addAll(s)
                    addAll(List(xs.size - s.size) { '0' })
                }
            }
            .mapIndexed { index, c -> Pair(index, c) }
            .partition { (_, c) -> c == '1' }

        Pair(
            g1.map { (index, _) -> xs[index] },
            g2.map { (index, _) -> xs[index] }
        )
    }
}

fun <T> partitionUniquePermutation(xs: List<T>): List<Pair<List<T>, List<T>>> {
    return (0 until xs.indices.fold(1) { acc, _ -> acc * 2 } / 2).map { bits ->
        val (g1, g2) = bits
            .toString(2)
            .toList()
            .reversed()
            .let { s ->
                buildList {
                    addAll(s)
                    addAll(List(xs.size - s.size) { '0' })
                }
            }
            .mapIndexed { index, c -> Pair(index, c) }
            .partition { (_, c) -> c == '1' }

        Pair(
            g1.map { (index, _) -> xs[index] },
            g2.map { (index, _) -> xs[index] }
        )
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val valveMap = input.associate { line ->
            val (valve, flowRateStr, toValvesStr) = """^Valve (\w\w) has flow rate=(\d+); tunnels? leads? to valves? (.+)$"""
                .toRegex().matchEntire(line)!!.destructured
            val toValves = toValvesStr.split(", ")
            val flowRate = flowRateStr.toInt()
            Pair(valve, Pair(flowRate, toValves))
        }

        // shortest hop distance to valve
        fun hops(fromValve: String, toValve: String): Int? {
            var i = 0
            val visited = mutableSetOf<String>()
            var fromValves = setOf(fromValve)
            while (true) {
                if (fromValves.isEmpty()) {
                    return null
                } else if (fromValves.contains(toValve)) {
                    return i
                }
                visited.addAll(fromValves)
                fromValves = fromValves
                    .flatMap {
                        val (_, valves) = valveMap[it]!!
                        valves
                    }
                    .toSet()
                    .subtract(visited)
                i += 1
            }
        }

        // valves with positive flow rate
        val goodClosedValves = valveMap
            .toList()
            .filter { (_, value) ->
                val (flowRate) = value
                flowRate > 0
            }
            .map { (valve) -> valve }

        val routes = goodClosedValves.plus("AA").let { valves ->
            valves
                .mapIndexed { index, fromValve ->
                    Pair(
                        fromValve,
                        buildList {
                            addAll(valves.subList(0, index))
                            addAll(valves.subList(index + 1, valves.size))
                        }
                            .mapNotNull { toValve ->
                                hops(fromValve, toValve)
                                    ?.let { Pair(toValve, it) }
                            }
                    )
                }
                .associate { it }
        }

        fun permute(t: Int, fromValve: String, closedValves: Set<String>): Int {
            val tm = 30
            return routes[fromValve]!!
                .filter { (nextValve, hops) ->
                    t + hops < tm && closedValves.contains(nextValve)
                }
                .maxOfOrNull { (nextValve, hops) ->
                    val (flowRate) = valveMap[nextValve]!!
                    val totalValveFlow = flowRate * (tm - (t + hops))
                    totalValveFlow + permute(
                        t = t + hops + 1,
                        fromValve = nextValve,
                        closedValves = closedValves - nextValve
                    )
                }
                ?: 0
        }

        return permute(1, "AA", goodClosedValves.toSet())//.also { println(it) }
    }

    fun part2(input: List<String>): Int {
        val valveMap = input.associate { line ->
            val (valve, flowRateStr, toValvesStr) = """^Valve (\w\w) has flow rate=(\d+); tunnels? leads? to valves? (.+)$"""
                .toRegex().matchEntire(line)!!.destructured
            val toValves = toValvesStr.split(", ")
            val flowRate = flowRateStr.toInt()
            Pair(valve, Pair(flowRate, toValves))
        }

        // shortest hop distance to valve
        fun hops(fromValve: String, toValve: String): Int? {
            var i = 0
            val visited = mutableSetOf<String>()
            var fromValves = setOf(fromValve)
            while (true) {
                if (fromValves.isEmpty()) {
                    return null
                } else if (fromValves.contains(toValve)) {
                    return i
                }
                visited.addAll(fromValves)
                fromValves = fromValves
                    .flatMap {
                        val (_, valves) = valveMap[it]!!
                        valves
                    }
                    .toSet()
                    .subtract(visited)
                i += 1
            }
        }

        // valves with positive flow rate
        val goodClosedValves = valveMap
            .toList()
            .filter { (_, value) ->
                val (flowRate) = value
                flowRate > 0
            }
            .map { (valve) -> valve }

        val routes = goodClosedValves.plus("AA").let { valves ->
            valves
                .mapIndexed { index, fromValve ->
                    Pair(
                        fromValve,
                        buildList {
                            addAll(valves.subList(0, index))
                            addAll(valves.subList(index + 1, valves.size))
                        }
                            .mapNotNull { toValve ->
                                hops(fromValve, toValve)
                                    ?.let { Pair(toValve, it) }
                            }
                    )
                }
                .associate { it }
        }

        fun permute(t: Int, fromValve: String, closedValves: Set<String>): Int {
            val tm = 26
            return routes[fromValve]!!
                .filter { (nextValve, hops) ->
                    t + hops < tm && closedValves.contains(nextValve)
                }
                .maxOfOrNull { (nextValve, hops) ->
                    val (flowRate) = valveMap[nextValve]!!
                    val totalValveFlow = flowRate * (tm - (t + hops))
                    totalValveFlow + permute(
                        t = t + hops + 1,
                        fromValve = nextValve,
                        closedValves = closedValves - nextValve
                    )
                }
                ?: 0
        }

        return partitionUniquePermutation(goodClosedValves) // .map { (a, b) -> setOf(a, b) }.toSet()
            .maxOf { ss ->
                val (s1, s2) = ss.toList()
                permute(1, "AA", s1.toSet()) + permute(1, "AA", s2.toSet())
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    val input = readInput("Day16")
    println(part1(input))

    check(part2(testInput) == 1707)
    println(part2(input))
}
