package chestcleaner.config;

import chestcleaner.config.serializable.Category;
import chestcleaner.config.serializable.ListCategory;
import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.config.serializable.WordCategory;
import chestcleaner.cooldown.CMRegistry;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.categorizer.Categorizer;
import chestcleaner.sorting.categorizer.ListCategoryCategorizer;
import chestcleaner.sorting.categorizer.MasterCategorizer;
import chestcleaner.sorting.categorizer.PredicateCategorizer;

import chestcleaner.utils.SortingAdminUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PluginConfigManager {

	private static List<Material> blacklistStacking = null;
	private static List<Material> blacklistInventory = null;
	private static List<Material> blacklistAutorefill = null;
	
	private PluginConfigManager() {}
	
	public static boolean isDefaultClickSort(){
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.DEFAULT_CLICKSORT.getPath());
	}
	
	public static void setDefaultClickSort(boolean b) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_CLICKSORT, b);
	}
	
	public static boolean isDefaultBreakableRefill() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.DEFAULT_BREAKABLE_ITEMS_REFILL.getPath());
	}

	public static void setDefaultBreakableRefill(boolean breakableRefill) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_BREAKABLE_ITEMS_REFILL, breakableRefill);
	}

	public static void setDefaultSortingSound(Sound sound){
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND, sound.name());
	}

	public static Sound getDefaultSortingSound(){
		String soundName = PluginConfig.getConfig().getString(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND.getPath());
		return SortingAdminUtils.getSoundByName(soundName);
	}

	public static void setDefaultVolume(float volume){
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND_VOLUME, String.valueOf(volume));
	}

	public static float getDefaultVolume(){
		return Float.parseFloat(Objects.requireNonNull(PluginConfig.getConfig().getString(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND_VOLUME.getPath())));
	}

	public static void setDefaultPitch(float pitch){
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND_PITCH, String.valueOf(pitch));
	}

	public static float getDefaultPitch(){
		return Float.parseFloat(Objects.requireNonNull(PluginConfig.getConfig().getString(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND_PITCH.getPath())));
	}

	public static void setDefaultSortingSoundBoolean(boolean bool) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND_BOOLEAN, bool);
	}
	
	public static boolean getDefaultSortingSoundBoolean() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.DEFAULT_SORTING_SOUND_BOOLEAN.getPath());
	}
	
	public static void setDefaultChatNotificationBoolean(boolean bool) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CHAT_NOTIFICATION_BOOLEAN, bool);
	}
	
	public static boolean getDefaultChatNotificationBoolean() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.CHAT_NOTIFICATION_BOOLEAN.getPath());
	}
	
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

	public static boolean isOpenEvent() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.CLEANING_ITEM_OPEN_EVENT.getPath());
	}

	public static void setOpenEvent(boolean openEvent) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CLEANING_ITEM_OPEN_EVENT, openEvent);
	}

	public static boolean isDefaultBlockRefill() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.REFILL_BLOCKS.getPath());
	}

	public static void setDefaultBlockRefill(boolean blockRefillActive) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.REFILL_BLOCKS, blockRefillActive);
	}

	public static boolean isDefaultConsumablesRefill() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.REFILL_CONSUMABLES.getPath());
	}

	public static void setDefaultConsumablesRefill(boolean consumablesRefillActive) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.REFILL_CONSUMABLES, consumablesRefillActive);
	}

	public static boolean isUpdateCheckerActive() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.UPDATE_CHECKER_ACTIVE.getPath());
	}

	public static boolean isCooldownActive(CMRegistry.CMIdentifier id) {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.COOLDOWN_ACTIVE.getPath().concat(".").concat(id.toString()));
	}

	public static void setCooldownActive(boolean active, CMRegistry.CMIdentifier id) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.COOLDOWN_ACTIVE.getPath().concat(".").concat(id.toString()), active);
	}

	public static int getCooldown(CMRegistry.CMIdentifier id) {
		return PluginConfig.getConfig().getInt(PluginConfig.ConfigPath.COOLDOWN_TIME.getPath().concat(".").concat(id.toString()));
	}

	public static void setCooldown(int time, CMRegistry.CMIdentifier id) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.COOLDOWN_TIME.getPath().concat(".").concat(id.toString()), time);
	}

	public static List<String> getCategoryOrder() {
		return PluginConfig.getConfig().getStringList(PluginConfig.ConfigPath.DEFAULT_CATEGORIES.getPath());
	}

	public static void setCategoryOrder(List<String> categorizationOrder) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_CATEGORIES, categorizationOrder);
	}

	public static Category<?> getCategoryByName(String name) {
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

	public static List<Category<?>> getAllCategories() {
		List<Category<?>> list = new ArrayList<>();
		list.addAll(getWordCategories());
		list.addAll(getListCategories());
		list.addAll(getMasterCategories());
		return list;
	}

	public static List<WordCategory> getWordCategories() {
		return getCastList(PluginConfig.getConfig().getList(
				PluginConfig.ConfigPath.CATEGORIES_WORDS.getPath(), new ArrayList<WordCategory>()));
	}

	public static List<ListCategory> getListCategories() {
		return getCastList(PluginConfig.getConfig().getList(
				PluginConfig.ConfigPath.CATEGORIES_LISTS.getPath(), new ArrayList<ListCategory>()));
	}

	public static List<MasterCategory> getMasterCategories() {
		return getCastList(PluginConfig.getConfig().getList(
				PluginConfig.ConfigPath.CATEGORIES_MASTER.getPath(), new ArrayList<MasterCategory>()));
	}

	public static void addWordCategory(WordCategory category) {
		List<WordCategory> categories = addOrUpdateCategory(category, getWordCategories());
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CATEGORIES_WORDS, categories);
	}

	public static void addListCategory(ListCategory category) {
		List<ListCategory> categories = addOrUpdateCategory(category, getListCategories());
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CATEGORIES_LISTS, categories);
	}


	public static void addMasterCategory(MasterCategory category) {
		List<MasterCategory> categories = addOrUpdateCategory(category, getMasterCategories());
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.CATEGORIES_MASTER, categories);
	}

	public static boolean removeCategory(Categorizer categorizer) {
		
		String path = PluginConfig.ConfigPath.CATEGORIES_WORDS.getPath();
		String categoryName = categorizer.getName();
		if(categorizer instanceof PredicateCategorizer){	
			path = PluginConfig.ConfigPath.CATEGORIES_WORDS.getPath();	
		}else if(categorizer instanceof ListCategoryCategorizer) {
			path = PluginConfig.ConfigPath.CATEGORIES_LISTS.getPath();
		}else if(categorizer instanceof MasterCategorizer) {
			path = PluginConfig.ConfigPath.CATEGORIES_MASTER.getPath();
		}
		
		boolean removed = false;
		List<Category<?>> list = (List<Category<?>>) PluginConfig.getConfig().getList(path);

		assert list != null;

		for(Category<?> cat : list) {
			if(cat.getName().equalsIgnoreCase(categoryName)) {
				list.remove(cat);
				removed = true;
				break;
			}
		}
		
		PluginConfig.setIntoConfig(path, list);
		return removed;
	}
	
	private static <T extends Category> List<T> addOrUpdateCategory(T category, List<T> categories) {
		T existingCategory = categories.stream()
				.filter(cat -> cat.getName().equalsIgnoreCase(category.getName()))
				.findFirst().orElse(null);
		if (existingCategory != null) {
			existingCategory.setValue(category.getValue());
		} else {
			categories.add(category);
		}
		return categories;
	}

	public static void setDefaultPattern(SortingPattern pattern) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_PATTERN, pattern.toString());
	}

	public static SortingPattern getDefaultPattern() {
		return SortingPattern.getSortingPatternByName(PluginConfig.getConfig()
				.getString(PluginConfig.ConfigPath.DEFAULT_PATTERN.getPath()));
	}

	public static boolean getDefaultAutoSortBoolean() {
		return PluginConfig.getConfig().getBoolean(PluginConfig.ConfigPath.DEFAULT_AUTOSORT.getPath());
	}

	public static void setDefaultAutoSort(boolean defaultAutoSort) {
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.DEFAULT_AUTOSORT, defaultAutoSort);
	}
	
	public static List<Material> getBlacklistInventory() {
		if (blacklistInventory == null) {
			blacklistInventory = getMaterialList(PluginConfig.getConfig(), PluginConfig.ConfigPath.BLACKLIST_INVENTORY);
		}
		return blacklistInventory;
	}

	public static void setBlacklistInventory(List<Material> blacklistInventory) {
	    PluginConfigManager.blacklistInventory = blacklistInventory;
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.BLACKLIST_INVENTORY, getStringList(blacklistInventory));
	}

	public static List<Material> getBlacklistStacking() {
	    if (blacklistStacking == null) {
			blacklistStacking = getMaterialList(PluginConfig.getConfig(), PluginConfig.ConfigPath.BLACKLIST_STACKING);
		}
		return blacklistStacking;
	}

	public static void setBlacklistStacking(List<Material> blacklistStacking) {
	    PluginConfigManager.blacklistStacking = blacklistStacking;
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.BLACKLIST_STACKING, getStringList(blacklistStacking));
	}
	
	public static List<Material> getBlacklistAutoRefill() {
	    if (blacklistAutorefill == null) {
	    	blacklistAutorefill = getMaterialList(PluginConfig.getConfig(), PluginConfig.ConfigPath.BLACKLIST_AUTOREFILL);
		}
		return blacklistAutorefill;
	}

	public static void setBlacklistAutoRefill(List<Material> blacklistAutorefill) {
	    PluginConfigManager.blacklistAutorefill = blacklistAutorefill;
		PluginConfig.setIntoConfig(PluginConfig.ConfigPath.BLACKLIST_AUTOREFILL, getStringList(blacklistAutorefill));
	}
	
	private static ArrayList<Material> getMaterialList(FileConfiguration config, PluginConfig.ConfigPath path) {
		List<String> list = config.getStringList(path.getPath());
		ArrayList<Material> materials = new ArrayList<>();

		for (String name : list) {
			materials.add(Material.getMaterial(name.toUpperCase()));
		}
		return materials;
	}

	private static List<String> getStringList(List<Material> materialList) {
		List<String> list = new ArrayList<>();

		for (Material material : materialList) {
			list.add(material.name().toLowerCase());
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
