package estruturas;

/** Lista duplamente encadeada de inteiros, sem ordem. */
public class DoublyLinkedList implements IntSet {
    private class Node {
        int value;
        Node prev, next;

        Node(int value) {
            this.value = value;
            prev = next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public DoublyLinkedList() {
        head = tail = null;
        size = 0;
    }

    private Node find(int value) {
        Node current = head;
        while (current != null) {
            if (current.value == value) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public boolean insert(int value) {
        if (find(value) != null) {
            return false;
        }
        Node node = new Node(value);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
        return true;
    }

    @Override
    public boolean contains(int value) {
        return find(value) != null;
    }

    @Override
    public boolean remove(int value) {
        Node node = find(value);
        if (node == null) {
            return false;
        }
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        size--;
        return true;
    }

    /** Percorre a lista para contar os valores no intervalo. */
    @Override
    public int rangeCount(int min, int max) {
        int count = 0;
        Node current = head;
        while (current != null) {
            if (current.value >= min && current.value <= max) {
                count++;
            }
            current = current.next;
        }
        return count;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String name() {
        return "lista_dupla";
    }

    @Override
    public IntSet copy() {
        DoublyLinkedList other = new DoublyLinkedList();
        // Mantém a ordem dos nós.
        Node current = head;
        while (current != null) {
            Node node = other.new Node(current.value);
            if (other.tail == null) {
                other.head = other.tail = node;
            } else {
                node.prev = other.tail;
                other.tail.next = node;
                other.tail = node;
            }
            current = current.next;
        }
        other.size = size;
        return other;
    }
}
