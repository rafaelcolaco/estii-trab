package estruturas;

/** Array de inteiros sem ordem, com busca sequencial e capacidade fixa. */
public class LinearArray implements IntSet {
    private int[] data;
    private int size;

    public LinearArray(int capacity) {
        data = new int[capacity];
        size = 0;
    }

    /** Procura o valor e retorna sua posição, ou -1 se não encontrar. */
    private int indexOf(int value) {
        for (int i = 0; i < size; i++) {
            if (data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean insert(int value) {
        if (indexOf(value) != -1) {
            return false;
        }
        if (size == data.length) {
            throw new IllegalStateException("Array cheio (capacidade " + data.length + ")");
        }
        data[size] = value;
        size++;
        return true;
    }

    @Override
    public boolean contains(int value) {
        return indexOf(value) != -1;
    }

    @Override
    public boolean remove(int value) {
        int index = indexOf(value);
        if (index == -1) {
            return false;
        }
        // Substitui pelo último elemento.
        data[index] = data[size - 1];
        size--;
        return true;
    }

    /** Conta os valores percorrendo o array. */
    @Override
    public int rangeCount(int min, int max) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] >= min && data[i] <= max) {
                count++;
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
        return "array_linear";
    }

    @Override
    public IntSet copy() {
        LinearArray other = new LinearArray(data.length);
        System.arraycopy(data, 0, other.data, 0, size);
        other.size = size;
        return other;
    }
}
