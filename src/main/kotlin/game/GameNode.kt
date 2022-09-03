package eu.macphail

import game.Transform
import java.awt.Graphics2D

enum class UpdateResult {
    Keep, Dispose
}

interface GameNode {
    val id: Long
    val transform: Transform

    fun update(dt: Double): UpdateResult

    fun draw(g: Graphics2D)

    fun onSpawn(): Unit {}

    fun onDespawn(): Unit {}

    fun onReady(): Unit {}
}
