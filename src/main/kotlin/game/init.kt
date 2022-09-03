package game

class Init {
    companion object {
        fun init() {
            Game.spawn { id -> SpiceReserve(id) }
            Game.spawn { id -> SpiceEquipmentHeader(id) }
            Game.spawn { id -> SpiceHarvesters(id) }
            Game.spawn { id -> SpiceSilos(id) }
            Game.spawn { id -> SolariReserve(id) }
            Game.spawn { id -> SpiceExchangeRate(id) }
        }
    }
}