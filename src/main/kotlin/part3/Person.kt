package tpo.maxim.part3

enum class Position {
    STANDING,
    SITTING
}

interface Actor {

    fun appear(place: Place) {
        place.wentIn(this)
    }

    fun leave(place: Place) {
        place.wentOut(this)
    }

}

class Person(
    val name: String,
    val race: Species,
): Actor {

    private var sittingOn: Chair? = null

    fun sit(chair: Chair) {
        chair.sit(this)
        sittingOn = chair
    }

    fun getUp() {
        sittingOn?.getUp(this)
        sittingOn = null
    }

    fun position() = if (sittingOn == null) Position.STANDING else Position.SITTING

    override fun toString(): String {
        return "$race $name"
    }
}

data class Company(
    val name: String,
    val people: MutableList<Person>
): Actor {

    override fun toString(): String {
        return name
    }

}