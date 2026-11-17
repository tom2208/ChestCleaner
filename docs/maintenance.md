# Maintenance

## Build

Run `automation/build-chestcleaner.sh` from the repository root. It runs Maven 3.9.16 on Eclipse Temurin 25 in the digest-pinned `maven:3.9.16-eclipse-temurin-25@sha256:93b8a14ea2f412782e4e842651273b4d903e35cc496284f178fbbe2d67d00976` image (the digest is set in the script and matches the deployment's `automation/build-chestcleaner.sh`), then runs `mvn --batch-mode --no-transfer-progress clean verify`. Output: `target/chestcleaner-2.4.5-awepeeps.3.jar`.

The JUnit/Mockito and Maven plugin versions are fixed in `pom.xml`; the plugin compiles against the Paper 26.3 API (`io.papermc.paper:paper-api:26.3.build.175-stable`) with Java 25. Maven's output timestamp is fixed so repeated builds from the same source and pinned inputs produce the same jar bytes; the Maven descriptor is omitted from the jar so the Windows and Linux builds agree.

## Locale resources

`pom.xml` packages only the current version's bundle, `ChestCleaner_${version.properties}_*.properties` (`version.properties` is `2-4-5-awepeeps-3`): `_de_DE`, `_en_GB`, `_ja_JP` and `_zh_CN`. At startup `ChestCleaner` derives the bundle name from the plugin version with dots replaced by dashes and `saveResource`s exactly those four files, so only they are shipped in the jar.

The older root files `ChestCleaner_2-4-3_*.properties` and `ChestCleaner_2-4-5-awepeeps-1_*.properties` are **intentionally kept as history** and are **not** packaged: no `<include>` in `pom.xml` references them and no code loads them. Do not delete them as part of a documentation change; if they are ever removed, do it in a dedicated change that also records the reason. The deployment transaction still treats the four *current* locale files as part of the artifact (see `docs/chestcleaner.md` in the private awepeeps-minecraft-deployment repository).

## Release identity

Keep the Bukkit plugin name `ChestCleaner`, its data folder, command and permission names, and the legacy config schema stable. `cleaningItem.openEvent` remains loadable and configurable for compatibility but has no sorting behavior in this fork. Normal container opens must not sort; sneak-right-click dispatches container sorting before personal inventory sorting.

`updateMessage` defaults to false. When it is disabled no update check runs and no network request is made. If enabled, the upstream `UpdateChecker` makes an unauthenticated HTTPS request to the SpigotMC version endpoint for resource 40313 and logs a single warning line if that request fails.

The code retains the upstream GPL-3.0 license and attribution. Do not replace or remove the license file when changing the fork.
