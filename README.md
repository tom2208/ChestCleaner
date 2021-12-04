Wiki for 2.4.0: [Wiki File](https://github.com/tom2208/ChestCleaner/blob/v2.4.0/wiki.md)

# ATTENTION
when migrating to version 2.x. A lot of things have changed, that is why it is a new major version. Read the [Migration Guide](https://github.com/tom2208/ChestCleaner/wiki/Migration-Guide-2.0) **BEFORE** upgrading. If you are not upgrading, but installing this plugin for the first time, you can disregard this.

# ChestCleaner

ChestCleaner is a Minecraft Spigot plugin that sorts your chests and inventories. Additionally it autorefills your items in the hotbar.

The used Spigot API version `1.17-R0.1-SNAPSHOT`

For more detailed information have a look at the Spigot article of this plugin: https://www.spigotmc.org/resources/chestcleaner-sorting-plugin.40313/

Also check out the [Github wiki of this repository](https://github.com/tom2208/ChestCleaner/wiki).

For questions and discussions join the [Discord chat](https://discord.gg/4AeApQ5).

---

## Maven

Installing Maven: https://maven.apache.org/install.html

With **mvn -version** you can check if you already installed maven.

### Usage

Building with maven:
If you are using CLI move in to your repository using `cd` (Windows, Mac and Linux).

`mvn package` or: `mvn package -e` 

The latter command logs the stacktrace but has no effect on the build.

On success the maven shows you the path to your jar.

For more information look into this [tutorial](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).
