package benchmark;

import estruturas.*;

final class StructureFactory {
    static final String[] NAMES = {
        "array_linear", "array_ordenado", "lista_dupla", "hash", "bst", "avl", "rubro_negra"
    };

    static IntSet create(String name, int n) {
        return switch (name) {
            case "array_linear" -> new LinearArray(n + n / 10);
            case "array_ordenado" -> new SortedArray(n + n / 10);
            case "lista_dupla" -> new DoublyLinkedList();
            case "hash" -> new HashTable();
            case "bst" -> new BinarySearchTree();
            case "avl" -> new AVLTree();
            case "rubro_negra" -> new RedBlackTree();
            default -> throw new IllegalArgumentException("estrutura desconhecida: " + name);
        };
    }
}
