package tpo.maxim.part3


abstract class Place(
    alreadyIn: Set<Actor>,
    val name: String,
){
    private val people: MutableSet<Actor> = mutableSetOf()

    init {
        if (alreadyIn.isNotEmpty()) {
            println("Это происходило давно здесь: $name")
            alreadyIn.forEach {
                wentIn(it)
            }
            println()
        }
    }
    
    fun wentIn(subject: Actor) {
        if (!people.add(subject)) {
            throw IllegalStateException("$subject уже в $name")
        }
        reportWentIn(subject)
    }
    
    fun wentOut(subject: Actor) {
        if (!people.remove(subject)) {
            throw IllegalStateException("$subject уже покинул $name")
        }
        reportWentOut(subject)
    }

    /**
     * зашел (появился)
     */
    protected abstract fun reportWentIn(subject: Actor)

    /**
     * Вышел (покинул)
     */
    protected abstract fun reportWentOut(subject: Actor)
}

class Planet(
    name: String, alreadyIn: Set<Actor>
): Place(name = name, alreadyIn =  alreadyIn) {
    override fun reportWentIn(subject: Actor) {
        println("$subject появился на планете $name")
    }

    override fun reportWentOut(subject: Actor) {
        println("$subject покинул $name")
    }

}


class Room(
    private val funiture: List<Furniture>, alreadyIn: Set<Actor>
): Place(name = "Комната", alreadyIn =  alreadyIn) {

    override fun reportWentIn(subject: Actor) {
        println("$subject зашел в комнату")
    }

    override fun reportWentOut(subject: Actor) {
        println("$subject вышел из комнаты")
    }

    override fun toString(): String {
        val result = StringBuilder("Комната заполненная мебелью: ").let {
            funiture.forEach {
                it.name.let {
                    it -> it + ", "
                }
            }
        }

        return result.toString()
    }
}