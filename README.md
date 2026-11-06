# Comparação de estruturas de dados

O programa compara sete conjuntos de inteiros: array linear, array ordenado, lista duplamente encadeada, tabela hash, árvore binária de busca, árvore AVL e árvore rubro-negra. Todos implementam a interface `IntSet`, com inserção, busca, remoção e contagem por intervalo.

Os arrays reservam capacidade para n + n/10 elementos. A tabela hash usa encadeamento separado, começa com 16 baldes e dobra a capacidade quando a carga ultrapassa 0,75.

Os testes medem construção, busca presente, busca ausente, inserção, remoção e consulta por intervalo. São usados n = 5.000, 10.000 e 100.000 números pares, nas ordens crescente, decrescente e aleatória. Os dados são sorteados com semente 42 e reutilizados entre as estruturas. Há cinco passadas gerais de aquecimento, seguidas de um aquecimento e cinco medições por combinação; o estado inicial dos testes é copiado fora do tempo medido.

O projeto compila para Java 21; o Java usado na medição foi OpenJDK 27. Para compilar e testar:

```sh
scripts/build.sh
scripts/testar.sh
```

Para executar as medições, use `scripts/rodar-benchmark.sh`. Para um teste rápido em outra pasta:

```sh
scripts/rodar-benchmark.sh --n 1000,2000 --out teste-rapido
```

Os CSVs em `resultados/` contêm 1.890 medições e 378 resumos. A conferência registra 2.268 itens OK e nenhuma falha. Para produzir os gráficos e tabelas, instale `matplotlib` para Python 3 e execute:

```sh
python3 graficos/gerar.py
```
