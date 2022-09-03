package game

import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

data class MyKeyEvent(val key: Key, val keyEventType: KeyEventType): Event {
    override val type: EventType = EventType.KeyEvent
}

data class MyMouseEvent(val point: Point): Event {
    override val type: EventType = EventType.MouseEvent
}

enum class KeyEventType {
    Pressed, Released
}

enum class Key {
    Left,
    Right,
    Up,
    Down,
    Firing
}

fun findKey(e: KeyEvent) = when (e.keyCode) {
    KeyEvent.VK_LEFT -> Key.Left
    KeyEvent.VK_RIGHT -> Key.Right
    KeyEvent.VK_UP -> Key.Up
    KeyEvent.VK_DOWN -> Key.Down
    KeyEvent.VK_W -> Key.Firing
    else -> null
}

class Inputs {
    companion object Inputs {
        fun keyPressed(e: KeyEvent) {
            val key = findKey(e)

            if(key != null) {
                EventBus.publish(MyKeyEvent(key, KeyEventType.Pressed))
            }
        }


        fun keyReleased(e: KeyEvent) {
            val key = findKey(e)

            if(key != null) {
                EventBus.publish(MyKeyEvent(key, KeyEventType.Released))
            }
        }

        fun mousePressed(e: MouseEvent) {
            EventBus.publish(MyMouseEvent(Point(e.x, e.y)))
        }
    }
}