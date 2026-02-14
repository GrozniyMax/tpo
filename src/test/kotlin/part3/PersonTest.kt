package part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import tpo.maxim.part3.Company
import tpo.maxim.part3.Person
import tpo.maxim.part3.Species

class PersonTest {

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
    fun `test company toString`() {
        val people = mutableListOf(Person("Иван", Species.HUMAN))
        val company = Company("Космическая корпорация", people)
        assertEquals("Космическая корпорация", company.toString())
    }

}
