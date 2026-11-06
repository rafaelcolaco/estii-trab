#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

out_dir="resultados"
args=("$@")
for ((i = 0; i < ${#args[@]}; i++)); do
    if [[ "${args[i]}" == "--out" && $((i + 1)) -lt ${#args[@]} ]]; then
        out_dir="${args[i + 1]}"
    fi
done

scripts/build.sh
mkdir -p "$out_dir"
{
    echo "data: $(date '+%Y-%m-%d %H:%M:%S %z')"
    echo "cpu: $(awk -F: '/model name/ {sub(/^ +/, "", $2); print $2; exit}' /proc/cpuinfo)"
    echo "ram: $(awk '/MemTotal/ {printf "%.1f GiB", $2 / 1048576}' /proc/meminfo)"
    echo "opcoes_jvm: -Xms1g -Xmx1g"
    java -version 2>&1
} > "$out_dir/ambiente.txt"
exec java -Xms1g -Xmx1g -cp out benchmark.Benchmark "$@"
