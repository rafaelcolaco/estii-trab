package estruturas;

/**
 * Árvore binária de busca sem balanceamento. As operações são iterativas para
 * evitar estouro de pilha quando a árvore fica muito alta.
 */
public class BinarySearchTree implements IntSet {
    private class Node {
        int value;
        Node left, right;

        Node(int value) {
            this.value = value;
            left = right = null;
        }
    }

    private Node root;
    private int size;

    // Pilhas usadas nas travessias.
    private Node[] stack;
    private Node[] stack2;

    public BinarySearchTree() {
        root = null;
        size = 0;
        stack = new Node[64];
        stack2 = new Node[64];
    }

    private void growStacks() {
        Node[] bigger = new Node[stack.length * 2];
        System.arraycopy(stack, 0, bigger, 0, stack.length);
        stack = bigger;
        Node[] bigger2 = new Node[stack2.length * 2];
        System.arraycopy(stack2, 0, bigger2, 0, stack2.length);
        stack2 = bigger2;
    }

    @Override
    public boolean insert(int value) {
        if (root == null) {
            root = new Node(value);
            size++;
            return true;
        }
        Node current = root;
        while (true) {
            if (value == current.value) {
                return false;
            }
            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                    break;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    break;
                }
                current = current.right;
            }
        }
        size++;
        return true;
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
        // Guarda o pai para ligar o filho após a remoção.
        Node parent = null;
        Node current = root;
        while (current != null && current.value != value) {
            parent = current;
            if (value < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (current == null) {
            return false;
        }

        if (current.left != null && current.right != null) {
            // Substitui pelo menor valor da subárvore direita.
            Node successorParent = current;
            Node successor = current.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            current.value = successor.value;
            // O sucessor não tem filho esquerdo.
            parent = successorParent;
            current = successor;
        }

        // Liga o pai ao único filho, se houver.
        Node child = (current.left != null) ? current.left : current.right;
        if (parent == null) {
            root = child;
        } else if (parent.left == current) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        size--;
        return true;
    }

    /** Conta o intervalo e visita apenas os ramos que podem conter seus valores. */
    @Override
    public int rangeCount(int min, int max) {
        if (root == null || min > max) {
            return 0;
        }
        int count = 0;
        int top = 0;
        stack[top++] = root;
        while (top > 0) {
            Node node = stack[--top];
            if (node.value < min) {
                // Valores à esquerda são ainda menores.
                if (node.right != null) {
                    if (top == stack.length) growStacks();
                    stack[top++] = node.right;
                }
            } else if (node.value > max) {
                if (node.left != null) {
                    if (top == stack.length) growStacks();
                    stack[top++] = node.left;
                }
            } else {
                count++;
                if (top + 2 > stack.length) growStacks();
                if (node.left != null) stack[top++] = node.left;
                if (node.right != null) stack[top++] = node.right;
            }
        }
        return count;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String name() {
        return "bst";
    }

    /** Copia a árvore mantendo seu formato. */
    @Override
    public IntSet copy() {
        BinarySearchTree other = new BinarySearchTree();
        other.size = size;
        if (root == null) {
            return other;
        }
        other.root = other.new Node(root.value);
        // Associa cada nó original ao seu par na cópia.
        int top = 0;
        stack[top] = root;
        stack2[top] = other.root;
        top++;
        while (top > 0) {
            top--;
            Node source = stack[top];
            Node target = stack2[top];
            if (top + 2 > stack.length) growStacks();
            if (source.left != null) {
                target.left = other.new Node(source.left.value);
                stack[top] = source.left;
                stack2[top] = target.left;
                top++;
            }
            if (source.right != null) {
                target.right = other.new Node(source.right.value);
                stack[top] = source.right;
                stack2[top] = target.right;
                top++;
            }
        }
        // Limpa as pilhas usadas na cópia.
        for (int i = 0; i < stack.length; i++) {
            stack[i] = null;
            stack2[i] = null;
        }
        return other;
    }
}
