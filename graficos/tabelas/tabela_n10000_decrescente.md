### n = 10.000, ordem decrescente

Mediana do tempo total do lote, em ms (3 casas decimais; o CSV de mesmo nome tem o valor exato).

| Teste | Array linear | Array ordenado | Lista duplamente encadeada | Tabela hash | ABB (BST) | AVL | Rubro-negra |
|---|---|---|---|---|---|---|---|
| Construção | 27,849 | 5,811 | 109,145 | 0,403 | 182,016 | 1,023 | 1,297 |
| Busca presente | 5,034 | 0,240 | 27,206 | 0,050 | 32,816 | 0,288 | 0,365 |
| Busca ausente | 6,246 | 0,254 | 53,940 | 0,035 | 29,527 | 0,283 | 0,322 |
| Inserção | 3,090 | 0,730 | 25,162 | 0,022 | 17,992 | 0,409 | 0,188 |
| Remoção | 1,553 | 0,655 | 12,206 | 0,033 | 17,393 | 0,423 | 0,195 |
| Consulta por intervalo | 1,172 | 0,040 | 5,599 | 9,792 | 4,575 | 0,156 | 0,186 |
