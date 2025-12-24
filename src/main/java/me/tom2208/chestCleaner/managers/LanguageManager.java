package me.tom2208.chestCleaner.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final JavaPlugin plugin;
    private final Map<String, String> messages = new HashMap<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        String langCode = plugin.getConfig().getString("language", "en");
        String fileName = "messages_" + langCode + ".yml";

        File langFile = new File(plugin.getDataFolder(), "lang/" + fileName);

        if (!langFile.exists()) {
            try {
                plugin.saveResource("lang/" + fileName, false);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Sprachdatei " + fileName + " nicht gefunden! Fallback auf EN.");
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);

        messages.clear();
        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                messages.put(key, config.getString(key));
            }
        }

        plugin.getLogger().info("Geladene Sprache: " + langCode + " (" + messages.size() + " Nachrichten)");
    }

    public Component getMessage(String key, TagResolver... placeholders) {
        String rawMessage = messages.getOrDefault(key, "<red>Missing key: " + key);
        String prefix = messages.getOrDefault("prefix", "");

        String fullMessage = prefix + rawMessage;

        return miniMessage.deserialize(fullMessage, placeholders);
    }
}
