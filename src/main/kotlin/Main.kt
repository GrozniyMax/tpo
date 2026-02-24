package tpo.maxim

import tpo.maxim.part3.Company
import tpo.maxim.part3.Furniture
import tpo.maxim.part3.Person
import tpo.maxim.part3.Planet
import tpo.maxim.part3.Room
import tpo.maxim.part3.Species

fun main() {

}

private fun generateThey(): Company {
    val human = Person("Максим", Species.HUMAN)
    val human1 = Person("Виктор", Species.HUMAN)
    val human2 = Person("Андрей", Species.HUMAN)
    val human3 = Person("Алексей", Species.HUMAN)

    return Company("Они", mutableListOf(human, human1, human2, human3))
}