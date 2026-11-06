package benchmark;

import java.util.Random;

// Sorteia todos os dados antes das medições.
final class Dataset {
    final int n;
    final int[] order, present, absent, insert, remove, ranges;

    Dataset(int n, String direction) {
        if (n < 100) throw new IllegalArgumentException("n deve ser pelo menos 100");
        this.n = n;
        Random random = new Random(42);
        int[] shuffled = values(n, false);
        shuffle(shuffled, n, random);
        order = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = direction.equals("aleatoria") ? shuffled[i]
                    : direction.equals("crescente") ? 2 * (i + 1) : 2 * (n - i);
        }
        present = new int[2000];
        absent = new int[2000];
        for (int i = 0; i < present.length; i++) present[i] = 2 * (random.nextInt(n) + 1);
        for (int i = 0; i < absent.length; i++) absent[i] = 2 * random.nextInt(n) + 1;
        int[] odds = values(n, true);
        int[] evens = values(n, false);
        shuffle(odds, n / 10, random);
        shuffle(evens, n / 10, random);
        insert = new int[n / 10];
        remove = new int[n / 10];
        System.arraycopy(odds, 0, insert, 0, insert.length);
        System.arraycopy(evens, 0, remove, 0, remove.length);
        ranges = new int[200];
        for (int i = 0; i < ranges.length; i++) ranges[i] = 1 + random.nextInt(2 * n - 100);
    }

    private static int[] values(int n, boolean odd) {
        int[] values = new int[n];
        for (int i = 0; i < n; i++) values[i] = 2 * (i + 1) - (odd ? 1 : 0);
        return values;
    }

    private static void shuffle(int[] values, int count, Random random) {
        for (int i = 0; i < count; i++) {
            int j = i + random.nextInt(values.length - i);
            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }
    }

    long rangeReference() {
        long sum = 0;
        for (int min : ranges) sum += Math.min(min + 100, 2 * n) / 2 - (min - 1) / 2;
        return sum;
    }
}
