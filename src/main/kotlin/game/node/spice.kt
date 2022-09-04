package game.node

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import game.Button
import game.Game
import game.Gui
import game.Transform
import java.awt.Color
import java.awt.Graphics2D

class SpiceSilos(override val id: Long, override val transform: Transform = Transform(10.0, 170.0)) : GameNode {
    var siloCapacity = 100
    var silos = 1
    var siloCost = 25
    var buySilo = false
    lateinit var solariReserve: SolariReserve
    lateinit var buySiloButton: Button

    override fun update(dt: Double): UpdateResult {
        if(buySilo) {
            if (solariReserve.amount() >= siloCost) {
                solariReserve.pay(siloCost)
                silos += 1
            }
            buySilo = false
        }
        return UpdateResult.Keep
    }

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString("Spice silos: $silos (cost: $siloCost Solari, 100 spice capacity)", t.x.toInt(), t.y.toInt() + 20)
        }
    }

    fun spiceCapacity(): Int {
        return silos * siloCapacity
    }

    override fun onReady() {
        solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
        buySiloButton = Gui.makeButton(Transform(transform.x, transform.y), "Buy spice silo") { buySilo = true }
    }
}

class SpiceEquipmentHeader(override val id: Long, override val transform: Transform = Transform(10.0, 110.0)) : GameNode {
    override fun update(dt: Double): UpdateResult = UpdateResult.Keep

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString("Spice equipment:", t.x.toInt(), t.y.toInt())
        }
    }

}

class SpiceHarvesters(override val id: Long, override val transform: Transform = Transform(10.0, 110.0)) : GameNode {
    var harvesters = 10
    var harvesterCost = 100
    var buyHarvester = false
    var buy10Harvesters = false
    var buy100Harvesters = false
    lateinit var solariReserve: SolariReserve
    lateinit var buyHarvesterButton: Button
    lateinit var buy10HarvesterButton: Button
    lateinit var buy100HarvesterButton: Button

    override fun update(dt: Double): UpdateResult {
        if (buyHarvester) {
            buyHarvesters(1)
            buyHarvester = false
        }
        if (buy10Harvesters) {
            buyHarvesters(10)
            buyHarvester = false
        }
        if (buy100Harvesters) {
            buyHarvesters(100)
            buyHarvester = false
        }
        return UpdateResult.Keep
    }

    private fun buyHarvesters(amount: Int) {
        if (solariReserve.amount() >= harvesterCost * amount) {
            solariReserve.pay(harvesterCost * amount)
            harvesters += amount
        }
    }

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString("Spice harvesters: $harvesters (cost: $harvesterCost Solari, +1 Sps)",
                t.x.toInt(), t.y.toInt() + 20)
        }
    }

    fun tick(): Int {
        return harvesters
    }

    fun projectedTick(): Int {
        return harvesters
    }

    override fun onReady() {
        solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
        buyHarvesterButton = Gui.makeButton(transform, "Buy harvester") { buyHarvester = true }
        buy10HarvesterButton = Gui.makeButton(transform.slideRight(160), "Buy 10 harvesters") { buy10Harvesters = true }
        buy100HarvesterButton = Gui.makeButton(transform.slideRight(320), "Buy 100 harvesters") { buy100Harvesters = true }
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
            drawString("spice: $amount / $spiceCapacity (${spicePerSeconds()} Sps, worth ${spiceExchangeRate.spiceToSolari
                (amount)} Solari)",
                t.x
                .toInt(), t.y
                .toInt())
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