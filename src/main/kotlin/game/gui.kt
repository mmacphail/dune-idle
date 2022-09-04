package game

import eu.macphail.GameNode
import eu.macphail.UpdateResult
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle

class Button(
    override val id: Long,
    override val transform: Transform,
    private val text: String,
    private val onClick: () -> Unit) : GameNode, EventListener, Bounded {

    var bound = Rectangle(transform.x.toInt(), transform.y.toInt() + 30, 150, 30)

    override fun update(dt: Double): UpdateResult = UpdateResult.Keep

    override fun draw(g: Graphics2D) {
        val t = transform
        with(g) {
            color = Color.BLACK
            g.fillRect(bound.x, bound.y, bound.width, bound.height)
            color = Color.WHITE
            drawString(text, bound.x + 20, bound.y + 20)
        }
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
                onClick()
            }
        }
    }

    override fun size(): Rectangle {
        return bound
    }
}