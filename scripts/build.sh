#!/usr/bin/env bash
# Compila src/estruturas e src/benchmark para out/ (Java 21). Sai com código != 0 se falhar.
set -euo pipefail
cd "$(dirname "$0")/.."

rm -rf out
mkdir -p out
javac --release 21 -Xlint:all -d out src/estruturas/*.java src/benchmark/*.java
echo "build ok: out/"
