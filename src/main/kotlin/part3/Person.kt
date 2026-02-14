package tpo.maxim.part3


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