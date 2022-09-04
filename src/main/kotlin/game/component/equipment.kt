package game.component

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import game.Game
import game.Transform
import game.node.SolariReserve
import java.awt.Color
import java.awt.Graphics2D

class Equipment(
    override val id: Long,
    override val transform: Transform, val cost: Int, val title: String,
    val unitLabel: String, val effects: String
) : GameNode {
    var units = 0

    lateinit var buyCapability: BuyCapability
    lateinit var buyButtons: BuyButtons

    override fun update(dt: Double): UpdateResult {
        buyCapability.update()
        return UpdateResult.Keep
    }

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString(
                "$title: $units (cost: $cost, $effects)",
                t.x.toInt(), t.y.toInt() + 20
            )
        }
    }

    override fun onReady() {
        val solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
        buyCapability = BuyCapability(solariReserve, cost) { units += it }
        buyButtons = BuyButtons(transform, unitLabel, buyCapability)
    }
}