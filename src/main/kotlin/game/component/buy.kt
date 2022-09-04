package game.component

import game.node.SolariReserve

class BuyCapability(private val solariReserve: SolariReserve, private val cost: Int, val acquire: (Int) -> Unit) {
    var buyOnce: Boolean = false
    var buyTenTimes: Boolean = false
    var buyHundredTimes: Boolean = false

    fun update() {
        if (buyOnce) {
            buyQuantity(1)
            buyOnce = false
        }
        if (buyTenTimes) {
            buyQuantity(10)
            buyTenTimes = false
        }
        if(buyHundredTimes) {
            buyQuantity(100)
            buyHundredTimes = false
        }
    }

    private fun buyQuantity(amount: Int) {
        if (solariReserve.amount() >= cost * amount) {
            solariReserve.pay(cost * amount)
            acquire(amount)
        }
    }
}