# ChestCleaner

ChestCleaner sorts storage inventories and player inventories with the configured cleaning item. This repository is a maintained standalone fork of [tom2208/ChestCleaner](https://github.com/tom2208/ChestCleaner), based on upstream tag `v2.4.4` (`131a11f7f64b3114218fb1141bceeb8197354c6e`). Bug reports for upstream behaviour go to [tom2208/ChestCleaner issues](https://github.com/tom2208/ChestCleaner/issues); this fork keeps its own issues disabled.

## Requirements

- A Paper 26.3 server on Java 25. The plugin declares `api-version: "26.3"` and compiles against `io.papermc.paper:paper-api:26.3.build.175-stable`.
- No other plugin is required; ChestCleaner is standalone.

## Install

1. Build the reproducible jar (see [Build and test](#build-and-test)). The artifact is `target/chestcleaner-2.4.5-awepeeps.3.jar`.
2. Stop the server.
3. Drop the jar into the server's `plugins/` directory.
4. Start the server. On first load ChestCleaner creates `plugins/ChestCleaner/`, writes `playerdata.yml`, and copies the bundled `config.yml` there.
5. Give a player the brush with `/cleaningitem get`, then sort with sneak-right-click (see [Cleaning item behavior](#cleaning-item-behavior)).

The live Awepeeps instance does not install a hand-built jar: the private minecraft-deployment repository pins an exact reviewed source commit and verifies the jar SHA-256 before installing it. Do not copy a jar, a live server config, or any host detail back into this public repository.

## Cleaning item behavior

- Right-click a container normally to open it. The brush does not sort it.
- Sneak and right-click a container to sort that container. A successful sort consumes one configured durability use and cancels the open interaction.
- Sneak and right-click air or a non-container block to sort your own inventory when you have `chestcleaner.cleaningitem.use.owninventory`.
- A cancelled/protected block interaction, a locked container, a blacklisted container, missing permission, or active cooldown prevents container sorting and does not fall through to sorting the player's inventory.
- Sorting continues to raise the existing `SortingEvent`, so other plugins can cancel it. The existing inventory sorting algorithm and patterns are retained.

`interaction-cooldown-ms` (default `750`, `0` disables) debounces repeated player interactions per player and per action kind. Bedrock clients (Switch and other consoles via Geyser/Floodgate) can deliver one press as two interaction or click events; the window drops the duplicate so container sorting, player-inventory sorting, and click-sort run once. Refill and sort-on-close are not debounced: they are not toggles, and debouncing close-sort would skip a second chest closed inside the window. Values outside `0-5000` fall back to the default with one warning.

`cleaningItem.openEvent` remains in the config and command interface for compatibility with existing configs, but it is ignored by this fork. Normal container opens always pass through unsorted, and sneak-right-click sorting works regardless of the old value. Auto-sort-on-close remains controlled by the existing player setting.

The plugin name, data folder (`ChestCleaner`), command names, permission nodes, and existing config keys are retained. The bundled `updateMessage` setting defaults to `false`. When it is disabled no update check runs and no network request is made at all. Enabling it makes the upstream optional update checker request the public SpigotMC version endpoint for resource 40313, without authentication; if that request fails the plugin logs a single warning line instead of a stack trace.

## Configuration

The shipped `config.yml` lives in this repository root, is packaged into the jar, and is copied to `plugins/ChestCleaner/config.yml` on first load. Every key is listed below with its default in the shipped file. Missing keys use the default; the command interface writes new values back to the same file. Admin defaults are changed with `/sortingadmin`, per-player preferences with `/sortingconfig`.

| Key | Default | Meaning |
| --- | --- | --- |
| `updateMessage` | `false` | Upstream update check. When `false` (default) no check runs and the plugin makes **no** network request. When `true`, an unauthenticated HTTPS request is made to the public SpigotMC version endpoint for resource 40313; a failed request logs one warning line instead of a stack trace. |
| `interaction-cooldown-ms` | `750` | Debounce window in milliseconds that drops a repeated tap-triggered action from the same player. It covers the tap-triggered sorts only: container sort, own-inventory sort and click-sort. It absorbs the duplicate interaction or click event a Bedrock (Geyser/Floodgate) client can send for one press. `0` disables it; valid `0`-`5000`, anything else falls back to `750` and logs one warning. Refill and sort-on-close are **not** debounced. |
| `locale.lang` | `en` | Language of the bundled message bundle. Bundled bundles: `en`/`GB`, `de`/`DE`, `ja`/`JP`, `zh`/`CN`. |
| `locale.country` | `GB` | Country of the bundled message bundle (see `locale.lang`). |
| `default.autosort` | `false` | Default auto-sort-on-close preference for players who have not saved their own setting. |
| `default.pattern` | `LEFT_TO_RIGHT_TOP_TO_BOTTOM` | Default sorting pattern (the order items are placed into). |
| `default.categories` | `[AlphaBackAsc, IsBlock]` | Default category order used while sorting. |
| `default.sorting.notification` | `true` | Default chat notification on a successful sort. |
| `default.sorting.sound.active` | `true` | Default sorting-sound toggle. |
| `default.sorting.sound.name` | `ENTITY_PIG_SADDLE` | Default sorting sound. Legacy enum-style names and namespaced keys (`entity.pig.saddle`, `minecraft:entity.pig.saddle`) are both accepted; values written back and shown by `/sortingadmin` stay in the legacy style. |
| `default.sorting.sound.volume` | `"2.0"` | Default sound volume (stored as a string). |
| `default.sorting.sound.pitch` | `"2.0"` | Default sound pitch (stored as a string). |
| `default.refill.blocks` | `true` | Default autorefill of blocks into the hotbar. |
| `default.refill.consumables` | `true` | Default autorefill of consumables. |
| `default.refill.breakables` | `true` | Default autorefill of breakables (tools, shields, and similar). |
| `default.clicksort` | `true` | Default click-sort preference (right-click outside an open inventory to sort it). |
| `cleaningItem.active` | `true` | Enable the cleaning item (brush). |
| `cleaningItem.durability` | `true` | A successful sort consumes one durability use of the brush. |
| `cleaningItem.openEvent` | `true` | Kept loadable and configurable for compatibility with existing configs, but **ignored** by this fork: a normal open never sorts and sneak-right-click sorting works whatever this value is. |
| `cleaningItem.item` | serialized `IRON_HOE` | The serialized `ItemStack` used as the brush. Change it in game with `/cleaningitem set` while holding the wanted item. |
| `cooldown.active` | `true` | Retained legacy cooldown block. The cooldown this fork actually reads is per action: `cooldown.active.<action>` and `cooldown.time.<action>`, for the actions `SORTING` and `CLEANING_ITEM_GET` (written by `/sortingadmin cooldown`). The flat `cooldown.active`/`cooldown.time` keys are kept for config compatibility. |
| `cooldown.time` | `5000` | Retained legacy cooldown duration in milliseconds (see `cooldown.active`). |
| `blacklist.stacking` | `[air]` | Materials that are never stacked together during a sort, so separate stacks of them are preserved. |
| `blacklist.inventory` | `[furnace]` | Container types the brush refuses to sort. |
| `blacklist.autorefill` | *(absent)* | Optional list of materials that are never auto-refilled. Not present in the shipped `config.yml`; the plugin uses an empty list while it is missing. |
| `categories.words` | `[StoneWord, StairWord, SlabWord, CarpetWord]` | `WordCategory` entries (serialized `name` plus `word`): an item whose material name contains the word is grouped into that category. |
| `categories.lists` | `[StoneList, DirtList]` | `ListCategory` entries (serialized `name` plus an explicit material `list`). |
| `categories.master` | `[StoneMaster]` | `MasterCategory` entries (serialized `name` plus a `list` of other category names to nest). |

## Commands and permissions

All commands and permission nodes are defined in [`plugin.yml`](plugin.yml) and are unchanged from upstream.

| Command | Aliases | Purpose |
| --- | --- | --- |
| `/cleaninventory` | `/cleaninv`, `/cinv` | Sort an inventory by player, own, or block coordinates. |
| `/cleaningitem` | `/ci` | Get or configure the brush (`get`, `set`, `give`, `name`, `lore`, `active`, `durabilityLoss`, `openEvent`). |
| `/blacklist` | `/bl` | List, clear, add, or remove blacklist entries. |
| `/sortingconfig` | `/sconfig`, `/sc` | Per-player sorting preferences (autosort, categories, notification, pattern, refill, sound). |
| `/sortingadmin` | `/sadmin`, `/sa` | Server-wide sorting defaults for the same settings, plus the cooldown commands. |

Keep the plugin name, data folder, command names, permission nodes, and existing config keys stable (see [AGENTS.md](AGENTS.md)).

## Build and test

Run `automation/build-chestcleaner.sh` from this repository root. It builds in the digest-pinned `maven:3.9.16-eclipse-temurin-25` container (`sha256:93b8a14ea2f412782e4e842651273b4d903e35cc496284f178fbbe2d67d00976`), the same image digest the deployment's `automation/build-chestcleaner.sh` uses, and runs `mvn --batch-mode --no-transfer-progress clean verify`. For a direct build without Docker, run the same `mvn --batch-mode --no-transfer-progress clean verify` on JDK 25. The Maven plugin and test dependency versions are pinned in `pom.xml`; the plugin compiles against the Paper 26.3 API (`io.papermc.paper:paper-api:26.3.build.175-stable`) with Java 25, matching the server it runs on.

The reproducible plugin artifact is `target/chestcleaner-2.4.5-awepeeps.3.jar`.

## Rollback

1. Stop the server.
2. Remove `plugins/chestcleaner-2.4.5-awepeeps.3.jar`.
3. If the update changed `plugins/ChestCleaner/config.yml`, restore the previous copy, then start the server with the previous jar. Player preferences live in `plugins/ChestCleaner/playerdata.yml`; the plugin recreates a missing `config.yml` with the bundled defaults.

The deployment's rollback transaction (in the private minecraft-deployment repository) backs up the current jar and the exact config, restores them if the new jar does not load cleanly, and records each of the four versioned locale resource files so only the copies it created are removed. Mirror that here: keep the previous jar and config until the new one has loaded cleanly.

## Repository docs

- [AGENTS.md](AGENTS.md) — repository-wide rules for AI coding agents.
- [.agents/skills/awepeeps-chestcleaner-build-review/SKILL.md](.agents/skills/awepeeps-chestcleaner-build-review/SKILL.md) — reproducible local build and review workflow.
- [RELEASING.md](RELEASING.md) — release and hash-pinning steps.
- [CHANGELOG.md](CHANGELOG.md) — release notes (Keep a Changelog format).
- [CONTRIBUTING.md](CONTRIBUTING.md) — how to build, test and propose a change.
- [SECURITY.md](SECURITY.md) — how to report a security issue.
- [docs/maintenance.md](docs/maintenance.md) — build, release identity, and network behavior notes.

## Upstream credits and license

ChestCleaner was created by Tom Hauschild (`tom2208`) with contributions from `leanNiki`. This fork retains the upstream GNU General Public License version 3.0; see [LICENSE](LICENSE). Upstream history and the original documentation are available at the repository linked above.
