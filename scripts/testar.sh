#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

classes="out-tests"
rm -rf "$classes"
mkdir -p "$classes"
javac --release 21 -d "$classes" src/estruturas/*.java tests/Testes.java
java -cp "$classes" tests.Testes
