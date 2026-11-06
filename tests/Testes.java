package tests;

import estruturas.AVLTree;
import estruturas.BinarySearchTree;
import estruturas.DoublyLinkedList;
import estruturas.HashTable;
import estruturas.IntSet;
import estruturas.LinearArray;
import estruturas.RedBlackTree;
import estruturas.SortedArray;

public final class Testes {
    private static final int N = 200;

    private record Caso(String nome, IntSet conjunto) { }

    public static void main(String[] args) {
        Caso[] casos = {
            new Caso("array_linear", new LinearArray(N + N / 10)),
            new Caso("array_ordenado", new SortedArray(N + N / 10)),
            new Caso("lista_dupla", new DoublyLinkedList()),
            new Caso("hash", new HashTable()),
            new Caso("bst", new BinarySearchTree()),
            new Caso("avl", new AVLTree()),
            new Caso("rubro_negra", new RedBlackTree())
        };
        boolean tudoPassou = true;
        for (Caso caso : casos) {
            try {
                testar(caso.conjunto());
                System.out.println("PASS " + caso.nome());
            } catch (Throwable erro) {
                tudoPassou = false;
                System.out.println("FAIL " + caso.nome() + ": " + erro);
            }
        }
        if (!tudoPassou) System.exit(1);
        System.out.println("TUDO PASS");
    }

    private static void testar(IntSet conjunto) {
        int[] valores = new int[N];
        for (int i = 0; i < N; i++) {
            valores[i] = 2 * (i + 1);
            exigir(conjunto.insert(valores[i]), "inserção inicial");
        }
        exigir(conjunto.size() == N, "size após inserir n valores");
        for (int valor : valores) {
            exigir(!conjunto.insert(valor), "duplicata aceita");
            exigir(conjunto.contains(valor), "busca de presente falhou");
        }
        exigir(conjunto.size() == N, "size mudou com duplicatas");
        for (int valor = 1; valor <= 2 * N + 1; valor += 2) {
            exigir(!conjunto.contains(valor), "busca de ausente falhou");
        }
        for (int i = 0; i < N; i += 2) {
            exigir(conjunto.remove(valores[i]), "remoção de presente falhou");
            exigir(!conjunto.contains(valores[i]), "valor removido continua presente");
        }
        for (int i = 0; i < N; i += 2) {
            exigir(!conjunto.remove(valores[i]), "remoção de ausente retornou true");
        }
        exigir(conjunto.size() == N / 2, "size após remoções");

        int[][] intervalos = {{1, 2 * N}, {2, 2}, {3, 399}, {400, 1000}, {10, 9}};
        for (int[] intervalo : intervalos) {
            int esperado = 0;
            if (intervalo[0] <= intervalo[1]) {
                for (int valor : valores) {
                    if (valor % 4 == 0 && valor >= intervalo[0] && valor <= intervalo[1]) esperado++;
                }
            }
            exigir(conjunto.rangeCount(intervalo[0], intervalo[1]) == esperado,
                    "rangeCount [" + intervalo[0] + ", " + intervalo[1] + "]");
        }

        IntSet copia = conjunto.copy();
        exigir(copia.size() == conjunto.size(), "size da cópia");
        exigir(copia.rangeCount(1, 2 * N) == conjunto.rangeCount(1, 2 * N), "cópia diferente");
        exigir(copia.insert(1001), "inserção na cópia");
        exigir(!conjunto.contains(1001), "cópia alterou o original");

        if (conjunto instanceof BinarySearchTree) testarBstGrande();
    }

    private static void testarBstGrande() {
        IntSet bst = new BinarySearchTree();
        for (int i = 1; i <= 100_000; i++) exigir(bst.insert(i), "BST crescente");
        exigir(bst.size() == 100_000, "size BST grande");
        exigir(bst.contains(1) && bst.contains(100_000), "busca BST grande");
        exigir(bst.rangeCount(99_990, 100_000) == 11, "intervalo BST grande");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
