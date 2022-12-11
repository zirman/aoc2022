data class Monkey(
    var items: MutableList<Long>,
    val operator: String,
    val operand: String,
    val divisibleBy: Long,
    val trueMonkey: Int,
    val falseMonkey: Int,
    var inspectedCount: Long,
)

data class Monkey2(
    var items: MutableList<List<Int>>,
    val operator: String,
    val operand: String,
    val divisibleBy: Int,
    val trueMonkey: Int,
    val falseMonkey: Int,
    var inspectedCount: Long,
)

fun main() {
    fun part1(input: List<String>): Long {
        val monkeys = input.joinToString("\n").split("\n\n").map { monkeyStr ->
            val (
                startingItemsLine,
                operationLine,
                testLine,
                trueLine,
                falseLine,
            ) = monkeyStr.split("\n").drop(1)

            val startingItems = startingItemsLine.split(": ").last().split(", ").map { it.toLong() }

            val (operator, operand) = Regex("""^old ([+*]) (old|\d+)$""")
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

        repeat(20) {
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
                    } / 3L

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

    fun part2(input: List<String>): Long {
        val m = input.joinToString("\n").split("\n\n").map { monkeyStr ->
            val (
                startingItemsLine,
                operationLine,
                testLine,
                trueLine,
                falseLine,
            ) = monkeyStr.split("\n").drop(1)

            val startingItems = startingItemsLine.split(": ").last().split(", ").map { it.toLong() }

            val (operator, operand) = Regex("""^old ([+*]) (old|\d+)$""")
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

        val divisors = m.map { it.divisibleBy.toInt() }

        val monkeys = m.map { monkey ->
            Monkey2(
                items = monkey.items.map { n -> divisors.map { d -> n.toInt() % d } }.toMutableList(),
                operator = monkey.operator,
                operand = monkey.operand,
                divisibleBy = monkey.divisibleBy.toInt(),
                trueMonkey = monkey.trueMonkey,
                falseMonkey = monkey.falseMonkey,
                inspectedCount = 0,
            )
        }

        repeat(10000) {
            monkeys.forEachIndexed { index, monkey ->
                monkey.inspectedCount += monkey.items.size

                monkey.items.forEach { item ->
                    val updatedItem = when (monkey.operator) {
                        "+" ->
                            if (monkey.operand == "old") {
                                item.mapIndexed { index, i ->
                                    (i + i) % divisors[index]
                                }
                            } else {
                                item.mapIndexed { index, i ->
                                    (i + monkey.operand.toInt()) % divisors[index]
                                }
                            }

                        "*" ->
                            if (monkey.operand == "old") {
                                item.mapIndexed { index, i ->
                                    (i * i) % divisors[index]
                                }
                            } else {
                                item.mapIndexed { index, i ->
                                    (i * monkey.operand.toInt()) % divisors[index]
                                }
                            }

                        else -> throw Exception("Invalid Operator")
                    }

                    if (updatedItem[index] == 0) {
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

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    val input = readInput("Day11")
    println(part1(input))

    check(part2(testInput) == 2713310158L)
    println(part2(input))
}
