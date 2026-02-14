package tpo.maxim.part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PersonTest {

    @Test
    fun `test person creation`() {
        val person = Person("Максим", Species.HUMAN)
        assertEquals("Максим", person.name)
        assertEquals(Species.HUMAN, person.race)
    }

    @Test
    fun `test person toString`() {
        val person = Person("Максим", Species.HUMAN)
        assertEquals("HUMAN Максим", person.toString())
        
        val marsian = Person("Зог", Species.MARSIAN)
        assertEquals("MARSIAN Зог", marsian.toString())
    }
}

class CompanyTest {

    @Test
    fun `test company creation`() {
        val people = mutableListOf(
            Person("Иван", Species.HUMAN),
            Person("Петр", Species.HUMAN)
        )
        val company = Company("Космическая корпорация", people)
        assertEquals("Космическая корпорация", company.name)
        assertEquals(people, company.people)
    }

    @Test
    fun `test company toString`() {
        val people = mutableListOf(Person("Иван", Species.HUMAN))
        val company = Company("Космическая корпорация", people)
        assertEquals("Космическая корпорация", company.toString())
    }

    @Test
    fun `test company mutability`() {
        val people = mutableListOf(Person("Иван", Species.HUMAN))
        val company = Company("Космическая корпорация", people)
        
        // Test that we can modify the people list
        val newPerson = Person("Петр", Species.HUMAN)
        company.people.add(newPerson)
        
        assertEquals(2, company.people.size)
        assertTrue(company.people.contains(newPerson))
    }
}
