package com.k0syach0k.astar

import java.util.*
import kotlin.math.abs
import kotlin.math.min

data class Node(
    val x: Int,
    val y: Int,
    val cost: Int,
    val previousNode: Node?
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Node) return false
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        return 10000 * x + y
    }
}

class PathFinder(private val map: Array<IntArray>,
                 private val startNode: Node,
                 private val finishNode: Node) {


    private val reachable = hashSetOf<Node>()
    private val explored = hashSetOf<Node>()

    fun findPath(): String {
        reachable.add(startNode)

        while (reachable.isNotEmpty()) {
            val node = chooseNode()

            if (node == finishNode) return buildPath(node)

            reachable.remove(node)
            explored.add(node)
            val newSuccessors =  generateSuccessors(node) - explored
            reachable.addAll(newSuccessors)
        }

        return "-1"
    }

    private fun buildPath(node: Node): String {
        val path = mutableListOf<Char>()
        var currentNode: Node? = node
        val maxX = map[0].size - 1
        val maxY = map.size - 1

        while (currentNode?.previousNode != null) {
            if (currentNode.previousNode!!.x == currentNode.x){
                when(currentNode.previousNode!!.y - currentNode.y){
                    1, -maxY -> path.add('N')
                    -1, maxY -> path.add('S')
                }
            }

            if (currentNode.previousNode!!.y == currentNode.y){
                when(currentNode.previousNode!!.x - currentNode.x){
                    1, -maxX -> path.add('W')
                    -1, maxX -> path.add('E')
                }
            }

            currentNode = currentNode.previousNode
        }

        path.reverse()

        return String(path.toCharArray())
    }

    private fun chooseNode(): Node {
        var minCost = Int.MAX_VALUE
        var bestNode: Node? = null

        for (node in reachable) {
            val totalCost = node.cost + estimateCostToFinish(node.x, node.y)

            if (minCost > totalCost) {
                minCost = totalCost
                bestNode = node
            }
        }
        return bestNode!!
    }

    private fun estimateCostToFinish(x: Int, y: Int): Int {
        val finishMinX = min(abs(x - finishNode.x), abs(map[0].size - abs(x - finishNode.x)))
        val finishMinY = min(abs(y - finishNode.y), abs(map.size - abs(y - finishNode.y)))

        return finishMinX + finishMinY
    }

    private fun generateSuccessors(node: Node): HashSet<Node> {
        val successorSet = hashSetOf<Node>()

        val x = node.x
        val y = node.y

        val maxX = map[0].size - 1
        val maxY = map.size - 1

        if (y < maxY && map[y + 1][x] == 0) successorSet.add(Node(x, y + 1, node.cost + 1, node))
        if (y > 0 && map[y - 1][x] == 0) successorSet.add(Node(x, y - 1, node.cost + 1, node))
        if (y == 0 && map[maxY][x] == 0) successorSet.add(Node(x, maxY, node.cost + 1, node))
        if (y == maxY && map[0][x] == 0) successorSet.add(Node(x, 0, node.cost + 1, node))

        if (x < maxX && map[y][x + 1] == 0) successorSet.add(Node(x + 1, y, node.cost + 1, node))
        if (x > 0 && map[y][x - 1] == 0) successorSet.add(Node(x - 1, y, node.cost + 1, node))
        if (x == 0 && map[y][maxX] == 0) successorSet.add(Node(maxX, y, node.cost + 1, node))
        if (x == maxX && map[y][0] == 0) successorSet.add(Node(0, y, node.cost + 1, node))

        return successorSet
    }
}

fun main() {

    val scanner = Scanner(System.`in`)

    val h = scanner.nextInt()
    val w = scanner.nextInt()

    val startH = scanner.nextInt()
    val startW = scanner.nextInt()

    val finishH = scanner.nextInt()
    val finishW = scanner.nextInt()

    val map = Array(h) { IntArray(w) }

    for (i in 0 until h)
        for (j in 0 until w)
            map[i][j] = scanner.nextInt()

    val pf = PathFinder(
        map,
        Node(startW, startH, 0, null),
        Node(finishW, finishH, Int.MAX_VALUE, null)
    )

    println(pf.findPath())
}