package part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import tpo.maxim.part3.Furniture
import tpo.maxim.part3.Person
import tpo.maxim.part3.Place
import tpo.maxim.part3.Planet
import tpo.maxim.part3.Room
import tpo.maxim.part3.Species
import java.util.stream.Stream

class PlaceTest {

    companion object {
        @JvmStatic
        fun emptyPlaceProvider(): Stream<Place> {
            return Stream.of(
                Planet("Марс", emptySet()),
                Room(emptyList(), emptySet())
            )
        }
    }

    @ParameterizedTest
    @MethodSource("emptyPlaceProvider")
    fun `добавление нового человека`(place: Place) {
        val person = Person("Максим", Species.HUMAN)

        assertDoesNotThrow {
            place.wentIn(person)
        }
    }

    @ParameterizedTest
    @MethodSource("emptyPlaceProvider")
    fun `добавление уже существующего человека`(place: Place) {
        val person = Person("Максим", Species.HUMAN)

        place.wentIn(person)

        val exception = assertThrows<IllegalStateException> {
            place.wentIn(person)
        }

        assertEquals("$person уже в ${place.name}", exception.message)
    }

    @ParameterizedTest
    @MethodSource("emptyPlaceProvider")
    fun `Уже существующий человек покидает место`(place: Place) {
        val person = Person("Максим", Species.HUMAN)
        place.wentIn(person)

        assertDoesNotThrow {
            place.wentOut(person)
        }
    }

    @ParameterizedTest
    @MethodSource("emptyPlaceProvider")
    fun `Несуществующий человек покидает место`(place: Place) {
        val person = Person("Максим", Species.HUMAN)

        val exception = assertThrows<IllegalStateException> {
            place.wentOut(person)
        }
        assertEquals("$person уже покинул ${place.name}", exception.message)
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
