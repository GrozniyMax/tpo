package tpo.maxim.part1


/**
 * Реализация красно-черного дерева.
 * Красно-черное дерево - это самобалансирующееся двоичное дерево поиска,
 * в котором каждый узел имеет цвет (красный или черный) и соблюдаются
 * определенные свойства, которые обеспечивают сбалансированность дерева.
 */
class RedBlackTree: Set<Int> {

    class Node internal constructor(var data: Int) {
        var left: Node? = null
        var right: Node? = null
        var parent: Node? = null
        var isRed: Boolean = true
    }

    private var root: Node?
    var TNULL: Node // Листовой узел

    /**
     * Конструктор для создания пустого красно-черного дерева.
     * Инициализирует корень и листовой узел.
     */
    init {
        TNULL = Node(0)
        TNULL.isRed = false
        root = TNULL
    }

    /**
     * Поиск узла с заданным ключом в дереве.
     *
     * @param key Ключ для поиска.
     * @return Узел с заданным ключом или TNULL, если узел не найден.
     */
    fun search(key: Int): Node? {
        return searchTreeHelper(this.root!!, key)
    }

    /**
     * Возвращает корневой узел
     *
     * @return корневой узел
     */
    fun getRoot(): Node {
        return root!!
    }

    /**
     * Выполнение обхода дерева в порядке возрастания.
     *
     * @return данные узлов в отсортированном порядке
     */
    fun inOrder(): MutableList<Node?> {
        val nodes = ArrayList<Node?>()
        inOrderHelper(nodes, this.root!!)
        return nodes
    }

    private fun inOrderHelper(nodes: MutableList<Node?>, node: Node) {
        if (node !== TNULL) {
            inOrderHelper(nodes, node.left!!)
            nodes.add(node)
            inOrderHelper(nodes, node.right!!)
        }
    }

    private fun searchTreeHelper(node: Node, key: Int): Node? {
        if (node === TNULL || key == node.data) {
            return node
        }

        if (key < node.data) {
            return searchTreeHelper(node.left!!, key)
        }
        return searchTreeHelper(node.right!!, key)
    }

    /**
     * Вставка узла с заданным ключом в дерево.
     *
     * @param key Ключ для вставки.
     */
    fun insert(key: Int) {
        val node = Node(key)
        node.parent = null
        node.left = TNULL
        node.right = TNULL

        var y: Node? = null
        var x = this.root

        while (x !== TNULL) {
            y = x
            if (node.data < x!!.data) {
                x = x.left
            } else {
                x = x.right
            }
        }

        node.parent = y
        if (y == null) {
            root = node // Если дерево пустое
        } else if (node.data < y.data) {
            y.left = node
        } else {
            y.right = node
        }

        if (node.parent == null) {
            node.isRed = false // Корень всегда черный
            return
        }

        if (node.parent!!.parent == null) {
            return
        }

        fixInsert(node)
    }

    private fun fixInsert(k: Node) {
        var k = k
        var u: Node? // Дядя
        while (k.parent != null && k.parent!!.isRed) {
            if (k.parent === k.parent!!.parent!!.left) {
                u = k.parent!!.parent!!.right // Дядя

                if (u !== TNULL && u!!.isRed) { // Случай 1: дядя красный
                    u.isRed = false
                    k.parent!!.isRed = false
                    k.parent!!.parent!!.isRed = true
                    k = k.parent!!.parent!!
                } else {
                    if (k === k.parent!!.right) { // Случай 2: k - правый ребенок
                        k = k.parent!!
                        leftRotate(k)
                    }
                    // Случай 3: k - левый ребенок
                    k.parent!!.isRed = false
                    k.parent!!.parent!!.isRed = true
                    rightRotate(k.parent!!.parent!!)
                }
            } else {
                u = k.parent!!.parent!!.left // Дядя

                if (u !== TNULL && u!!.isRed) { // Случай 1: дядя красный
                    u.isRed = false
                    k.parent!!.isRed = false
                    k.parent!!.parent!!.isRed = true
                    k = k.parent!!.parent!!
                } else {
                    if (k === k.parent!!.left) { // Случай 2: k - левый ребенок
                        k = k.parent!!
                        rightRotate(k)
                    }
                    // Случай 3: k - правый ребенок
                    k.parent!!.isRed = false
                    k.parent!!.parent!!.isRed = true
                    leftRotate(k.parent!!.parent!!)
                }
            }
        }
        root!!.isRed = false // Корень всегда черный
    }

    private fun leftRotate(x: Node) {
        val y = x.right
        x.right = y!!.left
        if (y.left !== TNULL) {
            y.left!!.parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            this.root = y
        } else if (x === x.parent!!.left) {
            x.parent!!.left = y
        } else {
            x.parent!!.right = y
        }
        y.left = x
        x.parent = y
    }

    // Выполнение правостороннего вращения
    private fun rightRotate(x: Node) {
        val y = x.left
        x.left = y!!.right
        if (y.right !== TNULL) {
            y.right!!.parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            this.root = y
        } else if (x === x.parent!!.right) {
            x.parent!!.right = y
        } else {
            x.parent!!.left = y
        }
        y.right = x
        x.parent = y
    }

    /**
     * Печать дерева в удобочитаемом формате.
     * Отображает структуру дерева с указанием цвета узлов.
     */
    fun printTree() {
        printTreeHelper(this.root!!, "", true)
    }

    private fun printTreeHelper(root: Node, indent: String?, last: Boolean) {
        var indent = indent
        if (root !== TNULL) {
            print(indent)
            if (last) {
                print("R----")
                indent += "   "
            } else {
                print("L----")
                indent += "|  "
            }
            println(root.data.toString() + (if (root.isRed) "(R)" else "(B)"))
            printTreeHelper(root.left!!, indent, false)
            printTreeHelper(root.right!!, indent, true)
        }
    }

    /**
     * Удаление узла с заданным ключом из дерева.
     *
     * @param data Ключ узла для удаления.
     */
    fun delete(data: Int) {
        deleteNodeHelper(this.root!!, data)
    }

    private fun deleteNodeHelper(node: Node, key: Int) {
        var node = node
        var z: Node? = TNULL
        val x: Node?
        var y: Node?
        while (node !== TNULL) {
            if (node.data == key) {
                z = node
            }

            if (node.data <= key) {
                node = node.right!!
            } else {
                node = node.left!!
            }
        }

        require(z !== TNULL) { "Node not found" }

        y = z
        var yOriginalColor = y!!.isRed
        if (z.left === TNULL) {
            x = z.right
            rbTransplant(z, z.right!!)
        } else if (z.right === TNULL) {
            x = z.left
            rbTransplant(z, z.left!!)
        } else {
            y = minimum(z.right!!)
            yOriginalColor = y.isRed
            x = y.right
            if (y.parent === z) {
                x!!.parent = y
            } else {
                rbTransplant(y, y.right!!)
                y.right = z.right
                y.right!!.parent = y
            }

            rbTransplant(z, y)
            y.left = z.left
            y.left!!.parent = y
            y.isRed = z.isRed
        }
        if (!yOriginalColor) {
            fixDelete(x!!)
        }
    }

    private fun rbTransplant(u: Node, v: Node) {
        if (u.parent == null) {
            this.root = v
        } else if (u === u.parent!!.left) {
            u.parent!!.left = v
        } else {
            u.parent!!.right = v
        }
        v.parent = u.parent
    }

    private fun minimum(node: Node): Node {
        var node = node
        while (node.left !== TNULL) {
            node = node.left!!
        }
        return node
    }

    private fun fixDelete(x: Node) {
        var x = x
        while (x !== root && !x.isRed) {
            if (x === x.parent!!.left) {
                var w = x.parent!!.right
                if (w!!.isRed) {
                    // Случай 1: дядя w красный
                    w.isRed = false
                    x.parent!!.isRed = true
                    leftRotate(x.parent!!)
                    w = x.parent!!.right
                }
                if (!w!!.left!!.isRed && !w.right!!.isRed) {
                    // Случай 2: оба ребенка дяди w черные
                    w.isRed = true
                    x = x.parent!!
                } else {
                    if (!w.right!!.isRed) {
                        // Случай 3: левый ребенок дяди w красный, правый — черный
                        w.left!!.isRed = false
                        w.isRed = true
                        rightRotate(w)
                        w = x.parent!!.right
                    }
                    // Случай 4: правый ребенок дяди w красный
                    w!!.isRed = x.parent!!.isRed
                    x.parent!!.isRed = false
                    w.right!!.isRed = false
                    leftRotate(x.parent!!)
                    x = root!!
                }
            } else {
                var w = x.parent!!.left
                if (w!!.isRed) {
                    // Случай 1: дядя w красный
                    w.isRed = false
                    x.parent!!.isRed = true
                    rightRotate(x.parent!!)
                    w = x.parent!!.left
                }
                if (!w!!.right!!.isRed && !w.left!!.isRed) {
                    // Случай 2: оба ребенка дяди w черные
                    w.isRed = true
                    x = x.parent!!
                } else {
                    if (!w.left!!.isRed) {
                        // Случай 3: правый ребенок дяди w красный, левый — черный
                        w.right!!.isRed = false
                        w.isRed = true
                        leftRotate(w)
                        w = x.parent!!.left
                    }
                    // Случай 4: левый ребенок дяди w красный
                    w!!.isRed = x.parent!!.isRed
                    x.parent!!.isRed = false
                    w.left!!.isRed = false
                    rightRotate(x.parent!!)
                    x = root!!
                }
            }
        }
        x.isRed = false
    }

    /**
     * Подсчет общего количества узлов в дереве.
     *
     * @return Общее количество узлов.
     */
    fun countNodes(): Int {
        return countNodesHelper(this.root!!)
    }

    private fun countNodesHelper(node: Node): Int {
        if (node === TNULL) {
            return 0
        }
        return 1 + countNodesHelper(node.left!!) + countNodesHelper(node.right!!)
    }

    val isValidRedBlackTree: Boolean
        /**
         * Проверка, является ли дерево корректным красно-черным деревом.
         *
         * @return true, если дерево корректно, иначе false.
         */
        get() {
            // Корень должен быть черным
            if (root!!.isRed) {
                return false
            }
            // Проверка свойств BST
            if (!isValidBST(root!!)) {
                return false
            }
            // Проверка цвета узлов
            if (!isProperlyColored(root!!)) {
                return false
            }
            // Проверка черной высоты
            if (!hasEqualBlackHeight(root!!)) {
                return false
            }
            return true
        }

    private fun isValidBST(node: Node): Boolean {
        return isValidBSTHelper(node, Int.Companion.MIN_VALUE, Int.Companion.MAX_VALUE)
    }

    private fun isValidBSTHelper(node: Node, min: Int, max: Int): Boolean {
        if (node === TNULL) {
            return true
        }
        if (node.data < min || node.data > max) {
            return false
        }
        return isValidBSTHelper(node.left!!, min, node.data) && isValidBSTHelper(node.right!!, node.data, max)
    }

    private fun isProperlyColored(node: Node): Boolean {
        if (node === TNULL) {
            return true
        }
        // Если узел красный, оба его ребенка должны быть черными
        if (node.isRed) {
            if (node.left!!.isRed || node.right!!.isRed) {
                return false
            }
        }
        // Рекурсивно проверяем левое и правое поддеревья
        return isProperlyColored(node.left!!) && isProperlyColored(node.right!!)
    }

    private fun hasEqualBlackHeight(node: Node): Boolean {
        if (node === TNULL) {
            return true
        }
        // Черная высота левого и правого поддеревьев должна быть одинаковой
        val leftBlackHeight = countBlackHeight(node.left!!)
        val rightBlackHeight = countBlackHeight(node.right!!)
        if (leftBlackHeight != rightBlackHeight) {
            return false
        }
        // Рекурсивно проверяем левое и правое поддеревья
        return hasEqualBlackHeight(node.left!!) && hasEqualBlackHeight(node.right!!)
    }

    private fun countBlackHeight(node: Node): Int {
        if (node === TNULL) {
            return 1 // Листовой узел (TNULL) считается черным
        }
        var count = if (node.isRed) 0 else 1 // Черный узел добавляет 1 к высоте
        count += countBlackHeight(node.left!!) // Рекурсивно считаем высоту
        return count
    }
}

