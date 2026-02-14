package tpo.maxim.part1

/**
 * Реализация красно-черного дерева с упрощенным интерфейсом.
 * Красно-черное дерево - это самобалансирующееся двоичное дерево поиска,
 * в котором каждый узел имеет цвет (красный или черный) и соблюдаются
 * определенные свойства, которые обеспечивают сбалансированность дерева.
 */
class RedBlackTree<T : Comparable<T>> {

    private class Node<T>(val data: T) {
        var left: Node<T>? = null
        var right: Node<T>? = null
        var parent: Node<T>? = null
        var isRed: Boolean = true
    }

    private var root: Node<T>? = null
    private val NULL: Node<T> = Node<T>(null as T).apply { isRed = false }
    
    init {
        NULL.left = NULL
        NULL.right = NULL
        NULL.parent = NULL
    }

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
        
        val node = Node(element)
        node.left = NULL
        node.right = NULL
        
        var y: Node<T>? = NULL
        var x = this.root
        
        // Найдем место для вставки
        while (x != NULL && x != null) {
            y = x
            x = if (element < x.data) {
                x.left
            } else {
                x.right
            }
        }
        
        node.parent = y
        when {
            y == NULL -> root = node // Дерево пустое
            element < y!!.data -> y.left = node
            else -> y.right = node
        }
        
        // Если новый узел корень, то он должен быть черным
        if (node.parent == NULL) {
            node.isRed = false
            return true
        }
        
        // Если родитель нового узла корень, то ничего не нужно делать
        if (node.parent?.parent == NULL) {
            return true
        }
        
        // Исправляем возможные нарушения свойств красно-черного дерева
        fixInsert(node)
        return true
    }

    private fun fixInsert(node: Node<T>) {
        var current = node
        while (current.parent?.isRed == true) {
            if (current.parent === current.parent?.parent?.left) {
                val uncle = current.parent?.parent?.right
                
                if (uncle != NULL && uncle?.isRed == true) { // Случай 1: дядя красный
                    uncle.isRed = false
                    current.parent?.isRed = false
                    current.parent?.parent?.isRed = true
                    current = current.parent?.parent ?: break
                } else {
                    if (current === current.parent?.right) { // Случай 2: current - правый ребенок
                        current = current.parent ?: break
                        leftRotate(current)
                    }
                    // Случай 3: current - левый ребенок
                    current.parent?.isRed = false
                    current.parent?.parent?.isRed = true
                    current.parent?.parent?.let { rightRotate(it) }
                }
            } else {
                val uncle = current.parent?.parent?.left
                
                if (uncle != NULL && uncle?.isRed == true) { // Случай 1: дядя красный
                    uncle.isRed = false
                    current.parent?.isRed = false
                    current.parent?.parent?.isRed = true
                    current = current.parent?.parent ?: break
                } else {
                    if (current === current.parent?.left) { // Случай 2: current - левый ребенок
                        current = current.parent ?: break
                        rightRotate(current)
                    }
                    // Случай 3: current - правый ребенок
                    current.parent?.isRed = false
                    current.parent?.parent?.isRed = true
                    current.parent?.parent?.let { leftRotate(it) }
                }
            }
        }
        root?.isRed = false // Корень всегда черный
    }

    private fun leftRotate(x: Node<T>) {
        val y = x.right ?: return
        x.right = y.left
        if (y.left != NULL) {
            y.left?.parent = x
        }
        y.parent = x.parent
        when {
            x.parent == NULL -> root = y
            x === x.parent?.left -> x.parent?.left = y
            else -> x.parent?.right = y
        }
        y.left = x
        x.parent = y
    }

    private fun rightRotate(x: Node<T>) {
        val y = x.left ?: return
        x.left = y.right
        if (y.right != NULL) {
            y.right?.parent = x
        }
        y.parent = x.parent
        when {
            x.parent == NULL -> root = y
            x === x.parent?.right -> x.parent?.right = y
            else -> x.parent?.left = y
        }
        y.right = x
        x.parent = y
    }

    /**
     * Проверяет, содержится ли элемент в дереве.
     *
     * @param element Элемент для поиска.
     * @return true если элемент содержится в дереве, иначе false.
     */
    fun contains(element: T): Boolean {
        var current = root
        while (current != null && current != NULL) {
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
        val node = searchNode(element) ?: return false
        deleteNode(node)
        return true
    }

    private fun searchNode(element: T): Node<T>? {
        var current = root
        while (current != null && current != NULL) {
            when {
                element < current.data -> current = current.left
                element > current.data -> current = current.right
                else -> return current
            }
        }
        return null
    }

    private fun deleteNode(node: Node<T>) {
        var y = node
        var yOriginalColor = y.isRed
        val x: Node<T>?
        
        when {
            node.left == NULL -> {
                x = node.right
                transplant(node, node.right ?: return)
            }
            node.right == NULL -> {
                x = node.left
                transplant(node, node.left ?: return)
            }
            else -> {
                y = minimum(node.right ?: return)
                yOriginalColor = y.isRed
                x = y.right
                
                if (y.parent === node) {
                    x?.parent = y
                } else {
                    transplant(y, y.right ?: return)
                    y.right = node.right
                    y.right?.parent = y
                }
                
                transplant(node, y)
                y.left = node.left
                y.left?.parent = y
                y.isRed = node.isRed
            }
        }
        
        if (!yOriginalColor) {
            x?.let { fixDelete(it) }
        }
    }

    private fun transplant(u: Node<T>, v: Node<T>?) {
        when {
            u.parent == NULL -> root = v
            u === u.parent?.left -> u.parent?.left = v
            else -> u.parent?.right = v
        }
        v?.parent = u.parent
    }

    private fun minimum(node: Node<T>): Node<T> {
        var current = node
        while (current.left != NULL && current.left != null) {
            current = current.left ?: return current
        }
        return current
    }

    private fun fixDelete(x: Node<T>) {
        var current = x
        while (current !== root && !current.isRed) {
            if (current === current.parent?.left) {
                var w = current.parent?.right ?: break
                if (w.isRed) {
                    // Случай 1: брат w красный
                    w.isRed = false
                    current.parent?.isRed = true
                    leftRotate(current.parent ?: break)
                    w = current.parent?.right ?: break
                }
                if (!(w.left?.isRed ?: false) && !(w.right?.isRed ?: false)) {
                    // Случай 2: оба ребенка w черные
                    w.isRed = true
                    current = current.parent ?: break
                } else {
                    if (!(w.right?.isRed ?: false)) {
                        // Случай 3: правый ребенок w черный, левый - красный
                        w.left?.isRed = false
                        w.isRed = true
                        rightRotate(w)
                        w = current.parent?.right ?: break
                    }
                    // Случай 4: правый ребенок w красный
                    w.isRed = current.parent?.isRed ?: false
                    current.parent?.isRed = false
                    w.right?.isRed = false
                    current.parent?.let { leftRotate(it) }
                    current = root ?: break
                }
            } else {
                var w = current.parent?.left ?: break
                if (w.isRed) {
                    // Случай 1: брат w красный
                    w.isRed = false
                    current.parent?.isRed = true
                    rightRotate(current.parent ?: break)
                    w = current.parent?.left ?: break
                }
                if (!(w.right?.isRed ?: false) && !(w.left?.isRed ?: false)) {
                    // Случай 2: оба ребенка w черные
                    w.isRed = true
                    current = current.parent ?: break
                } else {
                    if (!(w.left?.isRed ?: false)) {
                        // Случай 3: левый ребенок w черный, правый - красный
                        w.right?.isRed = false
                        w.isRed = true
                        leftRotate(w)
                        w = current.parent?.left ?: break
                    }
                    // Случай 4: левый ребенок w красный
                    w.isRed = current.parent?.isRed ?: false
                    current.parent?.isRed = false
                    w.left?.isRed = false
                    current.parent?.let { rightRotate(it) }
                    current = root ?: break
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
        if (node != null && node != NULL) {
            inOrderTraversal(node.left, action)
            action(node.data)
            inOrderTraversal(node.right, action)
        }
    }
}
