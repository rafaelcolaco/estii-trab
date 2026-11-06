package benchmark;

import estruturas.IntSet;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

public class Benchmark {
    private static final String[] ORDERS = {"crescente", "decrescente", "aleatoria"};
    private static final String[] TESTS = {
        "construcao", "busca_presente", "busca_ausente", "insercao", "remocao", "intervalo"
    };
    private static boolean failed;
    private static volatile long warmUpResult;

    public static void main(String[] args) throws Exception {
        String sizes = "5000,10000,100000";
        Path output = Path.of("resultados");
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--n") && i + 1 < args.length) sizes = args[++i];
            else if (args[i].equals("--out") && i + 1 < args.length) output = Path.of(args[++i]);
            else throw new IllegalArgumentException("argumento inválido: " + args[i]);
        }
        Files.createDirectories(output);
        Locale.setDefault(Locale.ROOT);
        warmUp();
        try (PrintWriter raw = new PrintWriter(Files.newBufferedWriter(output.resolve("resultados_brutos.csv")));
             PrintWriter summary = new PrintWriter(Files.newBufferedWriter(output.resolve("resumo.csv")));
             PrintWriter check = new PrintWriter(Files.newBufferedWriter(output.resolve("conferencia.txt")))) {
            raw.println("tamanho;ordem;estrutura;teste;repeticao;operacoes;tempo_ns");
            summary.println("tamanho;ordem;estrutura;teste;mediana_ms;mediana_ns_por_op");
            for (String size : sizes.split(",")) {
                int n = Integer.parseInt(size);
                for (String order : ORDERS) {
                    Dataset data = new Dataset(n, order);
                    for (String name : StructureFactory.NAMES) {
                        runBlock(data, order, name, raw, summary, check);
                    }
                }
            }
        }
        System.out.println(failed ? "conferência: há FALHA" : "conferência: tudo OK");
        if (failed) System.exit(1);
    }

    private static void warmUp() {
        for (int pass = 0; pass < 5; pass++) {
            for (String order : ORDERS) {
                Dataset data = new Dataset(1000, order);
                for (String name : StructureFactory.NAMES) {
                    IntSet base = build(name, data);
                    for (int test = 0; test < TESTS.length; test++) {
                        warmUpResult += execute(name, data, base, test, false)[1];
                    }
                }
            }
        }
    }

    private static IntSet build(String name, Dataset data) {
        IntSet set = StructureFactory.create(name, data.n);
        for (int value : data.order) set.insert(value);
        return set;
    }

    private static void runBlock(Dataset data, String order, String name, PrintWriter raw,
                                 PrintWriter summary, PrintWriter check) {
        IntSet base = build(name, data);
        for (int test = 0; test < TESTS.length; test++) {
            long[] times = new long[5];
            int operations = operations(data, test);
            String key = data.n + ";" + order + ";" + name + ";" + TESTS[test];
            for (int repetition = 0; repetition <= 5; repetition++) {
                long[] result = execute(name, data, base, test, true);
                long expected = expected(data, test);
                boolean ok = result[1] == expected && result[2] == expectedSize(data, test);
                check.printf("%s;repeticao=%d;resultado=%d;esperado=%d;size=%d;esperado_size=%d;%s%n",
                        key, repetition, result[1], expected, result[2], expectedSize(data, test), ok ? "OK" : "FALHA");
                if (!ok) failed = true;
                if (repetition > 0) {
                    times[repetition - 1] = result[0];
                    raw.printf("%s;%d;%d;%d%n", key, repetition, operations, result[0]);
                }
            }
            Arrays.sort(times);
            summary.printf("%s;%.6f;%.6f%n", key, times[2] / 1_000_000.0, (double) times[2] / operations);
        }
    }

    // A cópia e a conferência ficam fora do intervalo medido.
    private static long[] execute(String name, Dataset data, IntSet base, int test, boolean measure) {
        IntSet set = test == 0 ? null : base.copy();
        long count = 0;
        long start = measure ? System.nanoTime() : 0;
        if (test == 0) {
            set = StructureFactory.create(name, data.n);
            for (int value : data.order) if (set.insert(value)) count++;
        } else if (test == 1) {
            for (int value : data.present) if (set.contains(value)) count++;
        } else if (test == 2) {
            for (int value : data.absent) if (set.contains(value)) count++;
        } else if (test == 3) {
            for (int value : data.insert) if (set.insert(value)) count++;
        } else if (test == 4) {
            for (int value : data.remove) if (set.remove(value)) count++;
        } else {
            for (int min : data.ranges) count += set.rangeCount(min, min + 100);
        }
        long elapsed = measure ? System.nanoTime() - start : 0;
        return new long[]{elapsed, count, set.size()};
    }

    private static int operations(Dataset data, int test) {
        return switch (test) {
            case 0 -> data.n;
            case 1, 2 -> 2000;
            case 3, 4 -> data.n / 10;
            default -> 200;
        };
    }

    private static long expected(Dataset data, int test) {
        return switch (test) {
            case 0 -> data.n;
            case 1 -> 2000;
            case 2 -> 0;
            case 3, 4 -> data.n / 10;
            default -> data.rangeReference();
        };
    }

    private static int expectedSize(Dataset data, int test) {
        if (test == 3) return data.n + data.n / 10;
        if (test == 4) return data.n - data.n / 10;
        return data.n;
    }
}
