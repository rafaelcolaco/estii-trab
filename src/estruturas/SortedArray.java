package estruturas;

/** Array de inteiros em ordem crescente, com busca binária e capacidade fixa. */
public class SortedArray implements IntSet {
    private int[] data;
    private int size;

    public SortedArray(int capacity) {
        data = new int[capacity];
        size = 0;
    }

    /** Busca o valor ou indica onde ele deve ser inserido. */
    private int binarySearch(int value) {
        int low = 0;
        int high = size - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (data[mid] == value) {
                return mid;
            }
            if (data[mid] < value) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -(low + 1);
    }

    /** Retorna a posição do primeiro valor maior ou igual a value. */
    private int lowerBound(int value) {
        int low = 0;
        int high = size;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (data[mid] < value) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    @Override
    public boolean insert(int value) {
        int index = binarySearch(value);
        if (index >= 0) {
            return false;
        }
        if (size == data.length) {
            throw new IllegalStateException("Array cheio (capacidade " + data.length + ")");
        }
        int position = -index - 1;
        // Abre espaço na posição correta.
        System.arraycopy(data, position, data, position + 1, size - position);
        data[position] = value;
        size++;
        return true;
    }

    @Override
    public boolean contains(int value) {
        return binarySearch(value) >= 0;
    }

    @Override
    public boolean remove(int value) {
        int index = binarySearch(value);
        if (index < 0) {
            return false;
        }
        // Fecha o espaço e mantém a ordem.
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        size--;
        return true;
    }

    /** Usa busca binária para localizar os limites do intervalo. */
    @Override
    public int rangeCount(int min, int max) {
        if (min > max) {
            return 0;
        }
        // Localiza o início e o fim do intervalo.
        int first = lowerBound(min);
        int end = (max == Integer.MAX_VALUE) ? size : lowerBound(max + 1);
        return end - first;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String name() {
        return "array_ordenado";
    }

    @Override
    public IntSet copy() {
        SortedArray other = new SortedArray(data.length);
        System.arraycopy(data, 0, other.data, 0, size);
        other.size = size;
        return other;
    }
}
