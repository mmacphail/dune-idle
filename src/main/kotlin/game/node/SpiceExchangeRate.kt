package game.node

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import game.Bounded
import game.Transform
import java.awt.*
import java.awt.geom.AffineTransform
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round

class SpiceExchangeRate(override val id: Long, override val transform: Transform = Transform(10.0, 250.0)) : GameNode,
    Bounded {
    private val random = Random()
    private val numberOfDots = 11
    private val width = 250
    private val height = 250
    private val lowerBound = 30
    private val upperBound = 200
    var numberOfSecondsBeforeNextUpdate = 5
    var accumulatedTime = 0.0
    var values = (1..11).map { _ -> 100 }

    private enum class MarketNextMove(val modifier: Int) {
        Crash(-40),
        Boom(+20),
        SlowIncrease(+5),
        SlowDecrease(-5),
        HighIncrease(+10),
        HighDecrease(-10)
    }

    private fun spiceMarketBehavior(): Map<IntRange, MarketNextMove> = mapOf(
        (0..5) to MarketNextMove.Crash,
        (6..10) to MarketNextMove.Boom,
        (11..40) to MarketNextMove.SlowIncrease,
        (41..60) to MarketNextMove.SlowDecrease,
        (61..80) to MarketNextMove.HighIncrease,
        (81..99) to MarketNextMove.HighDecrease
    )

    private fun calculateNextMarketRate(currentValue: Int): Int {
        val i = random.nextInt(0, 100)
        val modifier = spiceMarketBehavior().firstNotNullOf { (range, move) -> if (i in range) move.modifier else null }
        val newValue = currentValue + modifier
        return newValue.coerceAtMost(upperBound).coerceAtLeast(lowerBound)
    }

    private fun valueToGraphY(v: Int): Int {
        return abs(round(height / (upperBound / v.toDouble())) - height).toInt() - height / 2
    }

    override fun update(dt: Double): UpdateResult {
        accumulatedTime += dt
        if (accumulatedTime >= 5.0) {
            values = values.drop(1) + calculateNextMarketRate(currentRate())
            accumulatedTime = 0.0
            numberOfSecondsBeforeNextUpdate = 5
        } else {
            numberOfSecondsBeforeNextUpdate = 5 - floor(accumulatedTime).toInt()
        }
        return UpdateResult.Keep
    }

    fun currentRate(): Int = values.last().toInt()

    fun spiceToSolari(spice: Int): Int = round(spice * currentRate().toDouble() / 100).toInt()

    override fun draw(g: Graphics2D) {
        val t = transform
        val g2d = g.create() as Graphics2D
        with(g2d) {
            val rect = Point(t.x.toInt() + 50, t.y.toInt() + 15)
            drawString("Spice Exchange Rate", t.x.toInt(), t.y.toInt())
            drawRect(rect.x, rect.y, width, height)
            val affineTransform = AffineTransform()
            affineTransform.rotate(Math.toRadians(-90.0), 0.0, 0.0)
            val font = Font(null, Font.PLAIN, 10)
            val rotatedFont = font.deriveFont(affineTransform)
            setFont(rotatedFont)
            setFont(Font(null, Font.PLAIN, 10))
            drawString("Time", t.x.toInt() + 160, t.y.toInt() + 280)
            fillOval(rect.x, rect.y, 5, 5)
            fillOval(rect.x + width - 5, rect.y + height - 5, 5, 5)
            fillOval(rect.x + width - 5, rect.y, 5, 5)
            fillOval(rect.x, rect.y + height - 5, 5, 5)

            val currentValueY = rect.y + height / 2 + valueToGraphY(values.last())
            color = Color.RED
            drawLine(rect.x, currentValueY, rect.x + width, currentValueY)
            drawString("${currentRate().toDouble() / 100}", rect.x - 25, currentValueY + 3)
            drawString("Update in $numberOfSecondsBeforeNextUpdate secs", rect.x + 85, rect.y + height + 35)
            color = Color.BLACK

            values.forEachIndexed { i, value ->
                val x = rect.x + i * width / (numberOfDots - 1)
                val y = rect.y + height / 2 + valueToGraphY(value)

                val nextX = rect.x + (i + 1) * width / (numberOfDots - 1)
                val nextY = rect.y + height / 2 + (try {
                    valueToGraphY(values[i + 1])
                } catch (e: IndexOutOfBoundsException) {
                    0
                })

                if (!(i == 0 || i == values.size - 1)) {
                    fillOval(x, y, 1, 1)
                }
                if (i != values.size - 1) {
                    drawLine(x, y, nextX, nextY)
                }
            }

        }
        g2d.dispose()
    }

    override fun size(): Rectangle =
        Rectangle(transform.x.toInt(), transform.y.toInt(), width, height)

    override fun onReady() {
        this.values = (1..11).fold(listOf<Int>()) { acc, _ ->
            if(acc.isEmpty()) {
                acc + calculateNextMarketRate(100)
            } else {
                acc + calculateNextMarketRate(acc.last())
            }
        }
    }
}