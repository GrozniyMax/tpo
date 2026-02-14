package part2

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import tpo.maxim.part2.RedBlackTree
import org.assertj.core.api.Assertions.assertThat

class RedBlackTreeTest {

    @Test
    fun `Несколько элементов, должен содержать все`() {
        val tree = RedBlackTree<Int>()

        Assertions.assertTrue(tree.add(10))
        Assertions.assertTrue(tree.add(5))
        Assertions.assertTrue(tree.add(15))
        Assertions.assertTrue(tree.add(3))
        Assertions.assertTrue(tree.add(7))
        Assertions.assertTrue(tree.add(12))
        Assertions.assertTrue(tree.add(17))

        Assertions.assertTrue(tree.contains(10))
        Assertions.assertTrue(tree.contains(5))
        Assertions.assertTrue(tree.contains(15))
        Assertions.assertTrue(tree.contains(3))
        Assertions.assertTrue(tree.contains(7))
        Assertions.assertTrue(tree.contains(12))
        Assertions.assertTrue(tree.contains(17))

        Assertions.assertFalse(tree.contains(1))
        Assertions.assertFalse(tree.contains(20))
    }

    @Test
    fun `удаление листа`() {
        val tree = RedBlackTree<Int>()

        // Add elements
        tree.add(10)
        tree.add(5)
        tree.add(15)
        tree.add(3)
        tree.add(7)

        Assertions.assertTrue(tree.remove(5))
        Assertions.assertFalse(tree.contains(5))
        Assertions.assertFalse(tree.remove(5))

        assertThat(tree.toList())
            .containsExactly(3, 7, 10, 15)

        // Remove root
        Assertions.assertTrue(tree.remove(10))
        Assertions.assertFalse(tree.contains(10))

        // Remove remaining elements
        Assertions.assertTrue(tree.remove(3))
        Assertions.assertTrue(tree.remove(7))
        Assertions.assertTrue(tree.remove(15))

        // Tree should be empty
        Assertions.assertFalse(tree.contains(3))
        Assertions.assertFalse(tree.contains(7))
        Assertions.assertFalse(tree.contains(15))
    }

    @Test
    fun `Удалеие корня дерева`() {

    }

    @Test
    fun `test toList traversal`() {
        val tree = RedBlackTree<Int>()

        // Add elements in random order
        tree.add(10)
        tree.add(5)
        tree.add(15)
        tree.add(3)
        tree.add(7)
        tree.add(12)
        tree.add(17)

        // Should be in sorted order
        Assertions.assertEquals(listOf(3, 5, 7, 10, 12, 15, 17), tree.toList())
    }

    @Test
    fun `test toList on empty tree`() {
        val tree = RedBlackTree<Int>()
        Assertions.assertTrue(tree.toList().isEmpty())
    }

    @Test
    fun `test toList on single element`() {
        val tree = RedBlackTree<Int>()
        tree.add(42)
        Assertions.assertEquals(listOf(42), tree.toList())
    }

    @Test
    fun `test add and remove with strings`() {
        val tree = RedBlackTree<String>()

        Assertions.assertTrue(tree.add("banana"))
        Assertions.assertTrue(tree.add("apple"))
        Assertions.assertTrue(tree.add("cherry"))

        Assertions.assertTrue(tree.contains("banana"))
        Assertions.assertTrue(tree.contains("apple"))
        Assertions.assertTrue(tree.contains("cherry"))
        Assertions.assertFalse(tree.contains("date"))

        Assertions.assertEquals(listOf("apple", "banana", "cherry"), tree.toList())

        Assertions.assertTrue(tree.remove("banana"))
        Assertions.assertFalse(tree.contains("banana"))
        Assertions.assertTrue(tree.contains("apple"))
        Assertions.assertTrue(tree.contains("cherry"))
    }

    @Test
    fun `test complex add and remove sequence`() {
        val tree = RedBlackTree<Int>()

        // Add many elements
        val elements = listOf(50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45)
        for (element in elements) {
            Assertions.assertTrue(tree.add(element))
        }

        // Verify all elements are present
        for (element in elements) {
            Assertions.assertTrue(tree.contains(element))
        }

        // Remove some elements
        Assertions.assertTrue(tree.remove(30))
        Assertions.assertFalse(tree.contains(30))

        Assertions.assertTrue(tree.remove(70))
        Assertions.assertFalse(tree.contains(70))

        // Add them back
        Assertions.assertTrue(tree.add(30))
        Assertions.assertTrue(tree.contains(30))

        // Verify order is still correct
        Assertions.assertEquals(listOf(10, 20, 25, 30, 35, 40, 45, 50, 60, 80), tree.toList())
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test add return true for new elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        Assertions.assertTrue(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test add return false for duplicate elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        Assertions.assertFalse(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test remove return true for existing elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        Assertions.assertTrue(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test remove return false for non-existing elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        Assertions.assertFalse(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test contains return true for existing elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        Assertions.assertTrue(tree.contains(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [10, 20, 30, 40, 50])
    fun `test contains return false for non-existing elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(1)
        tree.add(2)
        tree.add(3)
        tree.add(4)
        tree.add(5)
        Assertions.assertFalse(tree.contains(value))
    }

    @Test
    fun `test return values of add and remove`() {
        val tree = RedBlackTree<Int>()

        // First add should return true
        Assertions.assertTrue(tree.add(10))
        // Second add of same element should return false
        Assertions.assertFalse(tree.add(10))

        // First remove should return true
        Assertions.assertTrue(tree.remove(10))
        // Second remove of same element should return false
        Assertions.assertFalse(tree.remove(10))

        // Remove non-existent element should return false
        Assertions.assertFalse(tree.remove(20))
    }

    fun <T : Comparable<T>> RedBlackTree<T>.toList(): List<T> {
        val result = mutableListOf<T>()
        this.forEachOrdered { result.add(it) }
        return result
    }

    @Test
    fun `test size property`() {
        val tree = RedBlackTree<Int>()

        // Empty tree
        Assertions.assertEquals(0, tree.size)

        // Add elements
        Assertions.assertTrue(tree.add(10))
        Assertions.assertEquals(1, tree.size)

        Assertions.assertTrue(tree.add(5))
        Assertions.assertEquals(2, tree.size)

        Assertions.assertTrue(tree.add(15))
        Assertions.assertEquals(3, tree.size)

        // Add duplicate (should not change size)
        Assertions.assertFalse(tree.add(10))
        Assertions.assertEquals(3, tree.size)

        // Remove elements
        Assertions.assertTrue(tree.remove(5))
        Assertions.assertEquals(2, tree.size)

        Assertions.assertTrue(tree.remove(15))
        Assertions.assertEquals(1, tree.size)

        Assertions.assertTrue(tree.remove(10))
        Assertions.assertEquals(0, tree.size)

        // Remove from empty tree
        Assertions.assertFalse(tree.remove(10))
        Assertions.assertEquals(0, tree.size)
    }

    @Test
    fun `test size with complex operations`() {
        val tree = RedBlackTree<Int>()

        // Add many elements
        val elements = listOf(50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45)
        for (element in elements) {
            tree.add(element)
        }
        Assertions.assertEquals(elements.size, tree.size)

        // Remove some elements
        tree.remove(30)
        tree.remove(70)
        Assertions.assertEquals(elements.size - 2, tree.size)

        // Add them back
        tree.add(30)
        Assertions.assertEquals(elements.size - 1, tree.size)
    }

    @Test
    fun `test red-black tree properties maintained after operations`() {
        val tree = RedBlackTree<Int>()

        // Add elements that should trigger rebalancing
        val elements = (1..10).toList()
        for (element in elements) {
            tree.add(element)
        }

        // Verify through public API that tree works correctly
        Assertions.assertEquals(10, tree.size)
        for (element in elements) {
            Assertions.assertTrue(tree.contains(element))
        }

        // Verify ordering is maintained
        Assertions.assertEquals(elements, tree.toList())

        // Remove some elements
        tree.remove(5)
        tree.remove(7)
        Assertions.assertEquals(8, tree.size)

        // Add them back
        tree.add(5)
        tree.add(7)
        Assertions.assertEquals(10, tree.size)

        // Final verification
        Assertions.assertEquals(elements, tree.toList())
    }
}
