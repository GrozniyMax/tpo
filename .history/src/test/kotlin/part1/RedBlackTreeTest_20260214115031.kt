package part1

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import tpo.maxim.part1.RedBlackTree

// Extension function to collect all elements from the tree in order
fun <T : Comparable<T>> RedBlackTree<T>.toList(): List<T> {
    val result = mutableListOf<T>()
    this.forEachOrdered { result.add(it) }
    return result
}

class RedBlackTreeTest {
    
    @Test
    fun `test add and contains with single element`() {
        val tree = RedBlackTree<Int>()
        
        assertFalse(tree.contains(5))
        assertTrue(tree.add(5))
        assertTrue(tree.contains(5))
        assertFalse(tree.add(5)) // Adding duplicate should return false
        assertTrue(tree.contains(5))
    }

    @Test
    fun `test add multiple elements`() {
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
    fun `test remove elements`() {
        val tree = RedBlackTree<Int>()
        
        // Add elements
        tree.add(10)
        tree.add(5)
        tree.add(15)
        tree.add(3)
        tree.add(7)
        
        // Test removal
        assertTrue(tree.contains(5))
        assertTrue(tree.remove(5))
        assertFalse(tree.contains(5))
        assertFalse(tree.remove(5)) // Removing non-existent element should return false
        
        assertTrue(tree.contains(10))
        assertTrue(tree.contains(15))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(7))
        
        // Remove root
        assertTrue(tree.remove(10))
        assertFalse(tree.contains(10))
        
        // Remove remaining elements
        assertTrue(tree.remove(3))
        assertTrue(tree.remove(7))
        assertTrue(tree.remove(15))
        
        // Tree should be empty
        assertFalse(tree.contains(3))
        assertFalse(tree.contains(7))
        assertFalse(tree.contains(15))
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
        assertEquals(listOf(3, 5, 7, 10, 12, 15, 17), tree.toList())
    }

    @Test
    fun `test toList on empty tree`() {
        val tree = RedBlackTree<Int>()
        assertTrue(tree.toList().isEmpty())
    }

    @Test
    fun `test toList on single element`() {
        val tree = RedBlackTree<Int>()
        tree.add(42)
        assertEquals(listOf(42), tree.toList())
    }

    @Test
    fun `test add and remove with strings`() {
        val tree = RedBlackTree<String>()
        
        assertTrue(tree.add("banana"))
        assertTrue(tree.add("apple"))
        assertTrue(tree.add("cherry"))
        
        assertTrue(tree.contains("banana"))
        assertTrue(tree.contains("apple"))
        assertTrue(tree.contains("cherry"))
        assertFalse(tree.contains("date"))
        
        assertEquals(listOf("apple", "banana", "cherry"), tree.toList())
        
        assertTrue(tree.remove("banana"))
        assertFalse(tree.contains("banana"))
        assertTrue(tree.contains("apple"))
        assertTrue(tree.contains("cherry"))
    }

    @Test
    fun `test complex add and remove sequence`() {
        val tree = RedBlackTree<Int>()
        
        // Add many elements
        val elements = listOf(50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45)
        for (element in elements) {
            assertTrue(tree.add(element))
        }
        
        // Verify all elements are present
        for (element in elements) {
            assertTrue(tree.contains(element))
        }
        
        // Remove some elements
        assertTrue(tree.remove(30))
        assertFalse(tree.contains(30))
        
        assertTrue(tree.remove(70))
        assertFalse(tree.contains(70))
        
        // Add them back
        assertTrue(tree.add(30))
        assertTrue(tree.contains(30))
        
        // Verify order is still correct
        assertEquals(listOf(10, 20, 25, 30, 35, 40, 45, 50, 60, 80), tree.toList())
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test add return true for new elements`(value: Int) {
        assertTrue(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test add return false for duplicate elements`(value: Int) {
        tree.add(value)
        assertFalse(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test remove return true for existing elements`(value: Int) {
        tree.add(value)
        assertTrue(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test remove return false for non-existing elements`(value: Int) {
        assertFalse(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test contains return true for existing elements`(value: Int) {
        tree.add(value)
        assertTrue(tree.contains(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [10, 20, 30, 40, 50])
    fun `test contains return false for non-existing elements`(value: Int) {
        tree.add(1)
        tree.add(2)
        tree.add(3)
        tree.add(4)
        tree.add(5)
        assertFalse(tree.contains(value))
    }

    @Test
    fun `test return values of add and remove`() {
        val tree = RedBlackTree<Int>()
        
        // First add should return true
        assertTrue(tree.add(10))
        // Second add of same element should return false
        assertFalse(tree.add(10))
        
        // First remove should return true
        assertTrue(tree.remove(10))
        // Second remove of same element should return false
        assertFalse(tree.remove(10))
        
        // Remove non-existent element should return false
        assertFalse(tree.remove(20))
    }
}
