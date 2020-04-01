package chestcleaner.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chestcleaner.sorting.DefaultSortingList;
import chestcleaner.sorting.v2.Categorizers;
import chestcleaner.sorting.v2.ListCategoryCategorizer;
import chestcleaner.sorting.v2.PredicateCategorizer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.main.ChestCleaner;
import chestcleaner.sorting.CooldownManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;
import chestcleaner.utils.PlayerDataManager;

/**
 * This is a singleton class to combine all configs and their utility methods
 * for this project.
 * 
 * @author Tom2208
 *
 */
public class PluginConfig {

	private static PluginConfig instance = null;

	private File configFile;
	private FileConfiguration config;
	private File playerDataConfigFile;
	private FileConfiguration playerDataConfig;

	protected PluginConfig() {
		configFile = new File("plugins/" + ChestCleaner.main.getName(), "config.yml");
		ConfigurationSerialization.registerClass(WordCategory.class);
		ConfigurationSerialization.registerClass(ListCategory.class);
		config = YamlConfiguration.loadConfiguration(configFile);
		playerDataConfigFile = new File("plugins/" + ChestCleaner.main.getName(), "playerdata.yml");
		playerDataConfig = YamlConfiguration.loadConfiguration(playerDataConfigFile);
	}

	/**
	 * Loads all variables out of the config, if the config does not exist it will
	 * generate one with the default values for the variables.
	 */
	public void loadConfig() {

		save(configFile, config);

		if (!setIfDoesntContains(ConfigPath.DEFAULT_AUTOSORT, PlayerDataManager.getInstance().isDefaultAutoSort())) {
			PlayerDataManager.getInstance()
					.setDefaultAutoSort(config.getBoolean(ConfigPath.DEFAULT_AUTOSORT.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.DEFAULT_EVALUATOR, EvaluatorType.DEFAULT.name())) {
			EvaluatorType.DEFAULT = EvaluatorType
					.getEvaluatorTypByName(config.getString(ConfigPath.DEFAULT_EVALUATOR.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.DEFAULT_SORTING_PATTERN, SortingPattern.DEFAULT.name())) {
			SortingPattern.DEFAULT = SortingPattern
					.getSortingPatternByName(config.getString(ConfigPath.DEFAULT_SORTING_PATTERN.getPath()));
		}

		if (config.contains(ConfigPath.LOCALE_LANGUAGE.getPath())
				&& config.contains(ConfigPath.LOCALE_COUNTRY.getPath())) {
			String language = config.getString(ConfigPath.LOCALE_LANGUAGE.getPath());
			String country = config.getString(ConfigPath.LOCALE_COUNTRY.getPath());
			ChestCleaner.main.setLocale(language, country, ChestCleaner.main.getDescription().getVersion().replace(".", "-"));
		} else {
			config.set(ConfigPath.LOCALE_LANGUAGE.getPath(), Locale.UK.getLanguage());
			config.set(ConfigPath.LOCALE_COUNTRY.getPath(), Locale.UK.getCountry());
			ChestCleaner.main.setLocale(Locale.UK.getLanguage(), Locale.UK.getCountry(), ChestCleaner.main.getDescription().getVersion().replace(".", "-"));
		}

		if (!setIfDoesntContains(ConfigPath.CLEANING_ITEM, new ItemStack(Material.IRON_HOE))) {
			ChestCleaner.item = config.getItemStack(ConfigPath.CLEANING_ITEM.getPath());
		}

		if (!setIfDoesntContains(ConfigPath.CLEANING_ITEM_ACTIVE, true)) {
			PluginConfigManager.getInstance()
					.setCleaningItemActive(config.getBoolean(ConfigPath.CLEANING_ITEM_ACTIVE.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.CLEANING_ITEM_DURABILITY, true)) {
			PluginConfigManager.getInstance()
					.setDurabilityLossActive(config.getBoolean(ConfigPath.CLEANING_ITEM_DURABILITY.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.OPEN_INVENTORY_MODE, false)) {
			PluginConfigManager.getInstance()
					.setDurabilityLossActive(config.getBoolean(ConfigPath.OPEN_INVENTORY_MODE.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.CONSUMABLES_REFILL, true)) {
			PluginConfigManager.getInstance()
					.setConsumablesRefillActive(config.getBoolean(ConfigPath.CONSUMABLES_REFILL.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.BLOCK_REFILL, true)) {
			PluginConfigManager.getInstance()
					.setBlockRefillActive(config.getBoolean(ConfigPath.BLOCK_REFILL.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.INVENTORY_PERMISSION_ACTIVE, true)) {
			PluginConfigManager.getInstance()
					.setCleanInvPermission(config.getBoolean(ConfigPath.INVENTORY_PERMISSION_ACTIVE.getPath()));
		}

		if (!setIfDoesntContains(ConfigPath.COOLDOWN_ACTIVE, true)) {
			CooldownManager.getInstance().setActive(config.getBoolean(ConfigPath.COOLDOWN_ACTIVE.getPath()));
		}

		setIfDoesntContains(ConfigPath.CATEGORIES_ORDER, DefaultSortingList.DEFAULT_CATEGORIES_ORDER);
		PluginConfigManager.getInstance().setCategorizationOrder(
				(List<String>) config.getList(ConfigPath.CATEGORIES_ORDER.getPath()));

		setIfDoesntContains(ConfigPath.CATEGORIES_WORDS, DefaultSortingList.DEFAULT_WORD_CATEGORIES);
		setIfDoesntContains(ConfigPath.CATEGORIES_LISTS, DefaultSortingList.DEFAULT_LIST_CATEGORIES);

		CategoryLoader.loadCategorizers(
				(List<WordCategory>) config.getList(ConfigPath.CATEGORIES_WORDS.getPath()),
				(List<ListCategory>) config.getList(ConfigPath.CATEGORIES_LISTS.getPath())
		);


		if (config.contains(ConfigPath.COOLDOWN_TIME.getPath())) {
			CooldownManager.getInstance().setCooldown(config.getInt(ConfigPath.COOLDOWN_TIME.getPath()));
		}

		if (config.contains(ConfigPath.UPDATE_CHECKER_ACTIVE.getPath())) {
			PluginConfigManager.getInstance()
					.setUpdateCheckerActive((config.getBoolean(ConfigPath.UPDATE_CHECKER_ACTIVE.getPath())));
		}

		if (config.contains(ConfigPath.BLACKLIST.getPath())) {
			List<String> list = config.getStringList(ConfigPath.BLACKLIST.getPath());
			ArrayList<Material> materials = new ArrayList<>();

			for (String name : list) {
				materials.add(Material.getMaterial(name));
			}
			InventorySorter.blacklist = materials;
		}

		if (config.contains(ConfigPath.INVENTORY_BLACKLIST.getPath())) {
			List<String> list = config.getStringList(ConfigPath.INVENTORY_BLACKLIST.getPath());
			ArrayList<Material> materials = new ArrayList<>();

			for (String name : list) {
				materials.add(Material.getMaterial(name));
			}
			BlacklistCommand.inventoryBlacklist = materials;
		}
		save(configFile, config);
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
	private void save(File configFile, FileConfiguration config) {

		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* SORTINGPATTERN */
	public void setSortingPattern(SortingPattern pattern, Player p) {
		playerDataConfig.set(p.getUniqueId() + ".sortingpattern", pattern.name());
		save(playerDataConfigFile, playerDataConfig);
	}

	public SortingPattern getSortingPattern(Player p) {
		return SortingPattern.getSortingPatternByName(playerDataConfig.getString(p.getUniqueId() + ".sortingpattern"));
	}

	/* EVALUATORTYP */
	public void setEvaluatorTyp(EvaluatorType pattern, Player p) {
		playerDataConfig.set(p.getUniqueId() + ".evaluatortyp", pattern.name());
		save(playerDataConfigFile, playerDataConfig);
	}

	public EvaluatorType getEvaluatorType(Player p) {
		return EvaluatorType.getEvaluatorTypByName(playerDataConfig.getString(p.getUniqueId() + ".evaluatortyp"));
	}

	/* AUTOSORT */
	public void setAutoSort(boolean b, Player p) {
		playerDataConfig.set(p.getUniqueId() + ".autosort", b);
		save(playerDataConfigFile, playerDataConfig);
	}

	public boolean containsAutoSort(Player p) {
		return playerDataConfig.contains(p.getUniqueId() + ".autosort");
	}

	public boolean getAutoSort(Player p) {
		return playerDataConfig.getBoolean(p.getUniqueId() + ".autosort");
	}

	/**
	 * Sets the {@code value} in the config in the path {@code path}. Returns true
	 * if i did successfully.
	 * 
	 * @param path  a path in the .yml configuration.
	 * @param value the object you want to check or set.
	 * @return true if the path was set otherwise false.
	 */
	private boolean setIfDoesntContains(ConfigPath path, Object value) {
		if (!config.contains(path.getPath())) {
			config.set(path.getPath(), value);
			return true;
		}
		return false;
	}

	public void setIntoConfig(ConfigPath path, Object obj) {
		setIntoConfig(path.getPath(), obj);
	}

	public void setIntoConfig(String path, Object obj) {
		config.set(path, obj);
		save(configFile, config);
	}

	public FileConfiguration getConfig() {
		return config;
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
		CATEGORIES_LISTS("categories.lists");

		private String path;

		ConfigPath(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

	}
}
