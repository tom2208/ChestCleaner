package me.tom2208.chestCleaner;

import me.tom2208.chestCleaner.commands.ChestCleanerCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ChestCleaner extends JavaPlugin {

    @Override
    public void onEnable() {
        ChestCleanerCommand cmd = new ChestCleanerCommand();
        Objects.requireNonNull(getCommand("chestcleaner")).setExecutor(cmd);
        Objects.requireNonNull(getCommand("chestcleaner")).setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
