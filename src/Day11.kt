import kotlin.system.measureTimeMillis

data class Monkey(
    var items: MutableList<Long>,
    val operator: String,
    val operand: String,
    val divisibleBy: Long,
    val trueMonkey: Int,
    val falseMonkey: Int,
    var inspectedCount: Long,
)

fun main() {
    fun parseMonkeys(input: List<String>): List<Monkey> {
        return input
            .joinToString("\n")
            .split("\n\n")
            .map { monkeyStr ->
                val (
                    startingItemsLine,
                    operationLine,
                    testLine,
                    trueLine,
                    falseLine,
                ) = monkeyStr.split("\n").drop(1)

                val startingItems = startingItemsLine.split(": ").last().split(", ").map { it.toLong() }

                val (operator, operand) = """^old ([+*]) (old|\d+)$""".toRegex()
                    .matchEntire(operationLine.split(" = ").last())!!
                    .destructured

                val divisibleBy = testLine.split(" by ").last().toLong()
                val trueMonkey = trueLine.split("monkey ").last().toInt()
                val falseMonkey = falseLine.split("monkey ").last().toInt()

                Monkey(
                    items = startingItems.toMutableList(),
                    operator = operator,
                    operand = operand,
                    divisibleBy = divisibleBy,
                    trueMonkey = trueMonkey,
                    falseMonkey = falseMonkey,
                    inspectedCount = 0,
                )
            }
    }

    fun iterateMonkeys(monkeys: List<Monkey>, times: Int, reducer: (Long) -> Long): Long {
        repeat(times) {
            monkeys.forEach { monkey ->
                monkey.inspectedCount += monkey.items.size

                monkey.items.forEach { item ->
                    val updatedItem = when (monkey.operator) {
                        "+" ->
                            if (monkey.operand == "old") {
                                item + item
                            } else {
                                item + monkey.operand.toLong()
                            }

                        "*" ->
                            if (monkey.operand == "old") {
                                item * item
                            } else {
                                item * monkey.operand.toLong()
                            }

                        else -> throw Exception("Invalid Operator")
                    }.let { reducer(it) }

                    if (updatedItem % monkey.divisibleBy == 0L) {
                        monkeys[monkey.trueMonkey].items.add(updatedItem)
                    } else {
                        monkeys[monkey.falseMonkey].items.add(updatedItem)
                    }
                }

                monkey.items = mutableListOf()
            }
        }

        return monkeys.map { it.inspectedCount }
            .sortedDescending()
            .take(2)
            .reduce { a, b -> a * b }
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        return iterateMonkeys(monkeys, times = 20, reducer = { it / 3 })
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        val divisor = monkeys.map { it.divisibleBy }.reduce(Long::times)
        return iterateMonkeys(monkeys, times = 10000, reducer = { it % divisor })
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    val input = readInput("Day11")
    println(part1(input))
    println(measureTimeMillis { part2(input) })
    check(part2(testInput) == 2713310158L)
    println(part2(input))
}
