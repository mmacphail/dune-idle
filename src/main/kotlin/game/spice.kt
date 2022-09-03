package game

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import kotlin.math.round

class SpiceSilos(override val id: Long, override val transform: Transform = Transform(10.0, 170.0)) : GameNode,
    Bounded, EventListener {

    var siloCapacity = 100
    var silos = 1
    var siloCost = 25
    var bound = Rectangle(transform.x.toInt(), transform.y.toInt() + 30, 150, 30)
    var buySilo = false
    lateinit var solariReserve: SolariReserve

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
            g.fillRect(bound.x, bound.y, bound.width, bound.height)
            color = Color.WHITE
            drawString("Buy spice silo", bound.x + 20, bound.y + 20)
        }
    }

    override fun size(): Rectangle {
        return bound
    }

    override fun onSpawn() {
        EventBus.subscribe(id, this, EventType.MouseEvent)
    }

    override fun onDespawn() {
        EventBus.unsubscribe(id)
    }

    fun spiceCapacity(): Int {
        return silos * siloCapacity
    }

    override fun handleEvent(event: Event) {
        if (event.type == EventType.MouseEvent) {
            if (size().contains((event as MyMouseEvent).point)) {
                buySilo = true
            }
        }
    }

    override fun onReady() {
        solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
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

class SpiceHarvesters(override val id: Long, override val transform: Transform = Transform(10.0, 110.0)) : GameNode,
    Bounded, EventListener {
    var harvesters = 10
    var harvesterCost = 100
    var buyHarvester = false
    lateinit var solariReserve: SolariReserve

    override fun update(dt: Double): UpdateResult {
        if (buyHarvester) {
            if (solariReserve.amount() >= harvesterCost) {
                solariReserve.pay(harvesterCost)
                harvesters += 1
            }
            buyHarvester = false
        }
        return UpdateResult.Keep
    }

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            drawString("Spice harvesters: $harvesters (cost: $harvesterCost Solari, +1 Sps)",
                t.x.toInt(), t.y.toInt() + 20)
            g.fillRect(t.x.toInt(), t.y.toInt() + 30, 150, 30)
            color = Color.WHITE
            drawString("Buy spice harvester", t.x.toInt() + 20, t.y.toInt() + 50)
        }
    }

    override fun size(): Rectangle {
        return Rectangle(transform.x.toInt(), transform.y.toInt() + 30, 150, 30)
    }

    override fun handleEvent(event: Event) {
        if (event.type == EventType.MouseEvent) {
            if (size().contains((event as MyMouseEvent).point)) {
                buyHarvester = true
            }
        }
    }

    override fun onSpawn() {
        EventBus.subscribe(id, this, EventType.MouseEvent)
    }

    override fun onDespawn() {
        EventBus.unsubscribe(id)
    }

    fun tick(): Int {
        return harvesters
    }

    override fun onReady() {
        solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
    }
}

class SpiceReserve(override val id: Long, override val transform: Transform = Transform(10.0, 30.0)) : GameNode,
    Bounded, EventListener {
    var amount = 0
    var accumulatedTime = 0.0
    private var sellReserve = false
    var spiceCapacity = 100

    lateinit var spiceHarvesters: SpiceHarvesters
    lateinit var spiceSilos: SpiceSilos
    lateinit var spiceExchangeRate: SpiceExchangeRate
    lateinit var solariReserve: SolariReserve

    override fun update(dt: Double): UpdateResult {
        accumulatedTime += dt
        spiceCapacity = spiceSilos.spiceCapacity()
        if (accumulatedTime >= 1.0) {
            collectSpice(spiceHarvesters)
        }
        if (sellReserve) {
            sellAllSpice()
        }
        return UpdateResult.Keep
    }

    private fun collectSpice(spiceHarvesters: SpiceHarvesters) {
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
            drawString("spice: $amount / $spiceCapacity (worth ${spiceExchangeRate.spiceToSolari(amount)} Solari)", t.x
                .toInt(), t.y
                .toInt())
            g.fillRect(t.x.toInt(), t.y.toInt() + 30, 150, 30)
            color = Color.WHITE
            drawString("Sell spice reserve", t.x.toInt() + 20, t.y.toInt() + 50)
        }
    }

    override fun size(): Rectangle {
        return Rectangle(transform.x.toInt(), transform.y.toInt() + 30, 150, 30)
    }

    override fun onSpawn() {
        EventBus.subscribe(id, this, EventType.MouseEvent)
    }

    override fun onDespawn() {
        EventBus.unsubscribe(id)
    }

    override fun handleEvent(event: Event) {
        if (event.type == EventType.MouseEvent) {
            if (size().contains((event as MyMouseEvent).point)) {
                sellReserve = true
            }
        }
    }

    override fun onReady() {
        spiceHarvesters = Game.seekEntity { it is SpiceHarvesters }[0] as SpiceHarvesters
        spiceSilos = Game.seekEntity { it is SpiceSilos }[0] as SpiceSilos
        spiceExchangeRate = Game.seekEntity { it is SpiceExchangeRate }[0] as SpiceExchangeRate
        solariReserve = Game.seekEntity { it is SolariReserve }[0] as SolariReserve
    }
}