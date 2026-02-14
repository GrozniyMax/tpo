package tpo.maxim.part1

/**
 * Реализация красно-черного дерева, реализующего интерфейс Set<T>.
 * Красно-черное дерево - это самобалансирующееся двоичное дерево поиска,
 * в котором каждый узел имеет цвет (красный или черный) и соблюдаются
 * определенные свойства, которые обеспечивают сбалансированность дерева.
 */
class RedBlackTree<T : Comparable<T>> : Set<T> {

    class Node<T> internal constructor(var data: T) {
        var left: Node<T>? = null
        var right: Node<T>? = null
        var parent: Node<T>? = null
        var isRed: Boolean = true
    }

    private var root: Node<T>? = null
    private val TNULL: Node<T> = Node(null as T).apply { isRed = false }
    private var _size: Int = 0

    /**
     * Конструктор для создания пустого красно-черного дерева.
     */
    init {
        TNULL.left = TNULL
        TNULL.right = TNULL
    }

    /**
     * Добавляет элемент в множество.
     *
     * @param element Элемент для добавления.
     * @return true если элемент был добавлен, false если элемент уже существует.
     */
    override fun add(element: T): Boolean {
        if (contains(element)) {
            return false
        }
        
        val node = Node(element)
        node.left = TNULL
        node.right = TNULL
        
        var y: Node<T>? = null
        var x = this.root
        
        // Найдем место для вставки
        while (x != TNULL && x != null) {
            y = x
            x = if (node.data < x.data) {
                x.left
            } else {
                x.right
            }
        }
        
        node.parent = y
        when {
            y == null -> root = node // Дерево пустое
            node.data < y.data -> y.left = node
            else -> y.right = node
        }
        
        _size++
        
        // Если новый узел корень, то он должен быть черным
        if (node.parent == null) {
            node.isRed = false
            return true
        }
        
        // Если родитель нового узла корень, то ничего не нужно делать
        if (node.parent?.parent == null) {
            return true
        }
        
        // Исправляем возможные нарушения свойств красно-черного дерева
        fixInsert(node)
        return true
    }

    private fun fixInsert(k: Node<T>) {
        var node = k
        while (node.parent?.isRed == true) {
            if (node.parent === node.parent?.parent?.left) {
                val u = node.parent?.parent?.right // Дядя
                
                if (u != TNULL && u?.isRed == true) { // Случай 1: дядя красный
                    u?.isRed = false
                    node.parent?.isRed = false
                    node.parent?.parent?.isRed = true
                    node = node.parent?.parent ?: break
                } else {
                    if (node === node.parent?.right) { // Случай 2: node - правый ребенок
                        node = node.parent ?: break
                        leftRotate(node)
                    }
                    // Случай 3: node - левый ребенок
                    node.parent?.isRed = false
                    node.parent?.parent?.isRed = true
                    node.parent?.parent?.let { rightRotate(it) }
                }
            } else {
                val u = node.parent?.parent?.left // Дядя
                
                if (u != TNULL && u?.isRed == true) { // Случай 1: дядя красный
                    u?.isRed = false
                    node.parent?.isRed = false
                    node.parent?.parent?.isRed = true
                    node = node.parent?.parent ?: break
                } else {
                    if (node === node.parent?.left) { // Случай 2: node - левый ребенок
                        node = node.parent ?: break
                        rightRotate(node)
                    }
                    // Случай 3: node - правый ребенок
                    node.parent?.isRed = false
                    node.parent?.parent?.isRed = true
                    node.parent?.parent?.let { leftRotate(it) }
                }
            }
        }
        root?.isRed = false // Корень всегда черный
    }

    private fun leftRotate(x: Node<T>) {
        val y = x.right ?: return
        x.right = y.left
        if (y.left != TNULL) {
            y.left?.parent = x
        }
        y.parent = x.parent
        when {
            x.parent == null -> root = y
            x === x.parent?.left -> x.parent?.left = y
            else -> x.parent?.right = y
        }
        y.left = x
        x.parent = y
    }

    private fun rightRotate(x: Node<T>) {
        val y = x.left ?: return
        x.left = y.right
        if (y.right != TNULL) {
            y.right?.parent = x
        }
        y.parent = x.parent
        when {
            x.parent == null -> root = y
            x === x.parent?.right -> x.parent?.right = y
            else -> x.parent?.left = y
        }
        y.right = x
        x.parent = y
    }

    /**
     * Проверяет, содержится ли элемент в множестве.
     *
     * @param element Элемент для поиска.
     * @return true если элемент содержится в множестве, иначе false.
     */
    override fun contains(element: T): Boolean {
        var current = root
        while (current != null && current != TNULL) {
            val comparison = element.compareTo(current.data)
            when {
                comparison < 0 -> current = current.left
                comparison > 0 -> current = current.right
                else -> return true
            }
        }
        return false
    }

    /**
     * Удаляет элемент из множества.
     *
     * @param element Элемент для удаления.
     * @return true если элемент был удален, false если элемент не найден.
     */
    override fun remove(element: T): Boolean {
        if (!contains(element)) {
            return false
        }
        
        deleteNodeHelper(root ?: return false, element)
        _size--
        return true
    }

    private fun deleteNodeHelper(node: Node<T>?, key: T): Node<T>? {
        var z: Node<T>? = TNULL
        var current = node
        
        // Найдем узел для удаления
        while (current != TNULL && current != null) {
            val comparison = key.compareTo(current.data)
            when {
                comparison == 0 -> {
                    z = current
                    break
                }
                comparison < 0 -> current = current.left
                else -> current = current.right
            }
        }
        
        if (z == TNULL) return node ?: TNULL
        
        var y = z
        var yOriginalColor = y?.isRed ?: false
        val x: Node<T>?
        
        when {
            z?.left == TNULL -> {
                x = z?.right
                rbTransplant(z ?: return TNULL, z.right ?: return TNULL)
            }
            z?.right == TNULL -> {
                x = z?.left
                rbTransplant(z ?: return TNULL, z.left ?: return TNULL)
            }
            else -> {
                y = minimum(z?.right ?: return TNULL)
                yOriginalColor = y.isRed
                x = y.right
                
                if (y.parent === z) {
                    x?.parent = y
                } else {
                    rbTransplant(y, y.right ?: return TNULL)
                    y.right = z.right
                    y.right?.parent = y
                }
                
                rbTransplant(z ?: return TNULL, y)
                y.left = z.left
                y.left?.parent = y
                y.isRed = z.isRed
            }
        }
        
        if (yOriginalColor == false) {
            x?.let { fixDelete(it) }
        }
        
        return x
    }

    private fun rbTransplant(u: Node<T>, v: Node<T>) {
        when {
            u.parent == null -> root = v
            u === u.parent?.left -> u.parent?.left = v
            else -> u.parent?.right = v
        }
        v.parent = u.parent
    }

    private fun minimum(node: Node<T>): Node<T> {
        var current = node
        while (current.left != TNULL && current.left != null) {
            current = current.left ?: return current
        }
        return current
    }

    private fun fixDelete(x: Node<T>) {
        var node = x
        while (node !== root && node.isRed == false) {
            if (node === node.parent?.left) {
                var w = node.parent?.right ?: break
                if (w.isRed) {
                    // Случай 1: брат w красный
                    w.isRed = false
                    node.parent?.isRed = true
                    leftRotate(node.parent ?: break)
                    w = node.parent?.right ?: break
                }
                if ((w.left?.isRed == false) && (w.right?.isRed == false)) {
                    // Случай 2: оба ребенка w черные
                    w.isRed = true
                    node = node.parent ?: break
                } else {
                    if (w.right?.isRed == false) {
                        // Случай 3: правый ребенок w черный, левый - красный
                        w.left?.isRed = false
                        w.isRed = true
                        rightRotate(w)
                        w = node.parent?.right ?: break
                    }
                    // Случай 4: правый ребенок w красный
                    w.isRed = node.parent?.isRed ?: false
                    node.parent?.isRed = false
                    w.right?.isRed = false
                    node.parent?.let { leftRotate(it) }
                    node = root ?: break
                }
            } else {
                var w = node.parent?.left ?: break
                if (w.isRed) {
                    // Случай 1: брат w красный
                    w.isRed = false
                    node.parent?.isRed = true
                    rightRotate(node.parent ?: break)
                    w = node.parent?.left ?: break
                }
                if ((w.right?.isRed == false) && (w.left?.isRed == false)) {
                    // Случай 2: оба ребенка w черные
                    w.isRed = true
                    node = node.parent ?: break
                } else {
                    if (w.left?.isRed == false) {
                        // Случай 3: левый ребенок w черный, правый - красный
                        w.right?.isRed = false
                        w.isRed = true
                        leftRotate(w)
                        w = node.parent?.left ?: break
                    }
                    // Случай 4: левый ребенок w красный
                    w.isRed = node.parent?.isRed ?: false
                    node.parent?.isRed = false
                    w.left?.isRed = false
                    node.parent?.let { rightRotate(it) }
                    node = root ?: break
                }
            }
        }
        node.isRed = false
    }

    /**
     * Возвращает количество элементов в множестве.
     */
    override val size: Int
        get() = _size

    /**
     * Проверяет, пусто ли множество.
     */
    override fun isEmpty(): Boolean = _size == 0

    /**
     * Возвращает итератор по элементам множества.
     */
    override fun iterator(): Iterator<T> {
        val elements = mutableListOf<T>()
        inOrderTraversal(root) { elements.add(it) }
        return elements.iterator()
    }

    private fun inOrderTraversal(node: Node<T>?, visit: (T) -> Unit) {
        if (node != null && node != TNULL) {
            inOrderTraversal(node.left, visit)
            visit(node.data)
            inOrderTraversal(node.right, visit)
        }
    }

    /**
     * Создает новое множество, содержащее все элементы текущего множества
     * и дополнительно указанный элемент.
     */
    override operator fun plus(element: T): Set<T> {
        val newSet = RedBlackTree<T>()
        for (item in this) {
            newSet.add(item)
        }
        newSet.add(element)
        return newSet
    }

    /**
     * Создает новое множество, содержащее все элементы текущего множества
     * кроме указанного элемента.
     */
    override operator fun minus(element: T): Set<T> {
        val newSet = RedBlackTree<T>()
        for (item in this) {
            if (item != element) {
                newSet.add(item)
            }
        }
        return newSet
    }

    /**
     * Проверяет, содержатся ли все элементы указанного множества
     * в текущем множестве.
     */
    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*>) return false
        return this.size == other.size && this.containsAll(other)
    }

    override fun hashCode(): Int {
        var result = 0
        for (element in this) {
            result += element?.hashCode() ?: 0
        }
        return result
    }

    override fun toString(): String {
        return this.joinToString(prefix = "[", postfix = "]")
    }
}
