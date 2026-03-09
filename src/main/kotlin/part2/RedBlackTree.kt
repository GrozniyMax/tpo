package part2

enum class Color {
    RED,
    BLACK,
}

data class Node<T>(
    var value: T,
    var color: Color = Color.RED,
    var left: Node<T>? = null,
    var right: Node<T>? = null,
    var parent: Node<T>? = null,
)

class RedBlackTree<T : Comparable<T>> {
    private var root: Node<T>? = null

    fun add(value: T) {
        val newNode = Node(value)

        val currentRoot = root
        if (currentRoot == null) {
            root = newNode
            newNode.color = Color.BLACK // Корень всегда черный
            return
        }

        var current: Node<T>? = currentRoot
        var parent: Node<T>? = null

        while (current != null) {
            parent = current
            current = when {
                value < current.value -> current.left
                value > current.value -> current.right
                else -> return // Значение уже существует
            }
        }

        newNode.parent = parent
        parent?.let {
            if (value < it.value) {
                it.left = newNode
            } else {
                it.right = newNode
            }
        }

        // Балансировка после вставки
        balanceAfterInsert(newNode)
    }

    private fun balanceAfterInsert(node: Node<T>) {
        var current = node

        while (current != root && current.parent?.color == Color.RED) {
            val parent = current.parent ?: break
            val grandparent = parent.parent ?: break

            if (parent == grandparent.left) {
                val uncle = grandparent.right

                // Случай 1: Дядя красный
                if (uncle?.color == Color.RED) {
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandparent.color = Color.RED
                    current = grandparent
                } else {
                    val currentParent = current.parent
                    // Случай 2: Дядя черный и узел - правый ребенок
                    if (current == parent.right) {
                        current = parent
                        rotateLeft(current)
                    }

                    // Случай 3: Дядя черный и узел - левый ребенок
                    if (currentParent != null) {
                        currentParent.color = Color.BLACK
                        currentParent.parent?.let {
                            it.color = Color.RED
                            rotateRight(it)
                        }
                    }
                }
            } else {
                val uncle = grandparent.left

                // Случай 1: Дядя красный (зеркальный)
                if (uncle?.color == Color.RED) {
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandparent.color = Color.RED
                    current = grandparent
                } else {
                    // Случай 2: Дядя черный и узел - левый ребенок (зеркальный)
                    if (current == parent.left) {
                        current = parent
                        rotateRight(current)
                    }

                    // Случай 3: Дядя черный и узел - правый ребенок (зеркальный)
                    val currentParent = current.parent
                    if (currentParent != null) {
                        currentParent.color = Color.BLACK
                        currentParent.parent?.let {
                            it.color = Color.RED
                            rotateLeft(it)
                        }
                    }
                }
            }
        }

        root?.color = Color.BLACK
    }

    private fun rotateLeft(node: Node<T>) {
        val rightChild = node.right ?: return

        node.right = rightChild.left
        rightChild.left?.parent = node

        rightChild.parent = node.parent

        val nodeParent = node.parent
        when {
            nodeParent == null -> root = rightChild
            node == nodeParent.left -> nodeParent.left = rightChild
            else -> nodeParent.right = rightChild
        }

        rightChild.left = node
        node.parent = rightChild
    }

    private fun rotateRight(node: Node<T>) {
        val leftChild = node.left ?: return

        node.left = leftChild.right
        leftChild.right?.parent = node

        leftChild.parent = node.parent

        val nodeParent = node.parent
        when {
            nodeParent == null -> root = leftChild
            node == nodeParent.right -> nodeParent.right = leftChild
            else -> nodeParent.left = leftChild
        }

        leftChild.right = node
        node.parent = leftChild
    }

    fun contains(value: T): Boolean {
        return findNode(value) != null
    }

    fun delete(value: T) {
        val nodeToDelete = findNode(value) ?: return

        var replacementNode: Node<T>?
        var nodeToFix: Node<T>?
        var originalColor = nodeToDelete.color

        when {
            nodeToDelete.left == null -> {
                replacementNode = nodeToDelete.right
                transplant(nodeToDelete, nodeToDelete.right)
                nodeToFix = replacementNode
            }

            nodeToDelete.right == null -> {
                replacementNode = nodeToDelete.left
                transplant(nodeToDelete, nodeToDelete.left)
                nodeToFix = replacementNode
            }

            else -> {
                replacementNode = minimum(nodeToDelete.right) ?: return

                originalColor = replacementNode.color
                nodeToFix = replacementNode.right

                if (replacementNode.parent == nodeToDelete) {
                    nodeToFix?.parent = replacementNode
                } else {
                    transplant(replacementNode, replacementNode.right)
                    replacementNode.right = nodeToDelete.right
                    replacementNode.right?.parent = replacementNode
                }

                transplant(nodeToDelete, replacementNode)
                replacementNode.left = nodeToDelete.left
                replacementNode.left?.parent = replacementNode
                replacementNode.color = nodeToDelete.color
            }
        }

        if (originalColor == Color.BLACK) {
            nodeToFix?.let { balanceAfterDelete(it) }
        }
    }

    private fun findNode(value: T): Node<T>? {
        var current = root

        while (current != null) {
            current = when {
                value < current.value -> current.left
                value > current.value -> current.right
                else -> return current
            }
        }

        return null
    }

    private fun minimum(node: Node<T>?): Node<T>? {
        var current = node
        while (current?.left != null) {
            current = current.left
        }
        return current
    }

    private fun transplant(oldNode: Node<T>, newNode: Node<T>?) {
        val oldNodeParent = oldNode.parent
        when {
            oldNodeParent == null -> root = newNode
            else -> oldNodeParent.left = newNode
        }
        newNode?.parent = oldNodeParent
    }

    private fun balanceAfterDelete(node: Node<T>) {
        var current = node

        while (current != root && current.color == Color.BLACK) {
            val currentParent = current.parent ?: break

            if (current == currentParent.left) {
                var sibling = currentParent.right

                if (sibling?.color == Color.RED) {
                    // Случай 1: Брат красный
                    sibling.color = Color.BLACK
                    currentParent.color = Color.RED
                    rotateLeft(currentParent)
                } else if (sibling?.left?.color == Color.BLACK && sibling.right?.color == Color.BLACK) {
                    // Случай 2: Брат черный и оба его ребенка черные
                    sibling.color = Color.RED
                    current = currentParent
                } else if (sibling?.right?.color == Color.BLACK) {
                    // Случай 3: Брат черный, левый ребенок красный, правый черный
                    sibling.left?.color = Color.BLACK
                    sibling.color = Color.RED
                    rotateRight(sibling)
                } else {
                    // Случай 4: Брат черный, правый ребенок красный
                    sibling?.color = currentParent.color
                    currentParent.color = Color.BLACK
                    sibling?.right?.color = Color.BLACK
                    rotateLeft(currentParent)
                    root?.let { current = it }
                }
            }
        }

        current.color = Color.BLACK
    }

    fun toList(): List<T> {
        val result = mutableListOf<T>()
        inOrderTraversal(root, result)
        return result
    }

    private fun inOrderTraversal(node: Node<T>?, result: MutableList<T>) {
        if (node == null) return

        inOrderTraversal(node.left, result)
        result.add(node.value)
        inOrderTraversal(node.right, result)
    }

    fun isEmpty(): Boolean = root == null

    fun size(): Int = countNodes(root)

    private fun countNodes(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + countNodes(node.left) + countNodes(node.right)
    }
}
