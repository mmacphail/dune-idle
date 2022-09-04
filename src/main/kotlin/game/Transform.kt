package game

class Transform(var x: Double, var y: Double){
    constructor() : this(0.0, 0.0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transform

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "Transform(x=$x, y=$y)"
    }

    fun slideRight(i: Int): Transform {
        return Transform(x + i.toDouble(), y)
    }
}