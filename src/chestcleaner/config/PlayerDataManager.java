package chestcleaner.config;

import chestcleaner.sorting.SortingPattern;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A singleton class to organize the player data.
 * 
 * @author Tom2208
 *
 */
public class PlayerDataManager {

	private PlayerDataManager() {}

	/**
	 * Loads the data of player form the config.
	 * 
	 * @param p the player whose data you want to load.
	 */
	public static void loadPlayerData(Player p) {
		// load player categories
	}

	public static void removePlayerDataFormMemory(Player p) {
		// remove player categories
	}

	public static void setSortingPattern(Player p, SortingPattern pattern) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.PATTERN, pattern.name());
	}

	public static SortingPattern getSortingPattern(Player p) {
		SortingPattern pattern = SortingPattern.getSortingPatternByName(
				PluginConfig.getPlayerData().getString(PluginConfig.PlayerDataPath.PATTERN.getPath(p)));

		return pattern != null ? pattern : PluginConfigManager.getDefaultPattern();
	}

	public static void setAutoSort(Player p, boolean b) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.AUTOSORT, b);
	}

	public static boolean isAutoSort(Player p) {
		return PluginConfig.getPlayerData().contains(PluginConfig.PlayerDataPath.AUTOSORT.getPath(p))
				? PluginConfig.getPlayerData().getBoolean(PluginConfig.PlayerDataPath.AUTOSORT.getPath(p))
				: PluginConfigManager.isDefaultAutoSort();
	}

	public static List<String> getCategoryOrder(Player p) {
		List<String> list = PluginConfig.getConfig().getStringList(PluginConfig.ConfigPath.CATEGORIES_ORDER.getPath());
		return !list.isEmpty() ? list : PluginConfigManager.getCategoryOrder();
	}

	public static void setCategoryOrder(Player p, List<String> categorizationOrder) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.CATEGORIES_ORDER, categorizationOrder);
	}
}
