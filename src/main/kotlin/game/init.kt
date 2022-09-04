package game

import game.node.*

class Init {
    companion object {
        fun init() {
            Game.spawn { id -> Cheat(id) }
            Game.spawn { id -> SpiceReserve(id) }
            Game.spawn { id -> SpiceEquipmentHeader(id) }
            Game.spawn { id -> SpiceHarvesters(id) }
            Game.spawn { id -> SpiceSilos(id) }
            Game.spawn { id -> SolariReserve(id) }
            Game.spawn { id -> SpiceExchangeRate(id) }
        }
    }
}