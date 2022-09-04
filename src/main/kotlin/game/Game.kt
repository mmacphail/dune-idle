package game

import eu.macphail.GameNode
import eu.macphail.IdDispenser
import eu.macphail.UpdateResult
import java.awt.Graphics2D

class Game {
    companion object Game {
        val idDispenser: IdDispenser = IdDispenser()
        val nodes = ArrayList<GameNode>()

        val nodesToSpawn = ArrayList<GameNode>()
        val nodesToDestroy = ArrayList<GameNode>()

        fun <T : GameNode> spawn(f: (Long) -> T): T {
            val node = f(idDispenser.giveId())
            node.onSpawn()
            nodesToSpawn.add(node)
            return node
        }

        fun despawn(node: GameNode) {
            node.onDespawn()
            nodesToDestroy.add(node)
        }

        fun updateEntities(dt: Double) {
            nodes.forEach {
                val updateResult = it.update(dt)
                if(updateResult != UpdateResult.Keep) {
                    despawn(it)
                }
            }

            val localNodesToSpawn = nodesToSpawn.toMutableList()
            nodesToSpawn.clear()
            localNodesToSpawn.forEach { nodes.add(it) }
            localNodesToSpawn.forEach { it.onReady() }

            nodesToDestroy.forEach { nodes.remove(it) }
            nodesToDestroy.clear()
        }

        fun seekEntity(p: (GameNode) -> Boolean): List<GameNode> = nodes.filter { p(it) }

        fun drawEntities(g : Graphics2D) {
            nodes.forEach { it.draw(g) }
        }
    }
}