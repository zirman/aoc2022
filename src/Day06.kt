fun main() {
    fun part(input: List<String>, startOfPacketSize: Int): Int {
        return input[0].windowed(startOfPacketSize, 1)
            .indexOfFirst { it.toSet().size == startOfPacketSize } + startOfPacketSize
    }

    fun part1(input: List<String>): Int {
        return part(input, 4)
    }

    fun part2(input: List<String>): Int {
        return part(input, 14)
    }

    // test if implementation meets criteria from the description, like:
    check(part1(listOf("bvwbjplbgvbhsrlpgdmjqwftvncz")) == 5)
    check(part1(listOf("nppdvjthqldpwncqszvftbrmjlhg")) == 6)
    check(part1(listOf("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")) == 10)
    check(part1(listOf("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")) == 11)
    val input = readInput("Day06")
    println(part1(input))
    check(part2(listOf("bvwbjplbgvbhsrlpgdmjqwftvncz")) == 23)
    check(part2(listOf("nppdvjthqldpwncqszvftbrmjlhg")) == 23)
    check(part2(listOf("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")) == 29)
    check(part2(listOf("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")) == 26)
    println(part2(input))
}
