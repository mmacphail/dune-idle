package game

import eu.macphail.GameNode
import java.awt.Point
import java.awt.Rectangle

interface Bounded {
    fun size(): Rectangle
}

class Collisions {
    companion object {
        fun update(p: Point): GameNode? {
            val boundedEntities = Game.seekEntity { it is Bounded }
            boundedEntities.forEach {
                println(it.javaClass)
            }
            return null
        }
    }
}