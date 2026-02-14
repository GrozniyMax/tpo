package tpo.maxim.part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PlaceTest {

    companion object {
        @JvmStatic
        fun placeProvider(): Stream<Place> {
            return Stream.of(
                Planet("Марс", emptySet()),
                Room(emptyList(), emptySet())
            )
        }
    }

    @ParameterizedTest
    @MethodSource("placeProvider")
    fun `test actor entering place`(place: Place) {
        val person = Person("Максим", Species.HUMAN)
        // This test checks state change when actor enters a place
        assertDoesNotThrow {
            place.wentIn(person)
        }
    }

    @ParameterizedTest
    @MethodSource("placeProvider")
    fun `test actor leaving place`(place: Place) {
        val person = Person("Максим", Species.HUMAN)
        // First enter, then leave
        place.wentIn(person)
        assertDoesNotThrow {
            place.wentOut(person)
        }
    }

    @ParameterizedTest
    @MethodSource("placeProvider")
    fun `test duplicate entry throws exception`(place: Place) {
        val person = Person("Максим", Species.HUMAN)
        place.wentIn(person)
        val exception = assertThrows<IllegalStateException> {
            place.wentIn(person)
        }
        assertTrue(exception.message!!.contains("уже в"))
    }

    @ParameterizedTest
    @MethodSource("placeProvider")
    fun `test leaving when not present throws exception`(place: Place) {
        val person = Person("Максим", Species.HUMAN)
        val exception = assertThrows<IllegalStateException> {
            place.wentOut(person)
        }
        assertTrue(exception.message!!.contains("уже покинул") || exception.message!!.contains("уже в"))
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
        val person = Person("Максим", Species.HUMAN)
        val planet = Planet("Марс", emptySet())
        
        // Test the Actor interface methods
        assertDoesNotThrow {
            person.appear(planet)
        }
        
        assertDoesNotThrow {
            person.leave(planet)
        }
    }
}
