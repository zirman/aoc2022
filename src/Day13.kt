data class Res<T>(val index: Int, val x: T)

sealed interface Exp {
    data class Num(val int: Int) : Exp
    data class Group(val items: List<Exp>) : Exp
}

fun parseToken(i: Int, ts: List<String>, t: String): Res<Unit>? {
    return when {
        i >= ts.size || ts[i] != t -> null
        else -> Res(i + 1, Unit)
    }
}

fun parseNum(i: Int, ts: List<String>): Res<Exp.Num>? {
    return when {
        i >= ts.size -> null
        else -> ts[i].toIntOrNull()?.let { Res(i + 1, Exp.Num(it)) }
    }
}

fun parseComma(i: Int, ts: List<String>): Res<Unit>? {
    return parseToken(i, ts, ",")
}

fun parseGroup(i: Int, ts: List<String>): Res<Exp>? {
    return parseToken(i, ts, "[")?.let { (i) ->
        parseInner(i, ts).let { (i, exp) ->
            parseToken(i, ts, "]")?.let { (i) ->
                Res(i, exp)
            }
        }
    }
}

fun parseInner(i: Int, ts: List<String>): Res<Exp> {
    var k = i

    val l = buildList {
        (parseNum(k, ts) ?: parseGroup(k, ts))?.let { (i, exp) ->
            k = i
            add(exp)
            while (true) {
                parseComma(k, ts)
                    ?.let { (i) ->
                        parseNum(i, ts)
                            ?.let { (i, num) ->
                                k = i
                                num
                            }
                            ?: parseGroup(i, ts)?.let { (i, group) ->
                                k = i
                                group
                            }
                    }
                    ?.let { add(it) }
                    ?: break
            }
        }
    }

    return Res(k, Exp.Group(l))
}

sealed interface Order {
    object Ascending : Order
    object Descending : Order
    object Equal : Order
}

fun compareGroups(c1: Exp.Group, c2: Exp.Group): Order {
    return when {
        c1.items.isEmpty() && c2.items.isEmpty() -> Order.Equal
        c1.items.isEmpty() -> Order.Ascending
        c2.items.isEmpty() -> Order.Descending
        else -> when (val r = compareExp(c1.items.first(), c2.items.first())) {
            Order.Equal -> compareGroups(Exp.Group(c1.items.drop(1)), Exp.Group(c2.items.drop(1)))
            else -> r
        }
    }
}

fun compareExp(e1: Exp, e2: Exp): Order {
    return when (e1) {
        is Exp.Group -> {
            when (e2) {
                is Exp.Group -> compareGroups(Exp.Group(e1.items), Exp.Group(e2.items))
                is Exp.Num -> compareExp(e1, Exp.Group(listOf(Exp.Num(e2.int))))
            }
        }

        is Exp.Num -> {
            when (e2) {
                is Exp.Group -> compareExp(Exp.Group(listOf(Exp.Num(e1.int))), e2)
                is Exp.Num -> {
                    when {
                        e1.int < e2.int -> Order.Ascending
                        e1.int > e2.int -> Order.Descending
                        else -> Order.Equal
                    }
                }
            }
        }
    }
}

fun parsePacket(line: String): Exp {
    val tokens = buildList {
        var m = """\[|]|\d+|,""".toRegex().matchAt(line, 0)
        while (m != null) {
            add(m.value)
            m = m.next()
        }
    }

    return parseGroup(0, tokens)!!.let { (i, exp) ->
        assert(i == tokens.size)
        exp
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .joinToString("\n")
            .split("\n\n")
            .mapIndexed { index, packets ->
                val (p1, p2) = packets.split("\n").map { parsePacket(it) }
                if (compareExp(p1, p2) == Order.Ascending) index + 1 else 0
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val divider1 = parsePacket("[[2]]")
        val divider2 = parsePacket("[[6]]")

        val sorted = input
            .filter { it != "" }
            .map { line -> parsePacket(line) }
            .plus(divider1)
            .plus(divider2)
            .sortedWith { a, b ->
                when (compareExp(a, b)) {
                    Order.Ascending -> -1
                    Order.Descending -> 1
                    Order.Equal -> 0
                }
            }

        return (sorted.indexOf(divider1) + 1) * (sorted.indexOf(divider2) + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    val input = readInput("Day13")
    println(part1(input))
    check(part2(testInput) == 140)
    println(part2(input))
}
