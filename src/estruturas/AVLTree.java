package estruturas;

/** Árvore AVL de inteiros, com alturas armazenadas em cada nó. */
public class AVLTree implements IntSet {
    private class Node {
        int value;
        Node left, right;
        int heightLeft, heightRight;

        Node(int value) {
            this.value = value;
            left = right = null;
            heightLeft = heightRight = 0;
        }
    }

    private Node root;
    private int size;
    private boolean changed; // Indica se a árvore foi alterada.

    public AVLTree() {
        root = null;
        size = 0;
    }

    @Override
    public boolean insert(int value) {
        changed = false;
        root = insert(root, value);
        if (changed) {
            size++;
        }
        return changed;
    }

    private Node insert(Node current, int value) {
        if (current == null) {
            changed = true;
            return new Node(value);
        }

        if (value < current.value) {
            current.left = insert(current.left, value);
            current.heightLeft = Math.max(current.left.heightLeft, current.left.heightRight) + 1;
        } else if (value > current.value) {
            current.right = insert(current.right, value);
            current.heightRight = Math.max(current.right.heightLeft, current.right.heightRight) + 1;
        }

        return balance(current);
    }

    private Node balance(Node current) {
        int balanceFactor = current.heightRight - current.heightLeft;

        // A subárvore direita ficou mais alta.
        if (balanceFactor >= 2) {
            if (current.right.heightRight >= current.right.heightLeft) {
                // Rotação simples à esquerda.
                return rotateLeft(current);
            } else {
                // Rotação dupla direita-esquerda.
                current.right = rotateRight(current.right);
                return rotateLeft(current);
            }
        // A subárvore esquerda ficou mais alta.
        } else if (balanceFactor <= -2) {
            if (current.left.heightLeft >= current.left.heightRight) {
                // Rotação simples à direita.
                return rotateRight(current);
            } else {
                // Rotação dupla esquerda-direita.
                current.left = rotateLeft(current.left);
                return rotateRight(current);
            }
        }

        return current;
    }

    private Node rotateLeft(Node node) {
        Node newRoot = node.right;
        node.right = newRoot.left;
        newRoot.left = node;
        updateHeights(node);
        updateHeights(newRoot);
        return newRoot;
    }

    private Node rotateRight(Node node) {
        Node newRoot = node.left;
        node.left = newRoot.right;
        newRoot.right = node;
        updateHeights(node);
        updateHeights(newRoot);
        return newRoot;
    }

    private void updateHeights(Node node) {
        if (node != null) {
            node.heightLeft = (node.left == null) ? 0
                    : Math.max(node.left.heightLeft, node.left.heightRight) + 1;
            node.heightRight = (node.right == null) ? 0
                    : Math.max(node.right.heightLeft, node.right.heightRight) + 1;
        }
    }

    @Override
    public boolean contains(int value) {
        Node current = root;
        while (current != null) {
            if (value == current.value) {
                return true;
            }
            if (value < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }

    @Override
    public boolean remove(int value) {
        changed = false;
        root = remove(root, value);
        if (changed) {
            size--;
        }
        return changed;
    }

    private Node remove(Node node, int value) {
        if (node == null) {
            return null;
        }

        if (value < node.value) {
            node.left = remove(node.left, value);
        } else if (value > node.value) {
            node.right = remove(node.right, value);
        } else {
            changed = true;
            // Nó sem filhos.
            if (node.left == null && node.right == null) {
                return null;
            }
            // Apenas filho direito.
            if (node.left == null) {
                return node.right;
            }
            // Apenas filho esquerdo.
            if (node.right == null) {
                return node.left;
            }
            // Substitui pelo menor valor da subárvore direita.
            Node successor = findMin(node.right);
            node.value = successor.value;
            node.right = remove(node.right, successor.value);
        }

        updateHeights(node);
        return balance(node);
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /** Conta o intervalo e visita apenas os ramos que podem conter seus valores. */
    @Override
    public int rangeCount(int min, int max) {
        return rangeCount(root, min, max);
    }

    private int rangeCount(Node node, int min, int max) {
        if (node == null) {
            return 0;
        }
        if (node.value < min) {
            return rangeCount(node.right, min, max);
        }
        if (node.value > max) {
            return rangeCount(node.left, min, max);
        }
        return 1 + rangeCount(node.left, min, max) + rangeCount(node.right, min, max);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String name() {
        return "avl";
    }

    @Override
    public IntSet copy() {
        AVLTree other = new AVLTree();
        other.root = copyNode(root, other);
        other.size = size;
        return other;
    }

    /** Copia a árvore mantendo seu formato e suas alturas. */
    private Node copyNode(Node node, AVLTree owner) {
        if (node == null) {
            return null;
        }
        Node copy = owner.new Node(node.value);
        copy.heightLeft = node.heightLeft;
        copy.heightRight = node.heightRight;
        copy.left = copyNode(node.left, owner);
        copy.right = copyNode(node.right, owner);
        return copy;
    }
}
