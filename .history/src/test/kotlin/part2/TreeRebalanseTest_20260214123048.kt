package part2

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tpo.maxim.part2.RedBlackTree

class TreeRebalanseTest {

    /*
     * Изначальный вид дерева:
     * 
     *        4(B)
     *      /      \
     *    2(R)     6(R)
     *   /  \     /   \
     *  1(B) 3(B) 5(B) 7(B)
     */

    private lateinit var tree: RedBlackTree<Int>

    @BeforeEach
    fun setUp() {
        tree = RedBlackTree<Int>()
        tree.add(4)
        tree.add(2)
        tree.add(6)
        tree.add(1)
        tree.add(3)
        tree.add(5)
        tree.add(7)
    }

    // Вспомогательные функции для доступа к приватным полям через рефлексию
    private fun getPrivateField(obj: Any, fieldName: String): Any? {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        return field.get(obj)
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

    private fun isNodeRed(node: Any?): Boolean {
        return if (node != null) getPrivateField(node, "isRed") as Boolean else false
    }

    private fun getRoot(tree: RedBlackTree<*>): Any? {
        return getPrivateField(tree, "root")
    }

    // Тестовые случаи для проверки ребалансировки
    @Test
    fun `test left rotation scenario`() {
        val tree = RedBlackTree<Int>()

        // Вставка узлов по порядку, которая должна вызвать левый поворот
        // Вставка: 1, 2, 3 - это должно вызвать левый поворот у узла 1
        tree.add(1)  // корень: 1 (красный)
        tree.add(2)  // правый потомок 1: 2 (красный) -> вызывает ребалансировку
        tree.add(3)  // правый потомок 2: 3 (красный) -> вызывает ребалансировку

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Корень не должен быть null")
        Assertions.assertEquals(2, getNodeData(root), "Корень должен быть 2 после ребалансировки")
        Assertions.assertFalse(isNodeRed(root!!), "Корень должен быть черным")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Левый потомок не должен быть null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Левый потомок должен быть 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Левый потомок должен быть красным")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Правый потомок не должен быть null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Правый потомок должен быть 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Правый потомок должен быть красным")
    }

    @Test
    fun `test right rotation scenario`() {
        val tree = RedBlackTree<Int>()

        // Вставка узлов по порядку, которая должна вызвать правый поворот
        // Вставка: 3, 2, 1 - это должно вызвать правый поворот у узла 3
        tree.add(3)  // корень: 3 (красный)
        tree.add(2)  // левый потомок 3: 2 (красный) -> вызывает ребалансировку
        tree.add(1)  // левый потомок 2: 1 (красный) -> вызывает ребалансировку

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Корень не должен быть null")
        Assertions.assertEquals(2, getNodeData(root), "Корень должен быть 2 после ребалансировки")
        Assertions.assertFalse(isNodeRed(root!!), "Корень должен быть черным")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Левый потомок не должен быть null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Левый потомок должен быть 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Левый потомок должен быть красным")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Правый потомок не должен быть null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Правый потомок должен быть 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Правый потомок должен быть красным")
    }

    @Test
    fun `test color flip scenario`() {
        val tree = RedBlackTree<Int>()

        // Вставка узлов, которая должна вызвать изменение цвета
        tree.add(2)  // корень: 2 (красный)
        tree.add(1)  // левый потомок 2: 1 (красный)
        tree.add(3)  // правый потомок 2: 3 (красный) -> должно вызвать изменение цвета

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Корень не должен быть null")
        Assertions.assertEquals(2, getNodeData(root), "Корень должен быть 2")
        Assertions.assertFalse(isNodeRed(root!!), "Корень должен быть черным после изменения цвета")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Левый потомок не должен быть null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Левый потомок должен быть 1")
        Assertions.assertFalse(isNodeRed(leftChild!!), "Левый потомок должен быть черным после изменения цвета")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Правый потомок не должен быть null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Правый потомок должен быть 3")
        Assertions.assertFalse(isNodeRed(rightChild!!), "Правый потомок должен быть черным после изменения цвета")
    }

    @Test
    fun `test complex rebalancing sequence`() {
        val tree = RedBlackTree<Int>()

        // Вставка последовательности, которая вызывает несколько операций ребалансировки
        val insertSequence = listOf(10, 5, 15, 3, 7, 12, 17, 1, 4, 6, 8, 11, 13, 16, 18)

        for (value in insertSequence) {
            tree.add(value)
        }

        // Проверяем, что дерево сохраняет свойства красно-черного дерева
        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Корень не должен быть null")
        Assertions.assertFalse(isNodeRed(root!!), "Корень должен быть черным")

        // Проверяем, что все элементы присутствуют
        for (value in insertSequence) {
            Assertions.assertTrue(tree.contains(value), "Дерево должно содержать $value")
        }

        // Проверяем правильный размер
        Assertions.assertEquals(insertSequence.size, tree.size, "Размер должен соответствовать количеству вставленных элементов")

        // Проверяем, что порядок сохраняется
        Assertions.assertEquals(insertSequence.sorted(), tree.toList(), "Элементы должны быть в отсортированном порядке")
    }

    @Test
    fun `test left-right rotation scenario`() {
        val tree = RedBlackTree<Int>()

        // Вставка узлов, которая должна вызвать лево-правый поворот
        tree.add(3)  // корень: 3
        tree.add(1)  // левый потомок 3: 1
        tree.add(2)  // правый потомок 1: 2 -> должно вызвать лево-правый поворот

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Корень не должен быть null")
        Assertions.assertEquals(2, getNodeData(root), "Корень должен быть 2 после лево-правого поворота")
        Assertions.assertFalse(isNodeRed(root!!), "Корень должен быть черным")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Левый потомок не должен быть null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Левый потомок должен быть 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Левый потомок должен быть красным")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Правый потомок не должен быть null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Правый потомок должен быть 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Правый потомок должен быть красным")
    }

    @Test
    fun `test right-left rotation scenario`() {
        val tree = RedBlackTree<Int>()

        // Вставка узлов, которая должна вызвать право-левый поворот
        tree.add(1)  // корень: 1
        tree.add(3)  // правый потомок 1: 3
        tree.add(2)  // левый потомок 3: 2 -> должно вызвать право-левый поворот

        val root = getRoot(tree)
        Assertions.assertNotNull(root, "Корень не должен быть null")
        Assertions.assertEquals(2, getNodeData(root), "Корень должен быть 2 после право-левого поворота")
        Assertions.assertFalse(isNodeRed(root!!), "Корень должен быть черным")

        val leftChild = getNodeLeft(root)
        Assertions.assertNotNull(leftChild, "Левый потомок не должен быть null")
        Assertions.assertEquals(1, getNodeData(leftChild), "Левый потомок должен быть 1")
        Assertions.assertTrue(isNodeRed(leftChild!!), "Левый потомок должен быть красным")

        val rightChild = getNodeRight(root)
        Assertions.assertNotNull(rightChild, "Правый потомок не должен быть null")
        Assertions.assertEquals(3, getNodeData(rightChild), "Правый потомок должен быть 3")
        Assertions.assertTrue(isNodeRed(rightChild!!), "Правый потомок должен быть красным")
    }

    fun <T : Comparable<T>> RedBlackTree<T>.toList(): List<T> {
        val result = mutableListOf<T>()
        this.forEachOrdered { result.add(it) }
        return result
    }
}
