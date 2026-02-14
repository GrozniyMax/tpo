package tpo.maxim

import tpo.maxim.part3.Company
import tpo.maxim.part3.Furniture
import tpo.maxim.part3.Person
import tpo.maxim.part3.Planet
import tpo.maxim.part3.Room
import tpo.maxim.part3.Species

fun main() {
    val table = Furniture("Стол", listOf("Стеклянный", "с наградами"))
    val chair = Furniture("Стул", listOf("Обитый плюшем"))

    val they = generateThey()

    val marsian = Person("Никита", Species.MARSIAN)

    val room = Room(alreadyIn = setOf(marsian), funiture = listOf(table, chair))

    val planet = Planet("Планета каталога", alreadyIn = setOf(they))

    they.leave(planet)
    they.appear(room)

}

private fun generateThey(): Company {
    val human = Person("Максим", Species.HUMAN)
    val human1 = Person("Виктор", Species.HUMAN)
    val human2 = Person("Андрей", Species.HUMAN)
    val human3 = Person("Алексей", Species.HUMAN)

    return Company("Они", mutableListOf(human, human1, human2, human3))
}