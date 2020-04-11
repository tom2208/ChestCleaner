package chestcleaner.config;

import chestcleaner.config.serializable.ListCategory;
import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.config.serializable.WordCategory;
import chestcleaner.main.ChestCleaner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This is a singleton class to combine all configs and their utility methods
 * for this project.
 * 
 * @author Tom2208
 *
 */
public class PluginConfig {

	private static PluginConfig instance = null;

	private File playerDataFile;
	private FileConfiguration playerDataConfig;

	protected PluginConfig() {
		ChestCleaner.main.saveDefaultConfig();
		ChestCleaner.main.getConfig().options().copyDefaults(true);
		playerDataFile = new File("plugins/" + ChestCleaner.main.getName(), "playerdata.yml");
		playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
	}

	/**
	 * Loads the Locale and Categories from the config.yml,
	 * or uses the defaults from the jar/default config.yml if not set
	 */
	public void loadConfig() {

		FileConfiguration config = ChestCleaner.main.getConfig();
	    // Locale
		String language = config.getString(ConfigPath.LOCALE_LANGUAGE.getPath());
		String country = config.getString(ConfigPath.LOCALE_COUNTRY.getPath());
		String variant = ChestCleaner.main.getDescription().getVersion().replace(".", "-");
		ChestCleaner.main.setLocale(language, country, variant);
		// Categorizers
		CategoryLoader.loadCategorizers(
				(List<WordCategory>) config.getList(ConfigPath.CATEGORIES_WORDS.getPath()),
				(List<ListCategory>) config.getList(ConfigPath.CATEGORIES_LISTS.getPath()),
				(List<MasterCategory>) config.getList(ConfigPath.CATEGORIES_MASTER.getPath())
		);

		ChestCleaner.main.saveConfig();
	}

	public static FileConfiguration getConfig() {
		return ChestCleaner.main.getConfig();
	}

	public static void setIntoConfig(ConfigPath path, Object obj) {
		ChestCleaner.main.getConfig().set(path.getPath(), obj);
		ChestCleaner.main.saveConfig();
	}

	public static FileConfiguration getPlayerData() {
		return getInstance().playerDataConfig;
	}

	public static void setIntoPlayerData(Player p, PlayerDataPath path, Object obj) {
		getPlayerData().set(path.getPath(p), obj);
		savePlayerData();
	}

	/**
	 * Saves this {@code FileConfiguration} to the the ChestCleaner folder. If the
	 * file does not exist, it will be created. If it already exists, it will be
	 * overwritten.
	 * 
	 * This method will save using the system default encoding, or possibly using
	 * UTF8.
	 *
	 * @param configFile the file in which the FileConfiguration should be saved in.
	 * @param config     the FileCondiguration that should get saved into the file.
	 */
	private static void save(File configFile, FileConfiguration config) {

		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void savePlayerData() {
		PluginConfig c = getInstance();
		save(c.playerDataFile, c.playerDataConfig);
	}


	/**
	 * Returns the instance of this singleton if it's null it creates one.
	 * 
	 * @return The Instance of the singleton.
	 */
	public static PluginConfig getInstance() {
		if (instance == null) {
			instance = new PluginConfig();
		}
		return instance;
	}

	public enum ConfigPath {
		DEFAULT_AUTOSORT("defaultautosort"),
		DEFAULT_EVALUATOR("defaultevaluator"),
		DEFAULT_SORTING_PATTERN("defaultsortingpattern"),
		LOCALE_LANGUAGE("locale.lang"),
		LOCALE_COUNTRY("locale.country"),
		CLEANING_ITEM("cleaningItem"),
		CLEANING_ITEM_ACTIVE("active"),
		CLEANING_ITEM_DURABILITY("durability"),
		OPEN_INVENTORY_MODE("openinventoryeventmode"),
		CONSUMABLES_REFILL("consumablesrefill"),
		BLOCK_REFILL("blockrefill"),
		INVENTORY_PERMISSION_ACTIVE("cleanInventorypermissionactive"),
		BLACKLIST("blacklist"),
		INVENTORY_BLACKLIST("inventoryblacklist"),
		COOLDOWN_TIME("cooldown.time"),
		COOLDOWN_ACTIVE("cooldown.active"),
		UPDATE_CHECKER_ACTIVE("updateMassageActive"),
		CATEGORIES_ORDER("categories.order"),
		CATEGORIES_WORDS("categories.words"),
		CATEGORIES_LISTS("categories.lists"),
		CATEGORIES_MASTER("categories.master");

		private String path;

		ConfigPath(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}

	public enum PlayerDataPath {

		AUTOSORT("autosort"),
		PATTERN("sortingpattern");

		private String path;

		PlayerDataPath(String path) {
			this.path = path;
		}

		public String getPath(Player p) {
			return p.getUniqueId() + "." + path;
		}
	}
}
