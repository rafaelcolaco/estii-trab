package estruturas;

/**
 * Tabela hash com encadeamento separado. Começa com 16 baldes e dobra a
 * capacidade quando a carga passa de 0,75.
 *
 * O espalhamento usa os bits altos do produto de Knuth. Como os valores são
 * pares, usar os bits baixos deixaria metade dos baldes vazios. O intervalo
 * percorre todos os elementos, pois a tabela não mantém ordem.
 */
public class HashTable implements IntSet {
    private static final int INITIAL_CAPACITY = 16;

    private class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
            next = null;
        }
    }

    private Node[] buckets;
    private int shift;   // Deslocamento para usar os bits altos.
    private int size;

    public HashTable() {
        buckets = new Node[INITIAL_CAPACITY];
        shift = 32 - 4;
        size = 0;
    }

    /** Calcula o índice do balde. */
    private int indexFor(int value) {
        return (value * 0x9E3779B9) >>> shift;
    }

    @Override
    public boolean insert(int value) {
        int index = indexFor(value);
        Node current = buckets[index];
        Node last = null;
        while (current != null) {
            if (current.value == value) {
                return false;
            }
            last = current;
            current = current.next;
        }
        // Acrescenta o nó ao fim da cadeia.
        Node node = new Node(value);
        if (last == null) {
            buckets[index] = node;
        } else {
            last.next = node;
        }
        size++;
        // A carga passou de 0,75.
        if (size * 4 > buckets.length * 3) {
            resize();
        }
        return true;
    }

    /** Dobra a capacidade e redistribui os nós. */
    private void resize() {
        Node[] old = buckets;
        buckets = new Node[old.length * 2];
        shift--;
        // Mantém a ordem dos nós em cada cadeia.
        Node[] tails = new Node[buckets.length];
        for (int i = 0; i < old.length; i++) {
            Node current = old[i];
            while (current != null) {
                Node next = current.next;
                current.next = null;
                int index = indexFor(current.value);
                if (tails[index] == null) {
                    buckets[index] = current;
                } else {
                    tails[index].next = current;
                }
                tails[index] = current;
                current = next;
            }
        }
    }

    @Override
    public boolean contains(int value) {
        Node current = buckets[indexFor(value)];
        while (current != null) {
            if (current.value == value) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean remove(int value) {
        int index = indexFor(value);
        Node current = buckets[index];
        Node previous = null;
        while (current != null) {
            if (current.value == value) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    /** Percorre todos os baldes, pois a tabela não tem ordem. */
    @Override
    public int rangeCount(int min, int max) {
        int count = 0;
        for (int i = 0; i < buckets.length; i++) {
            Node current = buckets[i];
            while (current != null) {
                if (current.value >= min && current.value <= max) {
                    count++;
                }
                current = current.next;
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
        return "hash";
    }

    @Override
    public IntSet copy() {
        HashTable other = new HashTable();
        other.buckets = new Node[buckets.length];
        other.shift = shift;
        other.size = size;
        // Mantém a capacidade e a ordem das cadeias.
        for (int i = 0; i < buckets.length; i++) {
            Node last = null;
            Node current = buckets[i];
            while (current != null) {
                Node node = other.new Node(current.value);
                if (last == null) {
                    other.buckets[i] = node;
                } else {
                    last.next = node;
                }
                last = node;
                current = current.next;
            }
        }
        return other;
    }
}
