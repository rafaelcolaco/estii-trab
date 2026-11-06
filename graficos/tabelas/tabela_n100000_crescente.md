### n = 100.000, ordem crescente

Mediana do tempo total do lote, em ms (3 casas decimais; o CSV de mesmo nome tem o valor exato).

| Teste | Array linear | Array ordenado | Lista duplamente encadeada | Tabela hash | ABB (BST) | AVL | Rubro-negra |
|---|---|---|---|---|---|---|---|
| Construção | 1260,833 | 2,907 | 8918,978 | 5,165 | 8652,033 | 11,757 | 16,160 |
| Busca presente | 22,699 | 0,214 | 213,652 | 0,080 | 220,438 | 0,465 | 0,574 |
| Busca ausente | 47,266 | 0,231 | 436,320 | 0,072 | 219,711 | 0,529 | 0,641 |
| Inserção | 254,567 | 52,541 | 1851,718 | 0,468 | 865,428 | 3,609 | 3,777 |
| Remoção | 111,973 | 47,552 | 905,242 | 0,509 | 1091,416 | 4,065 | 3,769 |
| Consulta por intervalo | 10,116 | 0,037 | 45,010 | 73,403 | 35,146 | 0,151 | 0,154 |
