package game

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import java.awt.Color
import java.awt.Graphics2D

class SolariReserve(override val id: Long, override val transform: Transform = Transform(10.0, 50.0)) : GameNode {
    var amount = 0
    var solariToAdd = 0

    override fun update(dt: Double): UpdateResult {
        if(solariToAdd != 0) {
            amount += solariToAdd
            solariToAdd = 0
        }
        return UpdateResult.Keep
    }

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString("solari: $amount", t.x.toInt(), t.y.toInt())
        }
    }

    fun addSolaris(amount: Int) {
        solariToAdd += amount
    }

    fun amount(): Int = amount
    fun pay(harvesterCost: Int) {
        solariToAdd -= harvesterCost
    }
}