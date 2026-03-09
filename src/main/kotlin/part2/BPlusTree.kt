package part2

const val ORDER = 4

sealed class BPlusNode {
    val keys: MutableList<Int> = mutableListOf()
}

class LeafNode : BPlusNode() {
    val values: MutableList<String> = mutableListOf()

    var next: LeafNode? = null
}

class InternalNode : BPlusNode() {
    val children: MutableList<BPlusNode> = mutableListOf()
}

class BPlusTree {
    private var root: BPlusNode = LeafNode()

    fun search(key: Int): String? {
        val leaf = findLeaf(key)
        val index = leaf.keys.indexOf(key)
        return if (index >= 0) leaf.values[index] else null
    }

    private fun findLeaf(key: Int): LeafNode {
        var current = root

        while (!isLeaf(current)) {

            for (i in current.keys.indices) {
                if (key < current.keys[i]) {
                    current = (current as InternalNode).children[i]
                    break
                }
            }
        }

        return current as LeafNode
    }

    fun insert(key: Int, value: String) {
        val leaf = findLeaf(key)

        var insertIndex = 0
        while (insertIndex < leaf.keys.size && leaf.keys[insertIndex] < key) {
            insertIndex++
        }

        if (insertIndex < leaf.keys.size && leaf.keys[insertIndex] == key) {
            leaf.values[insertIndex] = value
            return
        }

        leaf.keys.add(insertIndex, key)
        leaf.values.add(insertIndex, value)

        if (leaf.keys.size >= ORDER * 2) {
            split(leaf)
        }
    }

    private fun split(leaf: LeafNode) {
        val newLeaf = LeafNode()

        val midIndex = ORDER
        while (leaf.keys.size > midIndex) {
            newLeaf.keys.addFirst(leaf.keys.removeAt(midIndex))
            newLeaf.values.addFirst(leaf.values.removeAt(midIndex))
        }
        newLeaf.keys.reverse()
        newLeaf.values.reverse()

        newLeaf.next = leaf.next
        leaf.next = newLeaf

        val promotedKey = newLeaf.keys[0]

        insertIntoParent(leaf, promotedKey, newLeaf)
    }

    private fun insertIntoParent(leftChild: BPlusNode, key: Int, rightChild: BPlusNode) {
        if (leftChild === root) {
            val newRoot = InternalNode()
            newRoot.keys.add(key)
            newRoot.children.add(leftChild)
            newRoot.children.add(rightChild)
            root = newRoot
            return
        }

        val parent = findParent(root, leftChild) as InternalNode

        var insertIndex = 0
        while (insertIndex < parent.keys.size && parent.keys[insertIndex] < key) {
            insertIndex++
        }

        parent.keys.add(insertIndex, key)
        parent.children.add(insertIndex + 1, rightChild)

        if (parent.keys.size >= ORDER * 2) {
            split(parent)
        }
    }

    private fun split(node: InternalNode) {
        val newNode = InternalNode()
        val midIndex = ORDER / 2

        val promotedKey = node.keys[midIndex]

        for (i in midIndex + 1 until node.keys.size) {
            newNode.keys.add(node.keys[i])
        }
        while (node.keys.size > midIndex) {
            node.keys.removeAt(midIndex)
        }

        for (i in midIndex + 1 until node.children.size) {
            newNode.children.add(node.children[i])
        }
        while (node.children.size > midIndex + 1) {
            node.children.removeAt(midIndex + 1)
        }

        insertIntoParent(node, promotedKey, newNode)
    }

    private fun findParent(current: BPlusNode, target: BPlusNode): BPlusNode? {
        if (current is LeafNode) return null

        val internal = current as InternalNode

        for (child in internal.children) {
            if (child === target) return internal
        }

        for (child in internal.children) {
            val result = findParent(child, target)
            if (result != null) return result
        }

        return null
    }

    fun delete(key: Int) {
        val leaf = findLeaf(key)
        val index = leaf.keys.indexOf(key)

        if (index < 0) {
            return
        }

        leaf.keys.removeAt(index)
        leaf.values.removeAt(index)

        if (leaf === root) {
            return
        }

        if (leaf.keys.size < ORDER - 1) {
            handleMinimumSize(leaf)
        } else {
            updateParentKeys(key, leaf)
        }

        return
    }

    private fun handleMinimumSize(leaf: LeafNode) {
        val parent = findParent(root, leaf) as? InternalNode ?: return

        val leafIndex = parent.children.indexOf(leaf)

        if (leafIndex > 0) {
            val leftSibling = parent.children[leafIndex - 1] as LeafNode
            if (leftSibling.keys.size > ORDER - 1) {
                borrowFromLeftLeaf(leaf, leftSibling, parent, leafIndex)
                return
            }
        }

        if (leafIndex < parent.children.size - 1) {
            val rightSibling = parent.children[leafIndex + 1] as LeafNode
            if (rightSibling.keys.size > ORDER - 1) {
                borrowFromRightLeaf(leaf, rightSibling, parent, leafIndex)
                return
            }
        }

        if (leafIndex > 0) {
            val leftSibling = parent.children[leafIndex - 1] as LeafNode
            mergeLeaves(leftSibling, leaf, parent, leafIndex)
        } else {
            val rightSibling = parent.children[leafIndex + 1] as LeafNode
            mergeLeaves(leaf, rightSibling, parent, leafIndex + 1)
        }
    }

    private fun borrowFromLeftLeaf(
        leaf: LeafNode,
        leftSibling: LeafNode,
        parent: InternalNode,
        leafIndex: Int
    ) {
        val borrowedKey = leftSibling.keys.removeAt(leftSibling.keys.size - 1)
        val borrowedValue = leftSibling.values.removeAt(leftSibling.values.size - 1)

        leaf.keys.add(0, borrowedKey)
        leaf.values.add(0, borrowedValue)

        parent.keys[leafIndex - 1] = leaf.keys[0]
    }

    private fun borrowFromRightLeaf(
        leaf: LeafNode,
        rightSibling: LeafNode,
        parent: InternalNode,
        leafIndex: Int
    ) {
        val borrowedKey = rightSibling.keys.removeAt(0)
        val borrowedValue = rightSibling.values.removeAt(0)

        leaf.keys.add(borrowedKey)
        leaf.values.add(borrowedValue)

        parent.keys[leafIndex] = rightSibling.keys[0]
    }

    private fun mergeLeaves(
        leftLeaf: LeafNode,
        rightLeaf: LeafNode,
        parent: InternalNode,
        rightLeafIndex: Int
    ) {
        leftLeaf.keys.addAll(rightLeaf.keys)
        leftLeaf.values.addAll(rightLeaf.values)

        leftLeaf.next = rightLeaf.next

        parent.keys.removeAt(rightLeafIndex - 1)
        parent.children.removeAt(rightLeafIndex)

        if (parent === root) {
            if (parent.keys.isEmpty()) {
                root = leftLeaf
            }
        } else if (parent.keys.size < (ORDER - 1)) {
            handleMinimumSize(parent)
        }
    }

    private fun handleMinimumSize(node: InternalNode) {
        val parent = findParent(root, node) as? InternalNode ?: return

        val nodeIndex = parent.children.indexOf(node)

        if (nodeIndex > 0) {
            val leftSibling = parent.children[nodeIndex - 1] as InternalNode
            if (leftSibling.keys.size > ORDER - 1) {
                borrowFromLeft(node, leftSibling, parent, nodeIndex)
                return
            }
        }

        if (nodeIndex < parent.children.size - 1) {
            val rightSibling = parent.children[nodeIndex + 1] as InternalNode
            if (rightSibling.keys.size > ORDER - 1) {
                borrowFromRight(node, rightSibling, parent, nodeIndex)
                return
            }
        }

        if (nodeIndex > 0) {
            val leftSibling = parent.children[nodeIndex - 1] as InternalNode
            mergeNodes(leftSibling, node, parent, nodeIndex)
        } else {
            val rightSibling = parent.children[nodeIndex + 1] as InternalNode
            mergeNodes(node, rightSibling, parent, nodeIndex + 1)
        }
    }

    private fun borrowFromLeft(
        node: InternalNode,
        leftSibling: InternalNode,
        parent: InternalNode,
        nodeIndex: Int
    ) {
        node.keys.add(0, parent.keys[nodeIndex - 1])
        parent.keys[nodeIndex - 1] = leftSibling.keys.removeAt(leftSibling.keys.size - 1)
        node.children.add(0, leftSibling.children.removeAt(leftSibling.children.size - 1))
    }

    private fun borrowFromRight(
        node: InternalNode,
        rightSibling: InternalNode,
        parent: InternalNode,
        nodeIndex: Int
    ) {
        node.keys.add(parent.keys[nodeIndex])
        parent.keys[nodeIndex] = rightSibling.keys.removeAt(0)
        node.children.add(rightSibling.children.removeAt(0))
    }

    private fun mergeNodes(
        leftNode: InternalNode,
        rightNode: InternalNode,
        parent: InternalNode,
        rightNodeIndex: Int
    ) {
        leftNode.keys.add(parent.keys[rightNodeIndex - 1])

        leftNode.keys.addAll(rightNode.keys)
        leftNode.children.addAll(rightNode.children)

        parent.keys.removeAt(rightNodeIndex - 1)
        parent.children.removeAt(rightNodeIndex)

        if (parent === root) {
            if (parent.keys.isEmpty()) {
                root = leftNode
            }
        } else if (parent.keys.size < ORDER - 1) {
            handleMinimumSize(parent)
        }
    }

    private fun updateParentKeys(oldKey: Int, leaf: LeafNode) {
        // Если удалённый ключ был первым в листе, нужно обновить родителей
        var current: BPlusNode = leaf
        var parent = findParent(root, current) as? InternalNode

        while (parent != null) {
            val index = parent.keys.indexOf(oldKey)
            if (index >= 0 && leaf.keys.isNotEmpty()) {
                parent.keys[index] = leaf.keys[0]
            }
            current = parent
            parent = findParent(root, current) as? InternalNode
        }
    }
}

private fun isLeaf(node: BPlusNode) = node is LeafNode