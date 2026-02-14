package part2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import tpo.maxim.part2.RedBlackTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class RedBlackTreeTest {

    @Test
    fun `Несколько элементов, должен содержать все`() {
        val tree = RedBlackTree<Int>()

        assertTrue(tree.add(10))
        assertTrue(tree.add(5))
        assertTrue(tree.add(15))
        assertTrue(tree.add(3))
        assertTrue(tree.add(7))
        assertTrue(tree.add(12))
        assertTrue(tree.add(17))

        assertTrue(tree.contains(10))
        assertTrue(tree.contains(5))
        assertTrue(tree.contains(15))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(12))
        assertTrue(tree.contains(17))

        assertFalse(tree.contains(1))
        assertFalse(tree.contains(20))
    }

    @Test
    fun `удаление элемента`() {
        val tree = RedBlackTree<Int>()

        // Add elements
        tree.add(10)
        tree.add(5)
        tree.add(15)
        tree.add(3)
        tree.add(7)

        assertTrue(tree.remove(5))
        assertFalse(tree.contains(5))
        assertFalse(tree.remove(5))

        assertThat(tree.toList())
            .containsExactly(3, 7, 10, 15)

    }
    
    @Test
    fun `проверка forEachOrdered`() {
        val tree = RedBlackTree<Int>()

        tree.add(10)
        tree.add(5)
        tree.add(15)
        tree.add(3)
        tree.add(7)
        tree.add(12)
        tree.add(17)

        assertEquals(listOf(3, 5, 7, 10, 12, 15, 17), tree.toList())
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `true при добавлении нового элемента`(value: Int) {
        val tree = RedBlackTree<Int>()
        assertTrue(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `false при добавлении для дубликата`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        assertFalse(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `true при удалении существующего элемента`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        assertTrue(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `false при удалении несуществующего элемента`(value: Int) {
        val tree = RedBlackTree<Int>()
        assertFalse(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `true при проверке наличия существующего элемента`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        assertTrue(tree.contains(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [10, 20, 30, 40, 50])
    fun `false при проверке наличия несуществующего элемента`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(1)
        tree.add(2)
        tree.add(3)
        tree.add(4)
        tree.add(5)
        assertFalse(tree.contains(value))
    }

    @Test
    fun `у пустого дерева size = 0`() {
        val tree = RedBlackTree<Int>()
        assertEquals(0, tree.size)
    }

    @Test
    fun `при добавлении элемента size увеличивается`() {
        val tree = RedBlackTree<Int>()

        assertTrue(tree.add(10))
        assertEquals(1, tree.size)

        assertTrue(tree.add(5))
        assertEquals(2, tree.size)

        assertTrue(tree.add(15))
        assertEquals(3, tree.size)
    }

    fun `удаление элемента уменьшает size`() {
        val tree = RedBlackTree<Int>()
        tree.add(10)
        tree.add(5)
        tree.add(15)

        assertTrue(tree.remove(5))
        assertEquals(2, tree.size)

        assertTrue(tree.remove(10))
        assertEquals(1, tree.size)

        assertTrue(tree.remove(15))
        assertEquals(0, tree.size)
    }

    fun <T : Comparable<T>> RedBlackTree<T>.toList(): List<T> {
        val result = mutableListOf<T>()
        this.forEachOrdered { result.add(it) }
        return result
    }
}
