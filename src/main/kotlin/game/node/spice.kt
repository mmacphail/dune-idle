package game.node

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import game.Game
import game.Transform
import game.component.Button
import game.component.Equipment
import game.component.Gui
import java.awt.Color
import java.awt.Graphics2D

class SpiceSilos(override val id: Long, override val transform: Transform = Transform(10.0, 170.0)) : GameNode {
    val title = "Spice silos"
    val label = "silo"
    val effect = "100 spice capacity"
    val cost = 25
    lateinit var equipment: Equipment

    var capacity = 100

    fun spiceCapacity(): Int {
        return equipment.units * capacity
    }

    override fun onReady() {
        equipment = Game.spawn { id -> Equipment(id, transform, cost, title, label, effect) }
    }
}

class SpiceEquipmentHeader(override val id: Long, override val transform: Transform = Transform(10.0, 110.0)) : GameNode {

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString("Spice equipment:", t.x.toInt(), t.y.toInt())
        }
    }

}

class SpiceHarvesters(override val id: Long, override val transform: Transform = Transform(10.0, 110.0)) : GameNode {
    val title = "Spice Harvesters"
    val label = "harvester"
    val effect = "+1 Sps"
    val cost = 100
    lateinit var equipment: Equipment

    fun tick(): Int {
        return equipment.units
    }

    fun projectedTick(): Int {
        return equipment.units
    }

    override fun onReady() {
        equipment = Game.spawn { id -> Equipment(id, transform, cost, title, label, effect) }
    }
}

class SpiceReserve(override val id: Long, override val transform: Transform = Transform(10.0, 30.0)) : GameNode {
    var amount = 0
    var accumulatedTime = 0.0
    private var sellReserve = false
    var spiceCapacity = 100

    lateinit var spiceHarvesters: SpiceHarvesters
    lateinit var spiceSilos: SpiceSilos
    lateinit var spiceExchangeRate: SpiceExchangeRate
    lateinit var solariReserve: SolariReserve
    lateinit var sellReserveButton: Button

    override fun update(dt: Double): UpdateResult {
        accumulatedTime += dt
        spiceCapacity = spiceSilos.spiceCapacity()
        if (accumulatedTime >= 1.0) {
            collectSpice()
        }
        if (sellReserve) {
            sellAllSpice()
        }
        return UpdateResult.Keep
    }

    private fun spicePerSeconds(): Int {
        return spiceHarvesters.projectedTick()
    }

    private fun collectSpice() {
        amount += spiceHarvesters.tick()
        amount = spiceCapacity.coerceAtMost(amount)
        accumulatedTime = 0.0
    }

    private fun sellAllSpice() {
        val reserve = amount
        amount = 0
        sellReserve = false
        solariReserve.addSolaris(spiceExchangeRate.spiceToSolari(reserve))
    }

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString(
                "spice: $amount / $spiceCapacity (${spicePerSeconds()} Sps, worth ${
                    spiceExchangeRate.spiceToSolari
                        (amount)
                } Solari)",
                t.x
                    .toInt(), t.y
                    .toInt()
            )
        }
    }

    override fun onReady() {
        spiceHarvesters = Game.seekEntity { it is SpiceHarvesters }[0] as SpiceHarvesters
        spiceSilos = Game.seekEntity { it is SpiceSilos }[0] as SpiceSilos
        spiceExchangeRate = Game.seekEntity { it is SpiceExchangeRate }[0] as SpiceExchangeRate
        solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
        sellReserveButton = Gui.makeButton(transform, "Sell spice reserve") { sellReserve = true }
    }
}