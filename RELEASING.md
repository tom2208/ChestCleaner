# Releasing

The build is reproducible for a fixed toolchain (JDK 25 + pinned plugin
versions), so the SHA-256 published by CI can be pinned with confidence.

1. Bump `<version>` in `pom.xml` (and `<version.properties>`), `plugin.yml`, the
   bundled `ChestCleaner_*_*.properties` resource names and `CHANGELOG.md`.
2. Open a pull request against `main`.
3. Wait for the **Build** workflow to pass on the pull request, then merge it.
4. Open the successful **Build** run for the merge commit, download the
   `awepeeps-chestcleaner` artifact and copy the jar file name and SHA-256 from
   the run's job summary.
5. Update the pin in `minecraft-deployment`: set the exact source commit
   (the merge commit SHA) and jar SHA-256 in `config/chestcleaner-source.env` and
   `config/plugins.list`.
6. Never pin a moving branch; always pin the immutable merge commit plus hash.
