#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
command -v tectonic >/dev/null 2>&1 || {
  echo 'Tectonic is required to build reports.' >&2
  exit 1
}
mkdir -p "$ROOT/reports"

# Compile from each lab so screenshots and shared inputs resolve consistently.
# Extra arguments are forwarded to Tectonic (for example, --only-cached).
for source in lab1/lab1.tex lab2/lab2.tex lab3/lab3.tex; do
  echo "Building $source"
  (
    cd -- "$ROOT/$(dirname -- "$source")"
    tectonic "$@" -Z "search-path=$ROOT/latex" \
      --outdir "$ROOT/reports" "$(basename -- "$source")"
  )
done
