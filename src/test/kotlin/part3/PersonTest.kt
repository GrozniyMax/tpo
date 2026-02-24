package part3

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import tpo.maxim.part3.Chair
import tpo.maxim.part3.Company
import tpo.maxim.part3.Person
import tpo.maxim.part3.Position
import tpo.maxim.part3.Species

class PersonTest {


    @Test
    fun `проверка положения человека по умолчанию`() {
        val person = Person("Иван", Species.HUMAN)
        assertEquals(Position.STANDING, person.position())
    }

    @Test
    fun `проверка положения человека после сидения`() {
        val person = Person("Иван", Species.HUMAN)
        val chair = mockk<Chair>()
        every { chair.sit(person) } returns Unit
        person.sit(chair)

        //Проверяем позицию
        assertEquals(Position.SITTING, person.position())
        verify(exactly = 1) { chair.sit(person) }
    }

    @Test
    fun `проверка положения человека после подъема`() {
        val person = Person("Иван", Species.HUMAN)
        val chair = mockk<Chair>()
        every { chair.sit(person) } returns Unit
        every { chair.getUp(person) } returns Unit
        person.sit(chair)
        person.getUp()

        //Проверяем позицию
        assertEquals(Position.STANDING, person.position())
        verify(exactly = 1) { chair.sit(person) }
        verify(exactly = 1) { chair.getUp(person) }
    }
}