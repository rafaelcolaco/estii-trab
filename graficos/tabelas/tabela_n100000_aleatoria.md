### n = 100.000, ordem aleatória

Mediana do tempo total do lote, em ms (3 casas decimais; o CSV de mesmo nome tem o valor exato).

| Teste | Array linear | Array ordenado | Lista duplamente encadeada | Tabela hash | ABB (BST) | AVL | Rubro-negra |
|---|---|---|---|---|---|---|---|
| Construção | 1140,799 | 269,996 | 10019,499 | 5,059 | 18,827 | 25,073 | 21,217 |
| Busca presente | 22,834 | 0,228 | 238,684 | 0,083 | 0,555 | 0,487 | 0,533 |
| Busca ausente | 46,275 | 0,245 | 502,856 | 0,069 | 0,619 | 0,569 | 0,642 |
| Inserção | 241,434 | 60,375 | 2426,696 | 0,419 | 2,951 | 3,471 | 2,991 |
| Remoção | 111,077 | 53,249 | 1000,366 | 0,488 | 2,514 | 3,433 | 2,912 |
| Consulta por intervalo | 56,782 | 0,036 | 87,232 | 72,882 | 0,252 | 0,196 | 0,233 |
