package part2

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tpo.maxim.part2.RedBlackTree

class TreeRebalanseTest {

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
        Assertions.assertNotNull(root, "Root should not be null")
        Assertions.assertEquals(2, getNodeData(root), "Root should be 2 after rebalancing")
        Assertions.assertFalse(isNodeRed(root!!), "Root should be black")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Left child should not be null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Left child should be red")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Right child should not be null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Right child should be red")
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
        Assertions.assertNotNull(root, "Root should not be null")
        Assertions.assertEquals(2, getNodeData(root), "Root should be 2 after rebalancing")
        Assertions.assertFalse(isNodeRed(root!!), "Root should be black")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Left child should not be null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Left child should be red")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Right child should not be null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Right child should be red")
    }

    @Test
    fun `test color flip scenario`() {
        val tree = RedBlackTree<Int>()

        // Insert nodes that should trigger a color flip
        tree.add(2)  // root: 2 (red)
        tree.add(1)  // left child of 2: 1 (red)
        tree.add(3)  // right child of 2: 3 (red) -> should trigger color flip

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Root should not be null")
        Assertions.assertEquals(2, getNodeData(root), "Root should be 2")
        Assertions.assertFalse(isNodeRed(root!!), "Root should be black after color flip")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Left child should not be null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        Assertions.assertFalse(isNodeRed(leftChild!!), "Left child should be black after color flip")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Right child should not be null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        Assertions.assertFalse(isNodeRed(rightChild!!), "Right child should be black after color flip")
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
        Assertions.assertNotNull(root, "Root should not be null")
        Assertions.assertFalse(isNodeRed(root!!), "Root should be black")

        // Verify all elements are present
        for (value in insertSequence) {
            Assertions.assertTrue(tree.contains(value), "Tree should contain $value")
        }

        // Verify correct size
        Assertions.assertEquals(insertSequence.size, tree.size, "Size should match number of inserted elements")

        // Verify ordering is maintained
        Assertions.assertEquals(insertSequence.sorted(), tree.toList(), "Elements should be in sorted order")
    }

    @Test
    fun `test left-right rotation scenario`() {
        val tree = RedBlackTree<Int>()

        // Insert nodes that should trigger a left-right rotation
        tree.add(3)  // root: 3
        tree.add(1)  // left child of 3: 1
        tree.add(2)  // right child of 1: 2 -> should trigger left-right rotation

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Root should not be null")
        Assertions.assertEquals(2, getNodeData(root), "Root should be 2 after left-right rotation")
        Assertions.assertFalse(isNodeRed(root!!), "Root should be black")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Left child should not be null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Left child should be red")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Right child should not be null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Right child should be red")
    }

    @Test
    fun `test right-left rotation scenario`() {
        val tree = RedBlackTree<Int>()

        // Insert nodes that should trigger a right-left rotation
        tree.add(1)  // root: 1
        tree.add(3)  // right child of 1: 3
        tree.add(2)  // left child of 3: 2 -> should trigger right-left rotation

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Root should not be null")
        Assertions.assertEquals(2, getNodeData(root), "Root should be 2 after right-left rotation")
        Assertions.assertFalse(isNodeRed(root!!), "Root should be black")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Left child should not be null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Left child should be 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Left child should be red")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Right child should not be null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Right child should be 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Right child should be red")
    }

    fun <T : Comparable<T>> RedBlackTree<T>.toList(): List<T> {
        val result = mutableListOf<T>()
        this.forEachOrdered { result.add(it) }
        return result
    }
}
