#!/usr/bin/env python3
"""Refresh the README release panel from published GitHub Releases."""
from __future__ import annotations
import json
import os
import re
import sys
import urllib.error
import urllib.request
from datetime import datetime, timezone
from pathlib import Path

START = "<!-- AUTO-RELEASES:START -->"
END = "<!-- AUTO-RELEASES:END -->"
EMOJI = re.compile("[" "\\U0001F1E6-\\U0001FAFF" "\\U00002600-\\U000027BF" "\\uFE0F" "\\u200D" "\\u20E3" "]+", re.UNICODE)


def github_get(url: str, token: str | None):
    headers = {"Accept": "application/vnd.github+json", "User-Agent": "SakuraTunes-README-Sync", "X-GitHub-Api-Version": "2022-11-28"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    request = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(request, timeout=30) as response:
            return json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as error:
        detail = error.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"GitHub API returned HTTP {error.code}: {detail}") from error


def clean(value: str) -> str:
    value = EMOJI.sub("", value or "")
    value = value.replace("\r\n", "\n").replace("\r", "\n")
    return re.sub(r"[ \t]+\n", "\n", value).strip()


def date_label(value: str | None) -> str:
    if not value:
        return "Date unavailable"
    try:
        parsed = datetime.fromisoformat(value.replace("Z", "+00:00"))
        return parsed.astimezone(timezone.utc).strftime("%d %b %Y")
    except ValueError:
        return value[:10]


def size_label(value: int | None) -> str:
    return f"{value / (1024 * 1024):.1f} MB" if isinstance(value, int) else "Unknown"


def render(releases: list[dict]) -> str:
    published = [release for release in releases if not release.get("draft")]
    published.sort(key=lambda release: release.get("published_at") or "", reverse=True)
    if not published:
        return "No published release was returned by GitHub. See the [Releases page](https://github.com/xboyrex/SakuraTunes/releases)."

    latest = published[0]
    tag = str(latest.get("tag_name") or "latest")
    title = clean(str(latest.get("name") or f"Sakura Tunes {tag}"))
    url = str(latest.get("html_url") or "https://github.com/xboyrex/SakuraTunes/releases")
    channel = "Pre-release" if latest.get("prerelease") else "Stable release"
    lines = [
        f"**[{title}]({url})**", "",
        f"`{tag}` · {channel} · Published {date_label(latest.get('published_at'))}", "",
        "[Open release page](https://github.com/xboyrex/SakuraTunes/releases/latest) · [All releases](https://github.com/xboyrex/SakuraTunes/releases)", "",
        "#### Files attached to this release", "",
    ]
    assets = latest.get("assets") or []
    if assets:
        lines += ["| File | Size | Downloads |", "| :--- | ---: | ---: |"]
        for asset in assets:
            name = str(asset.get("name") or "Download").replace("|", "\\|")
            asset_url = str(asset.get("browser_download_url") or url)
            downloads = int(asset.get("download_count") or 0)
            lines.append(f"| [{name}]({asset_url}) | {size_label(asset.get('size'))} | {downloads:,} |")
    else:
        lines.append("No downloadable files are attached to this release yet.")

    notes = clean(str(latest.get("body") or ""))
    lines += ["", "<details open>", f"<summary>Release notes for {tag}</summary>", "", notes or "No release notes were attached to this release.", "", "</details>", "", "#### Recent release history", "", "| Version | Published | Channel |", "| :--- | :--- | :--- |"]
    for release in published[:5]:
        version = str(release.get("tag_name") or "untagged")
        release_url = str(release.get("html_url") or url)
        release_channel = "Pre-release" if release.get("prerelease") else "Stable"
        lines.append(f"| [{version}]({release_url}) | {date_label(release.get('published_at'))} | {release_channel} |")
    return "\n".join(lines)


def main() -> int:
    repository = os.environ.get("GITHUB_REPOSITORY", "xboyrex/SakuraTunes")
    token = os.environ.get("GITHUB_TOKEN")
    if not re.fullmatch(r"[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+", repository):
        raise RuntimeError("GITHUB_REPOSITORY has an invalid owner/repository format.")
    api = f"https://api.github.com/repos/{repository}/releases?per_page=10"
    releases = github_get(api, token)
    if not isinstance(releases, list):
        raise RuntimeError("GitHub Releases API returned an unexpected response.")

    path = Path("README.md")
    source = path.read_text(encoding="utf-8")
    if START not in source or END not in source:
        raise RuntimeError("README release markers are missing; refusing to overwrite other content.")
    start = source.index(START) + len(START)
    end = source.index(END)
    if end < start:
        raise RuntimeError("README release markers are out of order.")

    updated = source[:start] + "\n\n" + render(releases) + "\n\n" + source[end:]
    if updated == source:
        print("README release panel is already current.")
        return 0
    path.write_text(updated, encoding="utf-8")
    print("README release panel refreshed.")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:
        print(f"README sync failed: {exc}", file=sys.stderr)
        raise SystemExit(1)
