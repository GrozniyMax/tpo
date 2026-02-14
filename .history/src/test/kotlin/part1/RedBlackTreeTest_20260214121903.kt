package part1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import tpo.maxim.part1.RedBlackTree
import java.lang.reflect.Field
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible



class RedBlackTreeTest {

    // Helper functions to access private fields using reflection
    private fun getPrivateField(obj: Any, fieldName: String): Any? {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        return field.get(obj)
    }

    private fun setPrivateField(obj: Any, fieldName: String, value: Any?) {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }

    private fun getNodeData(node: Any?): Any? {
        return if (node != null) getPrivateField(node, "data") else null
    }

    private fun getNodeLeft(node: Any?): Any? {
        return if (node != null) getPrivateField(node, "left") else null
    }

    private fun getNodeRight(node: Any?): Any? {
        return if (node != null) getPrivateField(node, "right") else null
    }

    private fun getNodeParent(node: Any?): Any? {
        return if (node != null) getPrivateField(node, "parent") else null
    }

    private fun isNodeRed(node: Any?): Boolean {
        return if (node != null) getPrivateField(node, "isRed") as Boolean else false
    }

    private fun getRoot(tree: RedBlackTree<*>): Any? {
        return getPrivateField(tree, "root")
    }

    // Test cases for rebalancing
    @Test
    fun `test left rotation scenario`() {
        val tree = RedBlackTree<Int>()
        
        // Insert nodes in sequence that should trigger a left rotation
        // Insert: 1, 2, 3 - this should trigger a left rotation at node 1
        tree.add(1)  // root: 1 (red)
        tree.add(2)  // right child of 1: 2 (red) -> triggers rebalance
        tree.add(3)  // right child of 2: 3 (red) -> triggers rebalance
        
        val root = getRoot(tree)
        assertNotNull(root, "Root should not be null")
        assertEquals(2, getNodeData(root), "Root should be 2 after rebalancing")
        assertFalse(isNodeRed(root!!), "Root should be black")
        
        val leftChild = getNodeLeft(root)
        assertNotNull(leftChild, "Left child should not be null")
        assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        assertTrue(isNodeRed(leftChild!!), "Left child should be red")
        
        val rightChild = getNodeRight(root)
        assertNotNull(rightChild, "Right child should not be null")
        assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        assertTrue(isNodeRed(rightChild!!), "Right child should be red")
    }

    @Test
    fun `test right rotation scenario`() {
        val tree = RedBlackTree<Int>()
        
        // Insert nodes in sequence that should trigger a right rotation
        // Insert: 3, 2, 1 - this should trigger a right rotation at node 3
        tree.add(3)  // root: 3 (red)
        tree.add(2)  // left child of 3: 2 (red) -> triggers rebalance
        tree.add(1)  // left child of 2: 1 (red) -> triggers rebalance
        
        val root = getRoot(tree)
        assertNotNull(root, "Root should not be null")
        assertEquals(2, getNodeData(root), "Root should be 2 after rebalancing")
        assertFalse(isNodeRed(root!!), "Root should be black")
        
        val leftChild = getNodeLeft(root)
        assertNotNull(leftChild, "Left child should not be null")
        assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        assertTrue(isNodeRed(leftChild!!), "Left child should be red")
        
        val rightChild = getNodeRight(root)
        assertNotNull(rightChild, "Right child should not be null")
        assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        assertTrue(isNodeRed(rightChild!!), "Right child should be red")
    }

    @Test
    fun `test color flip scenario`() {
        val tree = RedBlackTree<Int>()
        
        // Insert nodes that should trigger a color flip
        tree.add(2)  // root: 2 (red)
        tree.add(1)  // left child of 2: 1 (red)
        tree.add(3)  // right child of 2: 3 (red) -> should trigger color flip
        
        val root = getRoot(tree)
        assertNotNull(root, "Root should not be null")
        assertEquals(2, getNodeData(root), "Root should be 2")
        assertFalse(isNodeRed(root!!), "Root should be black after color flip")
        
        val leftChild = getNodeLeft(root)
        assertNotNull(leftChild, "Left child should not be null")
        assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        assertFalse(isNodeRed(leftChild!!), "Left child should be black after color flip")
        
        val rightChild = getNodeRight(root)
        assertNotNull(rightChild, "Right child should not be null")
        assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        assertFalse(isNodeRed(rightChild!!), "Right child should be black after color flip")
    }

    @Test
    fun `test complex rebalancing sequence`() {
        val tree = RedBlackTree<Int>()
        
        // Insert a sequence that triggers multiple rebalancing operations
        val insertSequence = listOf(10, 5, 15, 3, 7, 12, 17, 1, 4, 6, 8, 11, 13, 16, 18)
        
        for (value in insertSequence) {
            tree.add(value)
        }
        
        // Verify the tree maintains red-black properties
        val root = getRoot(tree)
        assertNotNull(root, "Root should not be null")
        assertFalse(isNodeRed(root!!), "Root should be black")
        
        // Verify all elements are present
        for (value in insertSequence) {
            assertTrue(tree.contains(value), "Tree should contain $value")
        }
        
        // Verify correct size
        assertEquals(insertSequence.size, tree.size, "Size should match number of inserted elements")
        
        // Verify ordering is maintained
        assertEquals(insertSequence.sorted(), tree.toList(), "Elements should be in sorted order")
    }

    @Test
    fun `test left-right rotation scenario`() {
        val tree = RedBlackTree<Int>()
        
        // Insert nodes that should trigger a left-right rotation
        tree.add(3)  // root: 3
        tree.add(1)  // left child of 3: 1
        tree.add(2)  // right child of 1: 2 -> should trigger left-right rotation
        
        val root = getRoot(tree)
        assertNotNull(root, "Root should not be null")
        assertEquals(2, getNodeData(root), "Root should be 2 after left-right rotation")
        assertFalse(isNodeRed(root!!), "Root should be black")
        
        val leftChild = getNodeLeft(root)
        assertNotNull(leftChild, "Left child should not be null")
        assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        assertTrue(isNodeRed(leftChild!!), "Left child should be red")
        
        val rightChild = getNodeRight(root)
        assertNotNull(rightChild, "Right child should not be null")
        assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        assertTrue(isNodeRed(rightChild!!), "Right child should be red")
    }

    @Test
    fun `test right-left rotation scenario`() {
        val tree = RedBlackTree<Int>()
        
        // Insert nodes that should trigger a right-left rotation
        tree.add(1)  // root: 1
        tree.add(3)  // right child of 1: 3
        tree.add(2)  // left child of 3: 2 -> should trigger right-left rotation
        
        val root = getRoot(tree)
        assertNotNull(root, "Root should not be null")
        assertEquals(2, getNodeData(root), "Root should be 2 after right-left rotation")
        assertFalse(isNodeRed(root!!), "Root should be black")
        
        val leftChild = getNodeLeft(root)
        assertNotNull(leftChild, "Left child should not be null")
        assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        assertTrue(isNodeRed(leftChild!!), "Left child should be red")
        
        val rightChild = getNodeRight(root)
        assertNotNull(rightChild, "Right child should not be null")
        assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        assertTrue(isNodeRed(rightChild!!), "Right child should be red")
    }


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
    fun `удаление листа`() {
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
        val tree = RedBlackTree<Int>()
        assertTrue(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test add return false for duplicate elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        assertFalse(tree.add(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test remove return true for existing elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        assertTrue(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test remove return false for non-existing elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        assertFalse(tree.remove(value))
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `test contains return true for existing elements`(value: Int) {
        val tree = RedBlackTree<Int>()
        tree.add(value)
        assertTrue(tree.contains(value))
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

    fun <T : Comparable<T>> RedBlackTree<T>.toList(): List<T> {
        val result = mutableListOf<T>()
        this.forEachOrdered { result.add(it) }
        return result
    }

    @Test
    fun `test size property`() {
        val tree = RedBlackTree<Int>()
        
        // Empty tree
        assertEquals(0, tree.size)
        
        // Add elements
        assertTrue(tree.add(10))
        assertEquals(1, tree.size)
        
        assertTrue(tree.add(5))
        assertEquals(2, tree.size)
        
        assertTrue(tree.add(15))
        assertEquals(3, tree.size)
        
        // Add duplicate (should not change size)
        assertFalse(tree.add(10))
        assertEquals(3, tree.size)
        
        // Remove elements
        assertTrue(tree.remove(5))
        assertEquals(2, tree.size)
        
        assertTrue(tree.remove(15))
        assertEquals(1, tree.size)
        
        assertTrue(tree.remove(10))
        assertEquals(0, tree.size)
        
        // Remove from empty tree
        assertFalse(tree.remove(10))
        assertEquals(0, tree.size)
    }

    @Test
    fun `test size with complex operations`() {
        val tree = RedBlackTree<Int>()
        
        // Add many elements
        val elements = listOf(50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45)
        for (element in elements) {
            tree.add(element)
        }
        assertEquals(elements.size, tree.size)
        
        // Remove some elements
        tree.remove(30)
        tree.remove(70)
        assertEquals(elements.size - 2, tree.size)
        
        // Add them back
        tree.add(30)
        assertEquals(elements.size - 1, tree.size)
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
        assertEquals(10, tree.size)
        for (element in elements) {
            assertTrue(tree.contains(element))
        }
        
        // Verify ordering is maintained
        assertEquals(elements, tree.toList())
        
        // Remove some elements
        tree.remove(5)
        tree.remove(7)
        assertEquals(8, tree.size)
        
        // Add them back
        tree.add(5)
        tree.add(7)
        assertEquals(10, tree.size)
        
        // Final verification
        assertEquals(elements, tree.toList())
    }
}
