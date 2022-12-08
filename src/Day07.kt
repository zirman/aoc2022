import java.lang.Exception
import java.util.Stack

//typealias Foo = Map<String, Foo>
sealed interface Filesystem {
    data class Directory(val nodes: MutableMap<String, Filesystem>) : Filesystem
    data class File(val size: Long) : Filesystem
}

sealed interface Size {
    data class Directory(override val size: Long, val nodes: Map<String, Size>) : Size
    data class File(override val size: Long) : Size

    val size: Long
}

fun main() {
    fun part1(input: List<String>): Long {
        val groups = input.joinToString("\n").split(Regex("""\$ """))
            .map { it.split("\n").dropLast(1) }
            .drop(1)

        val root = Filesystem.Directory(mutableMapOf())
        val path = Stack<Filesystem.Directory>()
        path.push(root)

        groups.forEach { group ->
            val (cmd) = group
            val cdMatches = Regex("""^cd (.+)$""").matchEntire(cmd)
            val lsMatches = Regex("""^ls$""").matchEntire(cmd)
            if (cdMatches != null) {
                val (dir) = cdMatches.destructured
                if (dir == "/") {
                    while (path.size > 1) {
                        path.pop()
                    }
                } else if (dir == "..") {
                    path.pop()
                } else if (path.peek().nodes.containsKey(dir)) {
                    path.push(path.peek().nodes[dir] as Filesystem.Directory)
                } else {
                    val directory = Filesystem.Directory(mutableMapOf())
                    path.peek().nodes[dir] = directory
                    path.push(directory)
                }
            } else if (lsMatches != null) {
                val output = group.subList(1, group.size)
                output.forEach { line ->
                    val dirRegex = Regex("""^dir (.+)$""").matchEntire(line)
                    val fileRegex = Regex("""^(\d+) (.+)$""").matchEntire(line)
                    if (dirRegex != null) {
                        val (directoryName) = dirRegex.destructured
                        if (path.peek().nodes.containsKey(directoryName).not()) {
                            path.peek().nodes[directoryName] = Filesystem.Directory(mutableMapOf())
                        }
                    } else if (fileRegex != null) {
                        val (size, filename) = fileRegex.destructured
                        path.peek().nodes[filename] = Filesystem.File(size.toLong())
                    }
                }
            } else {
                throw Exception()
            }
        }

        fun directorySizes(filesystem: Filesystem): Pair<Long, List<Pair<Long, Filesystem>>> {
            return when (filesystem) {
                is Filesystem.File -> {
                    Pair(filesystem.size, listOf(Pair(filesystem.size, filesystem)))
                }

                is Filesystem.Directory -> {
                    val foo = filesystem.nodes.values.map { directorySizes(it) }
                    val size = foo.sumOf { it.first }
                    Pair(size, listOf(Pair(size, filesystem)).plus(foo.flatMap { it.second }))
                }
            }
        }

        return directorySizes(root).second
            .filter { (size, filesystem) -> filesystem is Filesystem.Directory && size <= 100000L }
            .sumOf { (size) -> size }
    }

    fun part2(input: List<String>): Long {
        val groups = input.joinToString("\n").split(Regex("""\$ """))
            .map { it.split("\n").takeWhile { it.isNotEmpty() } }
            .drop(1)

        val root = Filesystem.Directory(mutableMapOf())
        val path = Stack<Filesystem.Directory>()
        path.push(root)

        groups.forEach { group ->
            val (cmd) = group
            val cdMatches = Regex("""^cd (.+)$""").matchEntire(cmd)
            val lsMatches = Regex("""^ls$""").matchEntire(cmd)
            if (cdMatches != null) {
                val (dir) = cdMatches.destructured
                if (dir == "/") {
                    while (path.size > 1) {
                        path.pop()
                    }
                } else if (dir == "..") {
                    path.pop()
                } else if (path.peek().nodes.containsKey(dir)) {
                    path.push(path.peek().nodes[dir] as Filesystem.Directory)
                } else {
                    val directory = Filesystem.Directory(mutableMapOf())
                    path.peek().nodes[dir] = directory
                    path.push(directory)
                }
            } else if (lsMatches != null) {
                val output = group.subList(1, group.size)
                output.forEach { line ->
                    val dirRegex = Regex("""^dir (.+)$""").matchEntire(line)
                    val fileRegex = Regex("""^(\d+) (.+)$""").matchEntire(line)
                    if (dirRegex != null) {
                        val (directoryName) = dirRegex.destructured
                        if (path.peek().nodes.containsKey(directoryName).not()) {
                            path.peek().nodes[directoryName] = Filesystem.Directory(mutableMapOf())
                        }
                    } else if (fileRegex != null) {
                        val (size, filename) = fileRegex.destructured
                        path.peek().nodes[filename] = Filesystem.File(size.toLong())
                    }
                }
            } else {
                throw Exception()
            }
        }

        fun directorySizes(filesystem: Filesystem): Size {
            return when (filesystem) {
                is Filesystem.File -> {
                    Size.File(filesystem.size)
                }

                is Filesystem.Directory -> {
                    val nodes = filesystem.nodes.mapValues { (_, it) -> directorySizes(it) }
                    Size.Directory(nodes.values.sumOf { it.size }, nodes)
                }
            }
        }

        val rootSize = directorySizes(root) as Size.Directory

        println(rootSize)

        fun flattenSize(size: Size.Directory): List<Size> {
            return listOf(size).plus(size.nodes.values.filterIsInstance<Size.Directory>().flatMap { flattenSize(it) })
        }

        return flattenSize(rootSize).sortedBy { it.size }
            .first { (70000000 - rootSize.size) + it.size >= 30000000 }.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)
    val input = readInput("Day07")
    println(part1(input))

    check(part2(testInput) == 24933642L)
    println(part2(input))
}
