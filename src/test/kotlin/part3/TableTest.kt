package part3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import tpo.maxim.part3.Table
import tpo.maxim.part3.Medal
import tpo.maxim.part3.Cup

class TableTest {


    @Test
    fun `добавление предметов на стол`() {
        val table = Table(3)
        val medal = Medal("За победу")
        val cup = Cup("Чемпионат мира", 2026)
        
        table.addItem(medal)
        table.addItem(cup)
        
        assertEquals(2, table.items.size)
        assertTrue(table.items.contains(medal))
        assertTrue(table.items.contains(cup))
    }

    @Test
    fun `проверка наличия предмета на столе`() {
        val table = Table(5)
        val medal = Medal("За победу")
        val cup = Cup("Чемпионат мира", 2026)
        
        table.addItem(medal)
        
        assertTrue(table.contains(medal))
        assertFalse(table.contains(cup))
    }

    @Test
    fun `попытка добавления одинакового предмета`() {
        val table = Table(5)
        val medal = Medal("За победу")
        
        table.addItem(medal)
        
        val exception = assertThrows(IllegalStateException::class.java) {
            table.addItem(medal)
        }
        assertEquals("$medal уже на столе", exception.message)
    }

    @Test
    fun `попытка добавления предмета при переполнении`() {
        val table = Table(2)
        val medal1 = Medal("За первенство")
        val medal2 = Medal("За победу")
        val cup = Cup("Турнир", 2026)
        
        table.addItem(medal1)
        table.addItem(medal2)
        
        val exception = assertThrows(IllegalStateException::class.java) {
            table.addItem(cup)
        }
        assertEquals("Стол переполнен", exception.message)
    }

    @Test
    fun `удаление предметов со стола`() {
        val table = Table(5)
        val medal = Medal("За победу")
        val cup = Cup("Чемпионат мира", 2026)
        
        table.addItem(medal)
        table.addItem(cup)
        assertEquals(2, table.items.size)
        assertTrue(table.contains(medal))
        assertTrue(table.contains(cup))
        
        table.removeItem(medal)
        assertEquals(1, table.items.size)
        assertFalse(table.contains(medal))
        assertTrue(table.contains(cup))
        
        table.removeItem(cup)
        assertTrue(table.items.isEmpty())
        assertFalse(table.contains(cup))
    }

    @Test
    fun `попытка удаления предмета которого нет на столе`() {
        val table = Table(5)
        val medal = Medal("За победу")
        
        val exception = assertThrows(IllegalStateException::class.java) {
            table.removeItem(medal)
        }
        assertEquals("$medal не на столе", exception.message)
        assertFalse(table.contains(medal))
    }

    @Test
    fun `работа с пустым столом`() {
        val table = Table(3)
        assertTrue(table.items.isEmpty())
        assertEquals(0, table.items.size)
        val medal = Medal("За победу")
        assertFalse(table.contains(medal))
    }
}
