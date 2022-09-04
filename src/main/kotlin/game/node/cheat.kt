package game.node

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import game.Game
import game.Transform
import java.awt.Graphics2D

class Cheat(override val id: Long, override val transform: Transform = Transform()) : GameNode {
    override fun update(dt: Double): UpdateResult {
        return UpdateResult.Keep
    }

    override fun draw(g: Graphics2D) {
    }

    override fun onReady() {
        val solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
        solariReserve.amount = 100000
    }
}