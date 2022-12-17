import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

data class Pos(val x: Int, val y: Int)
data class Sensor(val x: Int, val y: Int)
data class Beacon(val x: Int, val y: Int)

sealed interface SensorGrid {
    object Air : SensorGrid
    object Sensor : SensorGrid
    object Beacon : SensorGrid
    object Scanned : SensorGrid
}

data class Range(val start: Int, val end: Int)

fun Range.contains(x: Int): Boolean {
    return x in start..end
}

fun Range.contains(range: Range): Boolean {
    return start <= range.start && end >= range.end
}

// combines two ranges if into a single continuous range
fun tryMerge(r1: Range, r2: Range): Range? {
    return when {
        r1.contains(r2.start - 1) -> Range(r1.start, max(r1.end, r2.end))
        r1.contains(r2.end + 1) -> Range(min(r1.start, r2.start), r1.end)
        r2.contains(r1.start - 1) -> Range(r2.start, max(r1.end, r2.end))
        r2.contains(r1.end + 1) -> Range(min(r1.start, r2.start), r2.end)
        else -> null
    }
}

fun merge(rs: List<Range>, r: Range): List<Range> {
    return buildList {
        var m = r
        rs.forEach { r2 ->
            val q = tryMerge(r2, m)
            if (q != null) {
                m = q
            } else {
                add(r2)
            }
        }
        add(m)
    }
}

operator fun Range.minus(range: Range): List<Range> {
    return if (start < range.start) {
        if (end > range.end) {
            listOf(Range(start, range.start - 1), Range(range.end + 1, end))
        } else {
            listOf(Range(start, range.start - 1))
        }
    } else if (start <= range.end) {
        if (end > range.end) {
            listOf(Range(range.end + 1, end))
        } else {
            listOf()
        }
    } else {
        listOf(this)
    }
}

operator fun List<Range>.minus(range: Range): List<Range> {
    return this.fold(emptyList()) { acc, r ->
        acc + (r - range)
    }
}

val Range.size: Int
    get() {
        return end - start + 1
    }

@ExperimentalStdlibApi
fun main() {
    fun part1(input: List<String>, searchRow: Int): Int {
        val sensors = input.map { line ->
            val (a, b, c, d) = """^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$"""
                .toRegex()
                .matchEntire(line)!!
                .destructured

            val sensor = Sensor(a.toInt(), b.toInt())
            val beacon = Beacon(c.toInt(), d.toInt())
            val manhattanDistance = (sensor.x - beacon.x).absoluteValue + (sensor.y - beacon.y).absoluteValue
            Triple(sensor, beacon, manhattanDistance)
        }

        val minX = sensors.minOf { (s, _, m) -> s.x - m }
        val maxX = sensors.maxOf { (s, _, m) -> s.x + m }
        val minY = sensors.minOf { (s, _, m) -> s.y - m }
        val maxY = sensors.maxOf { (s, _, m) -> s.y + m }

//        val width = maxX - minX + 1
//        val height = maxY - minY + 1

        val xRange = minX..maxX
        val yRange = minY..maxY

//        operator fun List<List<SensorGrid>>.get(p: Pos): SensorGrid {
//            return if (p.x in xRange && p.y in yRange) this[p.y - minY][p.x - minX]
//            else SensorGrid.Air
//        }
//
//        operator fun List<MutableList<SensorGrid>>.set(p: Pos, t: SensorGrid) {
//            when (this[p.y - minY][p.x - minX]) {
//                SensorGrid.Air, SensorGrid.Scanned -> {
//                    this[p.y - minY][p.x - minX] = t
//                }
//
//                else -> {}
//            }
//        }

//        println(width)
//        println(height)
//        println(width * height)

        val grid: MutableMap<Pos, SensorGrid> = mutableMapOf()

//        fun Array<SensorGrid>.toString(width: Int): String {
//            return toList().chunked(width).joinToString("\n") { row ->
//                row.joinToString("") {
//                    when (it) {
//                        SensorGrid.Air -> "."
//                        SensorGrid.Beacon -> "B"
//                        SensorGrid.Scanned -> "#"
//                        SensorGrid.Sensor -> "S"
//                    }
//                }
//            }
//        }

        sensors.forEach { (s, b, m) ->
            grid[Pos(s.x, s.y)] = SensorGrid.Sensor
            grid[Pos(b.x, b.y)] = SensorGrid.Beacon

            val visited = mutableSetOf<Pos>()

            val fillScanned = DeepRecursiveFunction { p: Pos ->
                if (visited.contains(p) || (p.x - s.x).absoluteValue + (p.y - s.y).absoluteValue > m) return@DeepRecursiveFunction
                visited.add(p)
                when (grid[p]) {
                    SensorGrid.Air, SensorGrid.Scanned, null -> {
                        grid[p] = SensorGrid.Scanned
                    }

                    else -> {}
                }
                callRecursive(Pos(p.x - 1, p.y))
                callRecursive(Pos(p.x + 1, p.y))
                callRecursive(Pos(p.x, p.y - 1))
                callRecursive(Pos(p.x, p.y + 1))
            }

//            fun fillScanned(p: Pos) {
//                if (visited.contains(p) || (p.x - s.x).absoluteValue + (p.y - s.y).absoluteValue > m) return
//                visited.add(p)
//                when (grid[p]) {
//                    SensorGrid.Air, SensorGrid.Scanned, null -> {
//                        grid[p] = SensorGrid.Scanned
//                    }
//
//                    else -> {}
//                }
//                fillScanned(Pos(p.x - 1, p.y))
//                fillScanned(Pos(p.x + 1, p.y))
//                fillScanned(Pos(p.x, p.y - 1))
//                fillScanned(Pos(p.x, p.y + 1))
//            }

            fillScanned(Pos(s.x, s.y))

//            yRange.joinToString("\n") { y ->
//                xRange.joinToString("") { x ->
//                    when (grid[Pos(x, y)]) {
//                        SensorGrid.Air -> "."
//                        SensorGrid.Beacon -> "B"
//                        SensorGrid.Scanned -> "#"
//                        SensorGrid.Sensor -> "S"
//                        null -> "."
//                    }
//                }
//            }.also { println(it) }
//            println(grid.toString())
//            println()
        }

        yRange.joinToString("\n") { y ->

            y.toString().last() + xRange.joinToString("") { x ->
                when (grid[Pos(x, y)]) {
                    SensorGrid.Air -> "."
                    SensorGrid.Beacon -> "B"
                    SensorGrid.Scanned -> "#"
                    SensorGrid.Sensor -> "S"
                    null -> "."
                }
            }
        }.also { println(it) }

        return xRange.count { x -> grid[Pos(x, searchRow)] == SensorGrid.Scanned }
    }

    fun part2(input: List<String>, maxXY: Int): Long {
        val sensors = input.map { line ->
            val (a, b, c, d) = """^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$"""
                .toRegex()
                .matchEntire(line)!!
                .destructured

            val sensor = Sensor(a.toInt(), b.toInt())
            val beacon = Beacon(c.toInt(), d.toInt())
            val manhattanDistance = (sensor.x - beacon.x).absoluteValue + (sensor.y - beacon.y).absoluteValue
            Pair(sensor, manhattanDistance)
        }

        //maxXY / 20

        return buildList {
            (0..maxXY).map { y ->
                val xRange = Range(0, maxXY)
                var ranges = listOf<Range>()

                sensors.forEach { (s, m) ->
                    val dif = m - (s.y - y).absoluteValue

                    if (dif >= 0) {
                        ranges = merge(ranges, Range(s.x - dif, s.x + dif))
                    }
                }

                ranges
                    .fold(listOf(xRange)) { acc, range -> acc - range }
                    .ifEmpty { null }
                    ?.let { add(Pair(y, it)) }
            }
        }
            .let {
                check(it.size == 1)
                val (y, rs) = it.first()
                check(rs.size == 1)
                val (q) = rs
                check(q.size == 1)
                q.start.toLong() * 4_000_000L + y.toLong()
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
    val input = readInput("Day15")

    check(
        part2(
            testInput,
            20
        ) == 56000011L
    )

    println(
        measureTimeMillis {
            part2(
                input,
                4_000_000
            )
        }
    )
}
