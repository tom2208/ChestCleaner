package chestcleaner.config;

import chestcleaner.config.serializable.Category;
import chestcleaner.config.serializable.ListCategory;
import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.config.serializable.WordCategory;
import chestcleaner.sorting.SortingPattern;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginConfigManager {

	private static List<Material> blacklistSorting = null;
	private static List<Material> blacklistInventory = null;

	private PluginConfigManager() {}

	public static boolean isDurabilityLossActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.CLEANING_ITEM_DURABILITY.getPath());
	}

	public static void setDurabilityLossActive(boolean durabilityLossActive) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CLEANING_ITEM_DURABILITY, durabilityLossActive);
	}

	public static boolean isCleaningItemActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.CLEANING_ITEM_ACTIVE.getPath());
	}

	public static void setCleaningItemActive(boolean cleaningItemActive) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CLEANING_ITEM_ACTIVE, cleaningItemActive);
	}

	public static ItemStack getCleaningItem() {
		return PluginConfig.getConfig().getItemStack(PluginConfig.ConfigPath.CLEANING_ITEM.getPath());
	}

	public static void setCleaningItem(ItemStack item) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CLEANING_ITEM, item);
	}

	public static boolean isEventModeActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.OPEN_INVENTORY_MODE.getPath());
	}

	public static void setEventModeActive(boolean eventModeActive) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.OPEN_INVENTORY_MODE, eventModeActive);
	}

	public static boolean isBlockRefillActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.BLOCK_REFILL.getPath());
	}

	public static void setBlockRefillActive(boolean blockRefillActive) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.BLOCK_REFILL, blockRefillActive);
	}

	public static boolean isConsumablesRefillActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.CONSUMABLES_REFILL.getPath());
	}

	public static void setConsumablesRefillActive(boolean consumablesRefillActive) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CONSUMABLES_REFILL, consumablesRefillActive);
	}

	public static boolean isUpdateCheckerActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.UPDATE_CHECKER_ACTIVE.getPath());
	}

	public static void setUpdateCheckerActive(boolean updateCheckerActive) {
	 	PluginConfig.setIntoConfig(PluginConfig.ConfigPath.UPDATE_CHECKER_ACTIVE, updateCheckerActive);
	}

	public static boolean isCleanInvPermission() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.INVENTORY_PERMISSION_ACTIVE.getPath());
	}

	public static void setCleanInvPermission(boolean cleanInvPermission) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.INVENTORY_PERMISSION_ACTIVE, cleanInvPermission);
	}

	public static boolean isCooldownActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.COOLDOWN_ACTIVE.getPath());
	}

	public static void setCooldownActive(boolean active) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.COOLDOWN_ACTIVE, active);
	}

	public static int getCooldown() {
		return PluginConfig.getConfig().getInt(PluginConfig.ConfigPath.COOLDOWN_TIME.getPath());
	}

	public static void setCooldown(int time) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.COOLDOWN_TIME, time * 1000);
	}

	public static List<String> getCategoryOrder() {
		return PluginConfig.getConfig().getStringList(PluginConfig.ConfigPath.CATEGORIES_ORDER.getPath());
	}

	public static void setCategoryOrder(List<String> categorizationOrder) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CATEGORIES_ORDER, categorizationOrder);
	}

	public static Category getCategoryByName(String name) {
		for (WordCategory category : getWordCategories())
			if (category.getName().equalsIgnoreCase(name))
				return category;
		for (ListCategory category : getListCategories())
			if (category.getName().equalsIgnoreCase(name))
				return category;
		for (MasterCategory category : getMasterCategories())
			if (category.getName().equalsIgnoreCase(name))
				return category;
		return null;
	}

	public static List<Category> getAllCategories() {
		List<Category> list = new ArrayList<>();
		list.addAll(getWordCategories());
		list.addAll(getListCategories());
		list.addAll(getMasterCategories());
		return list;
	}

	public static List<WordCategory> getWordCategories() {
		return getCastList(PluginConfig.getConfig().getList(
				PluginConfig.ConfigPath.CATEGORIES_WORDS.getPath(), new ArrayList<WordCategory>()));
	}

	public static void addWordCategory(WordCategory category) {
		List<WordCategory> categories = getWordCategories();
		categories.add(category);
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CATEGORIES_WORDS, categories);
	}

	public static List<ListCategory> getListCategories() {
		return getCastList(PluginConfig.getConfig().getList(
				PluginConfig.ConfigPath.CATEGORIES_LISTS.getPath(), new ArrayList<ListCategory>()));
	}

	public static void addListCategory(ListCategory category) {
		List<ListCategory> categories = getListCategories();
		categories.add(category);
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CATEGORIES_LISTS, categories);
	}

	public static List<MasterCategory> getMasterCategories() {
		return getCastList(PluginConfig.getConfig().getList(
				PluginConfig.ConfigPath.CATEGORIES_MASTER.getPath(), new ArrayList<MasterCategory>()));
	}

	public static void addMasterCategory(MasterCategory category) {
		List<MasterCategory> categories = getMasterCategories();
		categories.add(category);
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CATEGORIES_MASTER, categories);
	}

	public static void setDefaultPattern(SortingPattern pattern) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_SORTING_PATTERN, pattern);
	}

	public static SortingPattern getDefaultPattern() {
		return SortingPattern.getSortingPatternByName(PluginConfig.getConfig()
				.getString(PluginConfig.ConfigPath.DEFAULT_SORTING_PATTERN.getPath()));
	}

	public static boolean isDefaultAutoSort() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.DEFAULT_AUTOSORT.getPath());
	}

	public static void setDefaultAutoSort(boolean defaultAutoSort) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_AUTOSORT, defaultAutoSort);
	}

	public static List<Material> getBlacklistInventory() {
		if (blacklistInventory == null) {
			blacklistInventory = getMaterialList(PluginConfig.getConfig(), PluginConfig.ConfigPath.INVENTORY_BLACKLIST);
		}
		return blacklistInventory;
	}

	public static void setBlacklistInventory(List<Material> blacklistInventory) {
	    PluginConfigManager.blacklistInventory = blacklistInventory;
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.INVENTORY_BLACKLIST, getStringList(blacklistInventory));
	}

	public static List<Material> getBlacklistSorting() {
	    if (blacklistSorting == null) {
			blacklistSorting = getMaterialList(PluginConfig.getConfig(), PluginConfig.ConfigPath.BLACKLIST);
		}
		return blacklistSorting;
	}

	public static void setBlacklistSorting(List<Material> blacklistSorting) {
	    PluginConfigManager.blacklistSorting = blacklistSorting;
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.INVENTORY_BLACKLIST, getStringList(blacklistSorting));
	}

	private static ArrayList<Material> getMaterialList(FileConfiguration config, PluginConfig.ConfigPath path) {
		List<String> list = config.getStringList(path.getPath());
		ArrayList<Material> materials = new ArrayList<>();

		for (String name : list) {
			materials.add(Material.getMaterial(name));
		}
		return materials;
	}

	private static List<String> getStringList(List<Material> materialList) {
		List<String> list = new ArrayList<>();

		for (Material material : materialList) {
			list.add(material.name());
		}
		return list;
	}

	private static <T> List<T> getCastList(List<?> input) {
		if (input == null) {
			return new ArrayList<>();
		}
		return input.stream().map(o -> (T) o).collect(Collectors.toList());
	}
}
