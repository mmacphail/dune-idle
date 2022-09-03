package game

import kotlin.collections.ArrayList

interface EventListener {
    fun handleEvent(event: Event)
}

enum class EventType {
    KeyEvent,
    MouseEvent
}


interface Event {
    val type: EventType
}

data class Subscription<T : EventListener>(val id: Long, val subscriber: T, val eventType: EventType)

class EventBus {
    companion object {
        val subscriptions: ArrayList<Subscription<out EventListener>> = ArrayList()

        fun <T : EventListener> subscribe(id: Long, subscriber: T, eventType: EventType) {
            if (subscriptions.none { it.id == id && it.eventType == eventType}) {
                subscriptions.add(Subscription(id, subscriber, eventType))
            }
        }

        fun unsubscribe(id: Long) {
            val subsToRemove = subscriptions.filter { it.id == id }
            subsToRemove.forEach { subscriptions.remove(it) }
        }

        fun publish(e: Event) {
            subscriptions.filter { it.eventType == e.type }
                .forEach { it.subscriber.handleEvent(e) }
        }
    }
}