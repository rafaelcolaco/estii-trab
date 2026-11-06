package estruturas;

/** Interface comum dos conjuntos de inteiros. */
public interface IntSet {
    /** Insere o valor, se ainda não estiver no conjunto. */
    boolean insert(int value);

    /** Verifica se o valor está no conjunto. */
    boolean contains(int value);

    /** Remove o valor, se ele estiver no conjunto. */
    boolean remove(int value);

    /** Conta os valores entre min e max, incluindo os limites. */
    int rangeCount(int min, int max);

    /** Retorna a quantidade de elementos. */
    int size();

    /** Retorna o nome da estrutura. */
    String name();

    /** Cria uma cópia independente do conjunto. */
    IntSet copy();
}
