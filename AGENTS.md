# AGENTS.md — Awepeeps ChestCleaner

Scope: this file applies to **every file in this repository**. Human overview: [README.md](README.md).
Repeatable local build/review: [.agents/skills/awepeeps-chestcleaner-build-review/SKILL.md](.agents/skills/awepeeps-chestcleaner-build-review/SKILL.md).

This is a maintained standalone fork of tom2208/ChestCleaner (GPL-3.0). Keep the upstream
attribution and `LICENSE` intact.

## Purpose and layout

- Paper 26.3 / Java 25 plugin that sorts storage and player inventories with the cleaning item.
- Sources: `src/chestcleaner/`; unit tests: `tests/chestcleaner/`.
- Runtime resources (repository root): `plugin.yml`, `config.yml`, `ChestCleaner_*_*.properties`.
  Only the four files for the current version (`ChestCleaner_2-4-5-awepeeps-3_*`) are packaged by `pom.xml`;
  the older `ChestCleaner_2-4-3_*` / `ChestCleaner_2-4-5-awepeeps-1_*` files are kept as history and are not.
- Reproducible build helper: `automation/build-chestcleaner.sh`. Release flow: [RELEASING.md](RELEASING.md);
  build details: [docs/maintenance.md](docs/maintenance.md); release notes: [CHANGELOG.md](CHANGELOG.md).

## Build and test

- Requires JDK 25; `just install` installs Temurin 25, Maven and the pinned tools via mise.
- `just check` — format, workflow lint, one clean `verify` (tests included).
- `just reproducible` — two clean `verify` runs, `cmp`, and the SHA-256; runs on `main` and on
  release pull requests (a PR that changes `CHANGELOG.md`).
- Reproducible containerized build: `bash automation/build-chestcleaner.sh`.
- Output jar: `target/chestcleaner-2.4.5-awepeeps.3.jar`.
- Open changes as a pull request against `main`.

## Interaction policy (must be preserved)

- A normal right-click opens a container **unsorted**. Sneak-right-click sorts the clicked
  container before the player's own inventory is considered.
- A cancelled/protected/locked/blacklisted interaction, missing permission, or active cooldown
  prevents container sorting and does not fall through to personal-inventory sorting.
- `cleaningItem.openEvent` stays loadable and configurable but has no sorting behavior in this fork.
- Keep the plugin name, data folder, command and permission names, and legacy config keys stable.
- `updateMessage` defaults to false; when disabled the plugin makes no network request.

## Native defaults and privacy

- Keep sorting on the existing `InventorySorter`/`SortingEvent` path and the Paper API already in
  use; do not add wrappers or dependencies for their own sake.
- This is a **public** repository: never add host paths, usernames, IPs, seeds, world coordinates,
  server or instance IDs, logs, config dumps, backups, or secrets.
- Exact deploy pins (source commit + jar SHA-256) live in the private minecraft-deployment
  repository, never here.

## Git hooks

Hooks are installed once per checkout with `just hooks` and run through Lefthook, loading the
shared jobs from `awepeeps/.github` at the `hooks-v2` tag. Agents never bypass them (C7.11):
no `LEFTHOOK=0`, no `git commit --no-verify`, and no `lefthook-local.yml`.
