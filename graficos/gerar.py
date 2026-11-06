#!/usr/bin/env python3
"""Gera figuras e tabelas a partir de resultados/resumo.csv.

Uso: python3 graficos/gerar.py [--entrada resumo.csv] [--saida pasta]
Requer Python 3 e matplotlib. A saída contém PNGs a 300 dpi e tabelas CSV/Markdown.
"""

import argparse
import csv
import re
import sys
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt  # noqa: E402
from matplotlib.ticker import FuncFormatter  # noqa: E402

RAIZ = Path(__file__).resolve().parent.parent
ENTRADA_PADRAO = RAIZ / "resultados" / "resumo.csv"
SAIDA_PADRAO = RAIZ / "graficos"

CABECALHO = ["tamanho", "ordem", "estrutura", "teste", "mediana_ms", "mediana_ns_por_op"]
ESTRUTURAS = ["array_linear", "array_ordenado", "lista_dupla", "hash", "bst", "avl", "rubro_negra"]
TESTES = ["construcao", "busca_presente", "busca_ausente", "insercao", "remocao", "intervalo"]
ORDENS = ["crescente", "decrescente", "aleatoria"]
ARVORES = ["bst", "avl", "rubro_negra"]

NOME_ESTRUTURA = {
    "array_linear": "Array linear",
    "array_ordenado": "Array ordenado",
    "lista_dupla": "Lista duplamente encadeada",
    "hash": "Tabela hash",
    "bst": "ABB (BST)",
    "avl": "AVL",
    "rubro_negra": "Rubro-negra",
}
NOME_TESTE = {
    "construcao": "Construção",
    "busca_presente": "Busca presente",
    "busca_ausente": "Busca ausente",
    "insercao": "Inserção",
    "remocao": "Remoção",
    "intervalo": "Consulta por intervalo",
}
NOME_ORDEM = {"crescente": "crescente", "decrescente": "decrescente", "aleatoria": "aleatória"}

# Mesma cor e marcador para a mesma estrutura em todas as figuras.
# Cores da paleta Okabe-Ito (amigável a daltonismo); o marcador garante leitura em preto e branco.
COR = {
    "array_linear": "#E69F00",
    "array_ordenado": "#56B4E9",
    "lista_dupla": "#009E73",
    "hash": "#CC79A7",
    "bst": "#0072B2",
    "avl": "#D55E00",
    "rubro_negra": "#7B3294",
}
MARCADOR = {
    "array_linear": "o",
    "array_ordenado": "s",
    "lista_dupla": "^",
    "hash": "D",
    "bst": "v",
    "avl": "P",
    "rubro_negra": "X",
}
# Séries que se sobrepõem (ex.: BST e AVL) continuam visíveis: traço diferente e marcador menor
# para as estruturas desenhadas por último (ficam por cima).
LINHA = {
    "array_linear": "-",
    "array_ordenado": "--",
    "lista_dupla": "-.",
    "hash": ":",
    "bst": "-",
    "avl": "--",
    "rubro_negra": "-.",
}
TAM_MARCADOR = {
    "array_linear": 10,
    "array_ordenado": 9,
    "lista_dupla": 8,
    "hash": 7,
    "bst": 7,
    "avl": 5.5,
    "rubro_negra": 4.5,
}

DPI = 300
# Prefixos dos arquivos produzidos pelo script.
PREFIXOS_FIGURA = ("teste_", "ordem_arvores_", "arrays_")
NUMERO = re.compile(r"^\d+(\.\d+)?$")

plt.rcParams.update({
    "font.size": 10,
    "axes.titlesize": 11,
    "axes.spines.top": False,
    "axes.spines.right": False,
    "legend.fontsize": 9,
})


class ErroFormato(Exception):
    """Entrada fora do formato esperado; o script para sem gravar nada."""


def operacoes(teste, n):
    """Operações por lote (enunciado §4). Serve para conferir mediana_ns_por_op = mediana_ms*1e6/operações."""
    return {
        "construcao": n,
        "busca_presente": 2000,
        "busca_ausente": 2000,
        "insercao": n // 10,
        "remocao": n // 10,
        "intervalo": 200,
    }[teste]


def ler_resumo(caminho):
    """Lê e valida o resumo. Devolve ({(n, ordem, estrutura, teste): valores}, [n em ordem crescente])."""
    with open(caminho, encoding="utf-8", newline="") as f:
        linhas = list(csv.reader(f, delimiter=";"))
    if not linhas or linhas[0] != CABECALHO:
        obtido = ";".join(linhas[0]) if linhas else "(arquivo vazio)"
        raise ErroFormato(
            "cabeçalho inválido.\n"
            f"  esperado: {';'.join(CABECALHO)}\n"
            f"  obtido:   {obtido}"
        )

    dados = {}
    for num, linha in enumerate(linhas[1:], start=2):
        if len(linha) != 6:
            raise ErroFormato(f"linha {num}: {len(linha)} colunas, esperado 6")
        tam, ordem, est, teste, ms_txt, ns_txt = linha
        if not tam.isdigit() or int(tam) <= 0 or int(tam) % 10:
            raise ErroFormato(f"linha {num}: tamanho '{tam}' inválido (inteiro positivo múltiplo de 10)")
        if ordem not in ORDENS:
            raise ErroFormato(f"linha {num}: ordem '{ordem}' desconhecida")
        if est not in ESTRUTURAS:
            raise ErroFormato(f"linha {num}: estrutura '{est}' desconhecida")
        if teste not in TESTES:
            raise ErroFormato(f"linha {num}: teste '{teste}' desconhecido")
        for campo, txt in (("mediana_ms", ms_txt), ("mediana_ns_por_op", ns_txt)):
            if not NUMERO.match(txt):
                raise ErroFormato(
                    f"linha {num}: {campo} = '{txt}' fora do formato (ponto decimal, sem separador de milhar)"
                )
        ms, ns = float(ms_txt), float(ns_txt)
        if ms <= 0 or ns <= 0:
            raise ErroFormato(f"linha {num}: tempo zero ou negativo ({ms_txt}; {ns_txt})")
        chave = (int(tam), ordem, est, teste)
        if chave in dados:
            raise ErroFormato(f"linha {num}: combinação repetida {chave}")
        dados[chave] = {"ms_txt": ms_txt, "ns_txt": ns_txt, "ms": ms, "ns": ns}

    ns_lista = sorted({k[0] for k in dados})
    if not ns_lista:
        raise ErroFormato("nenhuma linha de dados")
    esperadas = len(ns_lista) * len(ORDENS) * len(ESTRUTURAS) * len(TESTES)
    if len(dados) != esperadas:
        raise ErroFormato(
            f"grade incompleta: {len(dados)} linhas, esperado {esperadas} "
            f"({len(ns_lista)} tamanhos x 3 ordens x 7 estruturas x 6 testes)"
        )

    for (n, ordem, est, teste), v in dados.items():
        esperado = v["ms"] * 1e6 / operacoes(teste, n)
        if abs(v["ns"] - esperado) > 1e-3 * v["ns"] + 1e-3:
            raise ErroFormato(
                f"inconsistência em n={n} {ordem} {est} {teste}: mediana_ns_por_op = {v['ns_txt']}, "
                f"mas mediana_ms*1e6/operações = {esperado:.3f}"
            )
    return dados, ns_lista


def br_num(v):
    """Número no padrão pt-BR para rótulos: 5000 -> '5.000'; 0.5 -> '0,50'."""
    s = f"{v:,.2f}".replace(",", "#").replace(".", ",").replace("#", ".")
    return s[:-3] if s.endswith(",00") else s


def escala_y(eixos, valores):
    """Aplica a mesma escala y a todos os eixos. Log quando o maior valor passa de 10 vezes o menor."""
    minimo, maximo = min(valores), max(valores)
    usar_log = maximo / minimo > 10
    for ax in eixos:
        if usar_log:
            ax.set_yscale("log")
            ax.set_ylim(minimo / 1.5, maximo * 1.5)
            ax.minorticks_off()
        else:
            ax.set_ylim(0, maximo * 1.1)
        ax.yaxis.set_major_formatter(FuncFormatter(lambda v, _: br_num(v)))
    if usar_log:
        return "eixo y em escala logarítmica (valores variam mais de 10 vezes)"
    return "eixo y em escala linear"


def desenhar(ax, xs, ys, est):
    """Uma série com cor, traço e marcador fixos por estrutura (iguais em todas as figuras)."""
    ax.plot(xs, ys, color=COR[est], marker=MARCADOR[est], linestyle=LINHA[est], linewidth=1.8,
            markersize=TAM_MARCADOR[est], label=NOME_ESTRUTURA[est])


def eixo_n(ax, ns):
    """Eixo x com os tamanhos n em escala logarítmica, rótulos pt-BR nos valores de n."""
    ax.set_xscale("log")
    ax.set_xlim(ns[0] / 1.3, ns[-1] * 1.3)
    ax.set_xticks(ns)
    ax.set_xticklabels([br_num(n) for n in ns])
    ax.minorticks_off()
    ax.set_xlabel("Quantidade de elementos n (escala logarítmica)")
    ax.grid(True, alpha=0.3)


def legenda_e_salvar(fig, eixo_legenda, caminho, nota):
    """Legenda única embaixo, nota de fonte e escala, e gravação em PNG."""
    handles, labels = eixo_legenda.get_legend_handles_labels()
    fig.legend(handles, labels, loc="lower center", ncol=4, frameon=False, bbox_to_anchor=(0.5, 0.075))
    fig.text(0.5, 0.012, nota, ha="center", fontsize=8.5)
    fig.tight_layout(rect=(0, 0.16, 1, 0.96))
    fig.savefig(caminho, dpi=DPI)
    plt.close(fig)
    return caminho


def figura_por_teste(dados, ns, teste, saida, fonte):
    """Um PNG por teste: três painéis (uma por ordem), n no eixo x, uma série por estrutura."""
    if teste in ("construcao", "busca_ausente"):
        fig, eixos = plt.subplots(3, 1, figsize=(6, 7.8), sharex=True, sharey=True)
        valores = [dados[(n, o, e, teste)]["ns"] for n in ns for o in ORDENS for e in ESTRUTURAS]
        nota_y = escala_y(eixos, valores)
        for ax, ordem in zip(eixos, ORDENS):
            for est in ESTRUTURAS:
                ys = [dados[(n, ordem, est, teste)]["ns"] for n in ns]
                desenhar(ax, ns, ys, est)
            ax.set_xscale("log")
            ax.set_xlim(ns[0] / 1.3, ns[-1] * 1.3)
            ax.set_xticks(ns)
            ax.set_xticklabels([br_num(n) for n in ns])
            ax.minorticks_off()
            ax.grid(True, alpha=0.3)
            ax.tick_params(axis="both", labelsize=9, pad=2)
        for ax in eixos:
            ax.set_ylabel("Tempo (ns/op)", fontsize=9)
        eixos[-1].set_xlabel("Quantidade de elementos n (escala logarítmica)", fontsize=9, labelpad=2)
        titulo = "Construção: tempo por operação" if teste == "construcao" else "Busca ausente: tempo por operação"
        fig.suptitle(titulo, fontsize=11, y=0.98)
        handles, labels = eixos[0].get_legend_handles_labels()
        fig.legend(handles, labels, loc="lower center", ncol=4, frameon=False,
                   bbox_to_anchor=(0.5, 0.105), fontsize=9,
                   columnspacing=1.0, handletextpad=0.4)
        nota = f"Mediana de 5 execuções por lote; fonte: {fonte}.\nEixo x em escala logarítmica; {nota_y}."
        fig.text(0.5, 0.015, nota, ha="center", va="bottom", fontsize=9)
        fig.subplots_adjust(left=0.18, right=0.98, top=0.89, bottom=0.25, hspace=0.7)
        for i, (ax, ordem) in enumerate(zip(eixos, ORDENS)):
            if i == 0:
                y_titulo = ax.get_position().y1 + 0.018
            else:
                y_titulo = (eixos[i - 1].get_position().y0 + ax.get_position().y1) / 2
            fig.text(0.58, y_titulo, f"Ordem de inserção {NOME_ORDEM[ordem]}",
                     ha="center", va="center", fontsize=10)
        caminho = saida / f"teste_{teste}.png"
        fig.savefig(caminho, dpi=DPI)
        plt.close(fig)
        return caminho

    fig, eixos = plt.subplots(1, 3, figsize=(15, 5.8), sharey=True)
    valores = [dados[(n, o, e, teste)]["ns"] for n in ns for o in ORDENS for e in ESTRUTURAS]
    nota_y = escala_y(eixos, valores)
    for ax, ordem in zip(eixos, ORDENS):
        for est in ESTRUTURAS:
            ys = [dados[(n, ordem, est, teste)]["ns"] for n in ns]
            desenhar(ax, ns, ys, est)
        ax.set_title(f"Ordem de inserção {NOME_ORDEM[ordem]}")
        eixo_n(ax, ns)
    eixos[0].set_ylabel("Tempo por operação (ns/op)")
    fig.suptitle(f"Teste: {NOME_TESTE[teste]}, tempo por operação em função de n")
    nota = f"Mediana de 5 execuções por lote. Fonte: {fonte}. Eixo x em escala logarítmica; {nota_y}."
    return legenda_e_salvar(fig, eixos[0], saida / f"teste_{teste}.png", nota)


def figura_ordem_arvores(dados, n, saida, fonte):
    """Um PNG por n: BST, AVL e rubro-negra em cada teste, com a ordem de inserção no eixo x."""
    fig, eixos = plt.subplots(2, 3, figsize=(14, 9.2), sharey=True)
    valores = [dados[(n, o, e, t)]["ns"] for o in ORDENS for e in ARVORES for t in TESTES]
    nota_y = escala_y(eixos.ravel(), valores)
    for ax, teste in zip(eixos.ravel(), TESTES):
        for est in ARVORES:
            ys = [dados[(n, o, est, teste)]["ns"] for o in ORDENS]
            desenhar(ax, range(len(ORDENS)), ys, est)
        ax.set_xticks(range(len(ORDENS)))
        ax.set_xticklabels([NOME_ORDEM[o].capitalize() for o in ORDENS])
        ax.set_xlim(-0.3, len(ORDENS) - 0.7)
        ax.set_title(NOME_TESTE[teste])
        ax.set_xlabel("Ordem de inserção (categorias, não é escala)")
        ax.grid(True, alpha=0.3)
    for ax in eixos[:, 0]:
        ax.set_ylabel("Tempo por operação (ns/op)")
    fig.suptitle(f"Árvores BST, AVL e rubro-negra por ordem de inserção, n = {br_num(n)}")
    nota = f"Mediana de 5 execuções por lote. Fonte: {fonte}. {nota_y.capitalize()}."
    return legenda_e_salvar(fig, eixos[0, 0], saida / f"ordem_arvores_n{n}.png", nota)


def figura_arrays(dados, ns, saida, fonte):
    """Array linear x ordenado em busca presente e ausente; linhas = ordens, colunas = testes."""
    testes = ["busca_presente", "busca_ausente"]
    pares = ["array_linear", "array_ordenado"]
    fig, eixos = plt.subplots(3, 2, figsize=(12, 13), sharey=True)
    valores = [dados[(n, o, e, t)]["ns"] for n in ns for o in ORDENS for e in pares for t in testes]
    nota_y = escala_y(eixos.ravel(), valores)
    for i, ordem in enumerate(ORDENS):
        for j, teste in enumerate(testes):
            ax = eixos[i, j]
            for est in pares:
                ys = [dados[(n, ordem, est, teste)]["ns"] for n in ns]
                desenhar(ax, ns, ys, est)
            ax.set_title(f"{NOME_TESTE[teste]}, ordem {NOME_ORDEM[ordem]}")
            eixo_n(ax, ns)
    for ax in eixos[:, 0]:
        ax.set_ylabel("Tempo por operação (ns/op)")
    fig.suptitle("Array linear x array ordenado: busca presente e busca ausente")
    nota = f"Mediana de 5 execuções por lote. Fonte: {fonte}. Eixo x em escala logarítmica; {nota_y}."
    return legenda_e_salvar(fig, eixos[0, 0], saida / "arrays_buscas.png", nota)


def tabelas_por_n_e_ordem(dados, ns, saida):
    """Para cada (n, ordem): tabela teste x estrutura com mediana_ms, em CSV (texto original) e Markdown."""
    gravados = []
    for n in ns:
        for ordem in ORDENS:
            base = saida / f"tabela_n{n}_{ordem}"
            csv_path = base.with_suffix(".csv")
            with open(csv_path, "w", encoding="utf-8", newline="") as f:
                w = csv.writer(f, delimiter=";", lineterminator="\n")
                w.writerow(["teste"] + ESTRUTURAS)
                for t in TESTES:
                    w.writerow([t] + [dados[(n, ordem, e, t)]["ms_txt"] for e in ESTRUTURAS])

            md = [
                f"### n = {br_num(n)}, ordem {NOME_ORDEM[ordem]}",
                "",
                "Mediana do tempo total do lote, em ms (3 casas decimais; o CSV de mesmo nome tem o valor exato).",
                "",
                "| Teste | " + " | ".join(NOME_ESTRUTURA[e] for e in ESTRUTURAS) + " |",
                "|" + "---|" * (len(ESTRUTURAS) + 1),
            ]
            for t in TESTES:
                celulas = [f"{dados[(n, ordem, e, t)]['ms']:.3f}".replace(".", ",") for e in ESTRUTURAS]
                md.append(f"| {NOME_TESTE[t]} | " + " | ".join(celulas) + " |")
            md_path = base.with_suffix(".md")
            md_path.write_text("\n".join(md) + "\n", encoding="utf-8")
            gravados += [csv_path, md_path]
    return gravados


def limpar_saida(figuras, tabelas):
    """Apaga só os arquivos que este script gera, para que uma nova rodada não deixe sobras."""
    for p in figuras.glob("*.png"):
        if p.name.startswith(PREFIXOS_FIGURA):
            p.unlink()
    for p in tabelas.glob("tabela_n*"):
        if p.suffix in (".csv", ".md"):
            p.unlink()


def main(argv=None):
    ap = argparse.ArgumentParser(description="Gera figuras e tabelas do ESTII a partir do resumo.csv.")
    ap.add_argument("--entrada", type=Path, default=ENTRADA_PADRAO, help="resumo.csv de entrada")
    ap.add_argument("--saida", type=Path, default=SAIDA_PADRAO,
                    help="pasta base; grava em <saida>/figuras e <saida>/tabelas")
    args = ap.parse_args(argv)

    try:
        dados, ns = ler_resumo(args.entrada)
    except (ErroFormato, OSError) as e:
        print(f"ERRO: {e}", file=sys.stderr)
        return 1

    figuras = args.saida / "figuras"
    tabelas = args.saida / "tabelas"
    figuras.mkdir(parents=True, exist_ok=True)
    tabelas.mkdir(parents=True, exist_ok=True)
    limpar_saida(figuras, tabelas)

    fonte = args.entrada.name
    gerados = []
    for teste in TESTES:
        gerados.append(figura_por_teste(dados, ns, teste, figuras, fonte))
    for n in ns:
        gerados.append(figura_ordem_arvores(dados, n, figuras, fonte))
    gerados.append(figura_arrays(dados, ns, figuras, fonte))
    gerados += tabelas_por_n_e_ordem(dados, ns, tabelas)

    for p in gerados:
        print(p)
    print(f"total: {len(gerados)} arquivos ({len(ns)} tamanhos, {len(dados)} linhas lidas)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
