package chestcleaner.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.commands.CleanInvenotryCommand;
import chestcleaner.commands.CleaningItemCommand;
import chestcleaner.commands.SortingConfigCommand;
import chestcleaner.commands.TimerCommand;
import chestcleaner.listeners.SortingListener;
import chestcleaner.playerdata.PlayerDataManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;
import chestcleaner.listeners.DataLoadingListener;
import chestcleaner.listeners.RefillListener;
import chestcleaner.timer.Counter;
import chestcleaner.utils.messages.StringTable;

public class ChestCleaner extends JavaPlugin {

	public static boolean cleanInvPermission = true;
	public static boolean timer = true;
	public static int time = 5;
	public static ItemStack item = new ItemStack(Material.IRON_HOE);
	public static boolean durability = true;
	public static boolean itemBoolean = true;
	public static boolean eventmode = false;
	public static boolean blockRefill = true;
	public static boolean consumablesRefill = true;

	public static ChestCleaner main;

	private Counter c = new Counter();
	private File configFile = new File("plugins/" + this.getName(), "config.yml");
	private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

	@Override
	public void onEnable() {
		main = this;
		loadConfig();
		getCommand("cleaninventory").setExecutor(new CleanInvenotryCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("cleaningitem").setExecutor(new CleaningItemCommand());
		getCommand("blacklist").setExecutor(new BlacklistCommand());
		getCommand("sortingconfig").setExecutor(new SortingConfigCommand());
		Bukkit.getPluginManager().registerEvents(new SortingListener(), this);
		Bukkit.getPluginManager().registerEvents(new RefillListener(), this);
		Bukkit.getPluginManager().registerEvents(new DataLoadingListener(), this);
		c.start();
		new UpdateChecker(this).checkForUpdate();
	}

	/**
	 * Loads all variables out of the config, if the config does not exist it will
	 * generate one with the default values for the variables.
	 */
	private void loadConfig() {

		save();

		if (setIfDoesntContains(ConfigPath.DEFAULT_AUTOSORT, PlayerDataManager.defaultAutoSort)) {
			PlayerDataManager.defaultAutoSort = config.getBoolean(ConfigPath.DEFAULT_AUTOSORT.getPath());
		}

		if (setIfDoesntContains(ConfigPath.DEFAULT_EVALUATOR, EvaluatorType.DEFAULT.name())) {
			EvaluatorType.DEFAULT = EvaluatorType
					.getEvaluatorTypByName(config.getString(ConfigPath.DEFAULT_EVALUATOR.getPath()));
		}

		if (setIfDoesntContains(ConfigPath.DEFAULT_SORTING_PATTERN, SortingPattern.DEFAULT.name())) {
			SortingPattern.DEFAULT = SortingPattern
					.getSortingPatternByName(config.getString(ConfigPath.DEFAULT_SORTING_PATTERN.getPath()));
		}

		if (config.contains(ConfigPath.STRINGS.getPath())) {
			StringTable.setUpList(config.getStringList(ConfigPath.STRINGS.getPath()));
		} else {
			StringTable.setUpList(null);
		}

		if (setIfDoesntContains(ConfigPath.CLEANING_ITEM, new ItemStack(Material.IRON_HOE))) {
			item = config.getItemStack(ConfigPath.CLEANING_ITEM.getPath());
		}

		if (setIfDoesntContains(ConfigPath.CLEANING_ITEM_ACTIVE, true)) {
			itemBoolean = config.getBoolean(ConfigPath.CLEANING_ITEM_ACTIVE.getPath());
		}

		if (setIfDoesntContains(ConfigPath.CLEANING_ITEM_DURABILITY, true)) {
			durability = config.getBoolean(ConfigPath.CLEANING_ITEM_DURABILITY.getPath());
		}

		if (setIfDoesntContains(ConfigPath.OPEN_INVENTORY_MODE, false)) {
			eventmode = config.getBoolean(ConfigPath.OPEN_INVENTORY_MODE.getPath());
		}

		if (setIfDoesntContains(ConfigPath.CONSUMABLES_REFILL, true)) {
			consumablesRefill = config.getBoolean(ConfigPath.CONSUMABLES_REFILL.getPath());
		}

		if (setIfDoesntContains(ConfigPath.BLOCK_REFILL, true)) {
			blockRefill = config.getBoolean(ConfigPath.BLOCK_REFILL.getPath());
		}

		if (setIfDoesntContains(ConfigPath.INVENTORY_PERMISSION_ACTIVE, true)) {
			cleanInvPermission = config.getBoolean(ConfigPath.INVENTORY_PERMISSION_ACTIVE.getPath());
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
		save();
	}

	/**
	 * Saves this {@code FileConfiguration} to the the ChestCleaner folder. If the
	 * file does not exist, it will be created. If it already exists, it will be
	 * overwritten.
	 * 
	 * This method will save using the system default encoding, or possibly using
	 * UTF8.
	 *
	 */
	private void save() {

		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		if (!config.contains("path")) {
			config.set(path.getPath(), value);
			return true;
		}
		return false;
	}

	public void setIntoConfig(ConfigPath path, Object obj) {
		config.set(path.getPath(), obj);
		save();
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public enum ConfigPath {
		DEFAULT_AUTOSORT("defaultautosort"), DEFAULT_EVALUATOR("defaultevaluator"),
		DEFAULT_SORTING_PATTERN("defaultsortingpattern"), STRINGS("Strings"), CLEANING_ITEM("cleaningItem"),
		CLEANING_ITEM_ACTIVE("active"), CLEANING_ITEM_DURABILITY("durability"),
		OPEN_INVENTORY_MODE("openinventoryeventmode"), CONSUMABLES_REFILL("consumablesrefill"),
		BLOCK_REFILL("blockrefill"), INVENTORY_PERMISSION_ACTIVE("cleanInventorypermissionactive"),
		BLACKLIST("blacklist"), INVENTORY_BLACKLIST("inventoryblacklist"), TIMER_TIME("timer.time"),
		TIMER_ACTIVE("timer.active");

		private String path;

		ConfigPath(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

	}

}
