package estruturas;

/** Árvore rubro-negra com sentinela NIL e ponteiro para o pai em cada nó. */
public class RedBlackTree implements IntSet {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        int value;
        boolean color;
        Node left, right, parent;

        Node(int value, boolean color) {
            this.value = value;
            this.color = color;
        }
    }

    private final Node nil;
    private Node root;
    private int size;

    public RedBlackTree() {
        nil = new Node(0, BLACK);
        nil.left = nil.right = nil.parent = nil;
        root = nil;
        size = 0;
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != nil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != nil) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    @Override
    public boolean insert(int value) {
        Node parent = nil;
        Node current = root;
        while (current != nil) {
            parent = current;
            if (value == current.value) {
                return false;
            }
            if (value < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        Node z = new Node(value, RED);
        z.left = z.right = nil;
        z.parent = parent;
        if (parent == nil) {
            root = z;
        } else if (value < parent.value) {
            parent.left = z;
        } else {
            parent.right = z;
        }
        insertFixup(z);
        size++;
        return true;
    }

    private void insertFixup(Node z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node uncle = z.parent.parent.right;
                if (uncle.color == RED) {
                    // Tio vermelho: recolore os nós.
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        // Rotaciona para preparar o próximo caso.
                        z = z.parent;
                        rotateLeft(z);
                    }
                    // Recolore e rotaciona.
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rotateRight(z.parent.parent);
                }
            } else {
                Node uncle = z.parent.parent.left;
                if (uncle.color == RED) {
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rotateRight(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rotateLeft(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private Node find(int value) {
        Node current = root;
        while (current != nil) {
            if (value == current.value) {
                return current;
            }
            if (value < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return nil;
    }

    @Override
    public boolean contains(int value) {
        return find(value) != nil;
    }

    /** Coloca v no lugar de u. */
    private void transplant(Node u, Node v) {
        if (u.parent == nil) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent; // vale mesmo se v for NIL: o fixup usa esse pai
    }

    private Node minimum(Node node) {
        while (node.left != nil) {
            node = node.left;
        }
        return node;
    }

    @Override
    public boolean remove(int value) {
        Node z = find(value);
        if (z == nil) {
            return false;
        }
        Node y = z;
        boolean yOriginalColor = y.color;
        Node x;
        if (z.left == nil) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == nil) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (yOriginalColor == BLACK) {
            removeFixup(x);
        }
        size--;
        return true;
    }

    private void removeFixup(Node x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.color == RED) {
                    // Irmão vermelho: recolore e rotaciona.
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    // Irmão e filhos pretos: sobe o ajuste.
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.right.color == BLACK) {
                        // Rotaciona para preparar o próximo caso.
                        w.left.color = BLACK;
                        w.color = RED;
                        rotateRight(w);
                        w = x.parent.right;
                    }
                    // Recolore e rotaciona.
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        rotateLeft(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }

    /** Conta o intervalo e visita apenas os ramos que podem conter seus valores. */
    @Override
    public int rangeCount(int min, int max) {
        return rangeCount(root, min, max);
    }

    private int rangeCount(Node node, int min, int max) {
        if (node == nil) {
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
        return "rubro_negra";
    }

    @Override
    public IntSet copy() {
        RedBlackTree other = new RedBlackTree();
        other.root = copyNode(root, other.nil, other);
        other.size = size;
        return other;
    }

    /** Copia a árvore mantendo seu formato e suas cores. */
    private Node copyNode(Node node, Node parent, RedBlackTree owner) {
        if (node == nil) {
            return owner.nil;
        }
        Node copy = owner.new Node(node.value, node.color);
        copy.parent = parent;
        copy.left = copyNode(node.left, copy, owner);
        copy.right = copyNode(node.right, copy, owner);
        return copy;
    }
}
