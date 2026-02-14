package tpo.maxim.part1

/**
 * Реализация красно-черного дерева с упрощенным интерфейсом.
 * Красно-черное дерево - это самобалансирующееся двоичное дерево поиска,
 * которое гарантирует O(log n) время выполнения основных операций.
 */
class RedBlackTree<T : Comparable<T>> {

    private class Node<T>(val data: T) {
        var left: Node<T>? = null
        var right: Node<T>? = null
        var parent: Node<T>? = null
        var isRed: Boolean = true
    }

    private var root: Node<T>? = null

    /**
     * Добавляет элемент в дерево.
     *
     * @param element Элемент для добавления.
     * @return true если элемент был добавлен, false если элемент уже существует.
     */
    fun add(element: T): Boolean {
        if (contains(element)) {
            return false
        }
        
        val newNode = Node(element)
        insertNode(newNode)
        fixInsert(newNode)
        return true
    }

    private fun insertNode(node: Node<T>) {
        var current = root
        var parent: Node<T>? = null
        
        // Найдем место для вставки
        while (current != null) {
            parent = current
            current = if (node.data < current.data) {
                current.left
            } else {
                current.right
            }
        }
        
        node.parent = parent
        when {
            parent == null -> root = node  // Дерево пустое
            node.data < parent.data -> parent.left = node
            else -> parent.right = node
        }
        
        node.isRed = true
    }

    private fun fixInsert(node: Node<T>) {
        var current = node
        
        // Исправляем возможные нарушения свойств красно-черного дерева
        while (current.parent?.isRed == true) {
            val parent = current.parent!!
            val grandParent = parent.parent
            
            if (parent === grandParent?.left) {
                val uncle = grandParent.right
                
                // Случай 1: дядя красный
                if (uncle?.isRed == true) {
                    parent.isRed = false
                    uncle.isRed = false
                    grandParent.isRed = true
                    current = grandParent
                } else {
                    // Случай 2: current - правый ребенок
                    if (current === parent.right) {
                        current = parent
                        rotateLeft(current)
                    }
                    // Случай 3: current - левый ребенок
                    current.parent?.isRed = false
                    current.parent?.parent?.isRed = true
                    current.parent?.parent?.let { rotateRight(it) }
                }
            } else {
                val uncle = grandParent?.left
                
                // Случай 1: дядя красный
                if (uncle?.isRed == true) {
                    parent.isRed = false
                    uncle.isRed = false
                    grandParent.isRed = true
                    current = grandParent
                } else {
                    // Случай 2: current - левый ребенок
                    if (current === parent.left) {
                        current = parent
                        rotateRight(current)
                    }
                    // Случай 3: current - правый ребенок
                    current.parent?.isRed = false
                    current.parent?.parent?.isRed = true
                    current.parent?.parent?.let { rotateLeft(it) }
                }
            }
        }
        
        // Корень всегда черный
        root?.isRed = false
    }

    private fun rotateLeft(node: Node<T>) {
        val rightChild = node.right ?: return
        node.right = rightChild.left
        
        if (rightChild.left != null) {
            rightChild.left?.parent = node
        }
        
        rightChild.parent = node.parent
        
        when {
            node.parent == null -> root = rightChild
            node === node.parent?.left -> node.parent?.left = rightChild
            else -> node.parent?.right = rightChild
        }
        
        rightChild.left = node
        node.parent = rightChild
    }

    private fun rotateRight(node: Node<T>) {
        val leftChild = node.left ?: return
        node.left = leftChild.right
        
        if (leftChild.right != null) {
            leftChild.right?.parent = node
        }
        
        leftChild.parent = node.parent
        
        when {
            node.parent == null -> root = leftChild
            node === node.parent?.right -> node.parent?.right = leftChild
            else -> node.parent?.left = leftChild
        }
        
        leftChild.right = node
        node.parent = leftChild
    }

    /**
     * Проверяет, содержится ли элемент в дереве.
     *
     * @param element Элемент для поиска.
     * @return true если элемент содержится в дереве, иначе false.
     */
    fun contains(element: T): Boolean {
        var current = root
        while (current != null) {
            when {
                element < current.data -> current = current.left
                element > current.data -> current = current.right
                else -> return true
            }
        }
        return false
    }

    /**
     * Удаляет элемент из дерева.
     *
     * @param element Элемент для удаления.
     * @return true если элемент был удален, false если элемент не найден.
     */
    fun remove(element: T): Boolean {
        val node = findNode(element) ?: return false
        deleteNode(node)
        return true
    }

    private fun findNode(element: T): Node<T>? {
        var current = root
        while (current != null) {
            when {
                element < current.data -> current = current.left
                element > current.data -> current = current.right
                else -> return current
            }
        }
        return null
    }

    private fun deleteNode(node: Node<T>) {
        var toDelete = node
        var originalColor = toDelete.isRed
        
        val replacement = when {
            node.left == null -> {
                val replacement = node.right
                transplant(node, replacement)
                replacement
            }
            node.right == null -> {
                val replacement = node.left
                transplant(node, replacement)
                replacement
            }
            else -> {
                // Найдем преемника
                val successor = findMinimum(node.right!!)
                originalColor = successor.isRed
                val replacement = successor.right
                
                if (successor.parent === node) {
                    replacement?.parent = successor
                } else {
                    transplant(successor, successor.right)
                    successor.right = node.right
                    successor.right?.parent = successor
                }
                
                transplant(node, successor)
                successor.left = node.left
                successor.left?.parent = successor
                successor.isRed = node.isRed
                replacement
            }
        }
        
        // Если удалили черный узел, нужно восстановить свойства дерева
        if (!originalColor) {
            replacement?.let { fixDelete(it) }
        }
    }

    private fun transplant(oldNode: Node<T>, newNode: Node<T>?) {
        when {
            oldNode.parent == null -> root = newNode
            oldNode === oldNode.parent?.left -> oldNode.parent?.left = newNode
            else -> oldNode.parent?.right = newNode
        }
        newNode?.parent = oldNode.parent
    }

    private fun findMinimum(node: Node<T>): Node<T> {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    private fun fixDelete(node: Node<T>?) {
        var current = node ?: return
        
        while (current !== root && !current.isRed) {
            if (current === current.parent?.left) {
                var sibling = current.parent?.right ?: break
                
                // Случай 1: брат красный
                if (sibling.isRed) {
                    sibling.isRed = false
                    current.parent?.isRed = true
                    rotateLeft(current.parent!!)
                    sibling = current.parent?.right ?: break
                }
                
                // Случай 2: оба ребенка брата черные
                if (!(sibling.left?.isRed ?: false) && !(sibling.right?.isRed ?: false)) {
                    sibling.isRed = true
                    current = current.parent!!
                } else {
                    // Случай 3: правый ребенок брата черный
                    if (!(sibling.right?.isRed ?: false)) {
                        sibling.left?.isRed = false
                        sibling.isRed = true
                        rotateRight(sibling)
                        sibling = current.parent?.right ?: break
                    }
                    
                    // Случай 4: правый ребенок брата красный
                    sibling.isRed = current.parent?.isRed ?: false
                    current.parent?.isRed = false
                    sibling.right?.isRed = false
                    current.parent?.let { rotateLeft(it) }
                    current = root!!
                }
            } else {
                var sibling = current.parent?.left ?: break
                
                // Случай 1: брат красный
                if (sibling.isRed) {
                    sibling.isRed = false
                    current.parent?.isRed = true
                    rotateRight(current.parent!!)
                    sibling = current.parent?.left ?: break
                }
                
                // Случай 2: оба ребенка брата черные
                if (!(sibling.right?.isRed ?: false) && !(sibling.left?.isRed ?: false)) {
                    sibling.isRed = true
                    current = current.parent!!
                } else {
                    // Случай 3: левый ребенок брата черный
                    if (!(sibling.left?.isRed ?: false)) {
                        sibling.right?.isRed = false
                        sibling.isRed = true
                        rotateLeft(sibling)
                        sibling = current.parent?.left ?: break
                    }
                    
                    // Случай 4: левый ребенок брата красный
                    sibling.isRed = current.parent?.isRed ?: false
                    current.parent?.isRed = false
                    sibling.left?.isRed = false
                    current.parent?.let { rotateRight(it) }
                    current = root!!
                }
            }
        }
        
        current.isRed = false
    }

    /**
     * Выполняет обход дерева в порядке возрастания и применяет функцию к каждому элементу.
     *
     * @param action Функция, которая будет применена к каждому элементу.
     */
    fun forEachOrdered(action: (T) -> Unit) {
        inOrderTraversal(root, action)
    }

    private fun inOrderTraversal(node: Node<T>?, action: (T) -> Unit) {
        if (node != null) {
            inOrderTraversal(node.left, action)
            action(node.data)
            inOrderTraversal(node.right, action)
        }
    }
}
