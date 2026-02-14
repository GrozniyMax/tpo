package tpo.maxim.part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class PlaceTest {

    private lateinit var person: Person
    private lateinit var planet: Planet
    private lateinit var room: Room

    @BeforeEach
    fun setUp() {
        person = Person("Максим", Species.HUMAN)
        planet = Planet("Марс", emptySet())
        room = Room(emptyList(), emptySet())
    }

    @Test
    fun `test planet creation`() {
        val planet = Planet("Земля", emptySet())
        assertEquals("Земля", planet.name)
    }

    @Test
    fun `test room creation`() {
        val furniture = listOf(Furniture("Стол"), Furniture("Стул"))
        val room = Room(furniture, emptySet())
        assertEquals("Комната", room.name)
    }

    @Test
    fun `test actor entering place`() {
        // This test checks state change when actor enters a place
        // We can't easily assert on private state, but we can test the behavior
        assertDoesNotThrow {
            planet.wentIn(person)
        }
    }

    @Test
    fun `test actor leaving place`() {
        // First enter, then leave
        planet.wentIn(person)
        assertDoesNotThrow {
            planet.wentOut(person)
        }
    }

    @Test
    fun `test duplicate entry throws exception`() {
        planet.wentIn(person)
        val exception = assertThrows<IllegalStateException> {
            planet.wentIn(person)
        }
        assertTrue(exception.message!!.contains("уже в"))
    }

    @Test
    fun `test leaving when not present throws exception`() {
        val exception = assertThrows<IllegalStateException> {
            planet.wentOut(person)
        }
        assertTrue(exception.message!!.contains("уже покинул"))
    }

    @Test
    fun `test room toString`() {
        val furniture = listOf(Furniture("Стол"), Furniture("Стул"))
        val room = Room(furniture, emptySet())
        // Room overrides toString(), so we can test it
        assertNotNull(room.toString())
        assertTrue(room.toString().contains("Комната"))
    }

    @Test
    fun `test actor appear and leave methods`() {
        // Test the Actor interface methods
        assertDoesNotThrow {
            person.appear(planet)
        }
        
        assertDoesNotThrow {
            person.leave(planet)
        }
    }
}
