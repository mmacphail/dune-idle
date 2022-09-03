package eu.macphail

class IdDispenser {
    var internalId = 100L

    fun giveId(): Long {
        val givenId = internalId
        internalId++
        return givenId
    }
}