### n = 10.000, ordem crescente

Mediana do tempo total do lote, em ms (3 casas decimais; o CSV de mesmo nome tem o valor exato).

| Teste | Array linear | Array ordenado | Lista duplamente encadeada | Tabela hash | ABB (BST) | AVL | Rubro-negra |
|---|---|---|---|---|---|---|---|
| Construção | 17,738 | 0,305 | 118,818 | 0,853 | 113,576 | 1,222 | 1,027 |
| Busca presente | 3,093 | 0,194 | 27,412 | 0,070 | 27,195 | 0,284 | 0,254 |
| Busca ausente | 6,586 | 0,234 | 53,395 | 0,071 | 28,522 | 0,280 | 0,449 |
| Inserção | 3,282 | 0,643 | 25,961 | 0,037 | 10,948 | 0,217 | 0,194 |
| Remoção | 1,444 | 0,531 | 13,608 | 0,036 | 13,419 | 0,334 | 0,191 |
| Consulta por intervalo | 1,321 | 0,031 | 5,840 | 7,984 | 5,478 | 0,146 | 0,100 |
