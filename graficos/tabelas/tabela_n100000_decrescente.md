### n = 100.000, ordem decrescente

Mediana do tempo total do lote, em ms (3 casas decimais; o CSV de mesmo nome tem o valor exato).

| Teste | Array linear | Array ordenado | Lista duplamente encadeada | Tabela hash | ABB (BST) | AVL | Rubro-negra |
|---|---|---|---|---|---|---|---|
| Construção | 1135,546 | 555,449 | 8959,516 | 3,606 | 9388,557 | 10,836 | 13,641 |
| Busca presente | 22,584 | 0,227 | 200,263 | 0,081 | 206,998 | 0,521 | 0,555 |
| Busca ausente | 45,679 | 0,233 | 412,005 | 0,067 | 211,921 | 0,661 | 0,642 |
| Inserção | 241,988 | 54,116 | 1972,762 | 0,425 | 950,172 | 3,936 | 3,215 |
| Remoção | 108,879 | 47,700 | 888,371 | 0,486 | 1016,133 | 3,901 | 3,849 |
| Consulta por intervalo | 10,040 | 0,039 | 44,395 | 72,790 | 30,082 | 0,156 | 0,169 |
