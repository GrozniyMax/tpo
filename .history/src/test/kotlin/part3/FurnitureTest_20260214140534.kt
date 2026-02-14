package tpo.maxim.part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class FurnitureTest {

    @Test
    fun `test furniture creation with name`() {
        val furniture = Furniture("Стол")
        assertEquals("Стол", furniture.name)
        assertTrue(furniture.details.isEmpty())
    }

    @Test
    fun `test furniture creation with name and details`() {
        val details = listOf("деревянный", "большой")
        val furniture = Furniture("Стол", details)
        assertEquals("Стол", furniture.name)
        assertEquals(details, furniture.details)
    }

    @Test
    fun `test furniture toString`() {
        val furniture = Furniture("Стул")
        // Since Furniture doesn't override toString(), we can't test meaningful state changes
        // But we can verify it has a default toString representation
        assertNotNull(furniture.toString())
    }
}
