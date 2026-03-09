package part2

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach

class RedBlackTreeTest {

    private lateinit var tree: RedBlackTree<Int>

    @BeforeEach
    fun setup() {
        tree = RedBlackTree()
    }

    @Test
    fun `Пустое дерево и базовые операции`() {
        assertTrue(tree.isEmpty())
        assertEquals(0, tree.size())
        assertFalse(tree.contains(value = 10))

        tree.add(value = 10)
        assertFalse(tree.isEmpty())
        assertEquals(1, tree.size())
        assertTrue(tree.contains(value = 10))
    }

    @Test
    fun `Вставка и поиск элементов`() {
        tree.add(value = 10)
        tree.add(value = 5)
        tree.add(value = 15)

        assertTrue(tree.contains(value = 10))
        assertTrue(tree.contains(value = 5))
        assertTrue(tree.contains(value = 15))
        assertFalse(tree.contains(value = 20))
        assertEquals(listOf(5, 10, 15), tree.toList())
    }

    @Test
    fun `Вставка в возрастающем порядке`() {
        (1..5).forEach { tree.add(value = it) }

        assertEquals(5, tree.size())
        assertEquals((1..5).toList(), tree.toList())
    }

    @Test
    fun `Вставка в убывающем порядке`() {
        (5 downTo 1).forEach { tree.add(value = it) }

        assertEquals(5, tree.size())
        assertEquals((1..5).toList(), tree.toList())
    }

    @Test
    fun `Вставка дубликатов`() {
        tree.add(value = 10)
        tree.add(value = 10)

        assertEquals(1, tree.size())
    }

    @Test
    fun `Удаление единственного элемента`() {
        tree.add(value = 10)
        tree.delete(value = 10)

        assertTrue(tree.isEmpty())
        assertFalse(tree.contains(value = 10))
    }

    @Test
    fun `Удаление несуществующего элемента`() {
        tree.add(value = 10)
        tree.delete(value = 20)

        assertEquals(1, tree.size())
    }

    @Test
    fun `Удаление узла с одним ребенком`() {
        tree.add(value = 10)
        tree.add(value = 5)
        tree.add(value = 3)

        tree.delete(value = 5)

        assertEquals(listOf(3, 10), tree.toList())
    }

    @Test
    fun `Удаление узла с двумя детьми`() {
        tree.add(value = 10)
        tree.add(value = 5)
        tree.add(value = 15)
        tree.add(value = 3)
        tree.add(value = 7)

        tree.delete(value = 5)

        assertFalse(tree.contains(value = 5))
        assertEquals(listOf(3, 7, 10, 15), tree.toList())
    }

    @Test
    fun `Удаление корня`() {
        tree.add(value = 10)
        tree.add(value = 5)
        tree.add(value = 15)

        tree.delete(value = 10)

        assertEquals(listOf(5, 15), tree.toList())
    }

    @Test
    fun `Последовательное удаление всех элементов`() {
        (1..5).forEach { tree.add(value = it) }
        (1..5).forEach { tree.delete(value = it) }

        assertTrue(tree.isEmpty())
    }

    @Test
    fun `Смешанные операции вставки и удаления`() {
        tree.add(value = 50)
        tree.add(value = 25)
        tree.add(value = 75)
        tree.add(value = 10)

        tree.delete(value = 25)
        assertFalse(tree.contains(value = 25))

        tree.add(value = 30)
        assertTrue(tree.contains(value = 30))

        tree.delete(value = 50)
        assertFalse(tree.contains(value = 50))

        assertEquals(listOf(10, 30, 75), tree.toList())
    }

    @Test
    fun `Отрицательные числа и граничные значения`() {
        tree.add(value = -10)
        tree.add(value = 0)
        tree.add(value = 10)
        tree.add(value = Int.MAX_VALUE)
        tree.add(value = Int.MIN_VALUE)

        assertEquals(5, tree.size())
        assertTrue(tree.contains(value = -10))
        assertTrue(tree.contains(value = Int.MAX_VALUE))
        assertTrue(tree.contains(value = Int.MIN_VALUE))
        assertEquals(listOf(Int.MIN_VALUE, -10, 0, 10, Int.MAX_VALUE), tree.toList())
    }

    @Test
    fun `Брат черный, левый ребенок красный, правый черный`() {
        val insertions = listOf(41, 19, 8, 39, 21, 97, 109, 82, 58, 72)
        val deletions = listOf(39, 8, 19, 41, 82)

        insertions.forEach { tree.add(value = it) }
        deletions.forEach { tree.delete(value = it) }

        assertEquals(listOf(21, 58, 72, 97, 109), tree.toList())
        assertEquals(5, tree.size())
    }

    @Test
    fun `Проверка enum Color`() {
        val colors = Color.entries
        assertEquals(2, colors.size)
        assertTrue(colors.contains(Color.RED))
        assertTrue(colors.contains(Color.BLACK))
    }
}
