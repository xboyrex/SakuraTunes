#!/usr/bin/env python3
"""Add an idempotent README sync step to the existing release workflows."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MARKER = "name: Sync README release details"
STEP = '''\n\n      - name: Sync README release details
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GITHUB_REPOSITORY: ${{ github.repository }}
        run: |
          python3 .github/scripts/sync_readme.py
          if ! git diff --quiet -- README.md; then
            git config user.name "github-actions[bot]"
            git config user.email "41898282+github-actions[bot]@users.noreply.github.com"
            git add README.md
            git commit -m "docs: sync latest release metadata"
            git push
          fi
'''

for relative in (".github/workflows/build.yml", ".github/workflows/release.yml"):
    path = ROOT / relative
    if not path.exists():
        raise SystemExit(f"Required workflow not found: {relative}")
    source = path.read_text(encoding="utf-8")
    if MARKER in source:
        print(f"Already patched: {relative}")
        continue
    path.write_text(source.rstrip() + STEP, encoding="utf-8")
    print(f"Patched: {relative}")
