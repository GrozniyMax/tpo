package tpo.maxim.part3

interface Furniture

class Table(val capacity: Int): Furniture {

    val items: MutableSet<Items> = mutableSetOf()

    fun addItem(item: Items) {
        if (items.size == capacity) {
            throw IllegalStateException("Стол переполнен")
        }
        if (!items.add(item)) {
            throw IllegalStateException("$item уже на столе")
        }
    }

    fun removeItem(item: Items) {
        if (!items.remove(item)) {
            throw IllegalStateException("$item не на столе")
        }
    }

    fun contains(item: Items): Boolean = items.contains(item)

}

class Chair(val material: String) : Furniture {

    private var sitter: Actor? = null

    fun sit(actor: Actor) {
        if (sitter != null) {
            throw IllegalStateException("Сидит уже кто-то")
        }
        this.sitter = actor
    }

    fun getUp(actor: Actor) {
        if (sitter != actor || sitter == null) {
            throw IllegalStateException("$actor не сидит на этом стуле")
        }
        this.sitter = null
    }

}