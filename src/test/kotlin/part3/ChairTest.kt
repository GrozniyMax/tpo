package part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import tpo.maxim.part3.Chair
import tpo.maxim.part3.Person
import tpo.maxim.part3.Position
import tpo.maxim.part3.Species

class ChairTest {

    lateinit var person1: Person;

    lateinit var person2: Person;

    @BeforeEach
    fun setUp() {
        person1 = Person("Иван", Species.HUMAN)
        person2 = Person("Петр", Species.HUMAN)
    }

    @Test
    fun `посадка на стул`() {
        val chair = Chair("пластик")
        assertDoesNotThrow { chair.sit(person1) }
    }

    @Test
    fun `попытка посадки когда кто-то уже сидит`() {
        val chair = Chair("металл")

        person1.sit(chair)

        val exception = assertThrows(IllegalStateException::class.java) {
            chair.sit(person2)
        }
        assertEquals("Сидит уже кто-то", exception.message)
    }

    @Test
    fun `вставание со стула`() {
        val chair = Chair("ткань")

        person1.sit(chair)

        assertDoesNotThrow { person1.getUp() }
    }

    @Test
    fun `попытка вставания другого человека`() {
        val chair = Chair("кожа")

        person1.sit(chair)

        assertThrows(IllegalStateException::class.java) {
            chair.getUp(person2)
        }
    }

    @Test
    fun `Вставание с пустого стула`() {
        val chair = Chair("кожа")

        assertThrows(IllegalStateException::class.java) {
            chair.getUp(person1)
        }
    }

    @Test
    fun `попытка вставания с пустого стула`() {
        val chair = Chair("резина")

        val exception = assertThrows(IllegalStateException::class.java) {
            chair.getUp(person1)
        }
    }
}
