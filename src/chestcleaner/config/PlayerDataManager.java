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
	
	public static void setSortingPattern(Player p, SortingPattern pattern) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.PATTERN, pattern.name());
	}

	public static SortingPattern getSortingPattern(Player p) {
		SortingPattern pattern = SortingPattern.getSortingPatternByName(
				PluginConfig.getPlayerData().getString(PluginConfig.PlayerDataPath.PATTERN.getPath(p)));

		return pattern != null ? pattern : PluginConfigManager.getDefaultPattern();
	}

	public static boolean containsNotification(Player p) {
		return PluginConfig.getPlayerData().contains(p.getUniqueId() + "." + PluginConfig.PlayerDataPath.NOTIFICATION);
	}
	
	public static void setNotification(Player p, boolean b) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.NOTIFICATION, b);
	}
	
	public static boolean isNotification(Player p) {
		if(PluginConfig.getPlayerData().contains(PluginConfig.PlayerDataPath.NOTIFICATION.getPath(p))) {
			return PluginConfig.getPlayerData().getBoolean(PluginConfig.PlayerDataPath.NOTIFICATION.getPath(p));
		}else {
			return PluginConfigManager.getDefaultChatNotificationBoolean();
		}
	}
	
	public static boolean containsSortingSound(Player p) {
		return PluginConfig.getPlayerData().contains(p.getUniqueId() + "." + PluginConfig.PlayerDataPath.SOUND);
	}
	
	public static void setSortingSound(Player p, boolean b) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.SOUND, b);
	}
	
	public static boolean isSortingSound(Player p) {
		if(PluginConfig.getPlayerData().contains(PluginConfig.PlayerDataPath.SOUND.getPath(p))) {
			return PluginConfig.getPlayerData().getBoolean(PluginConfig.PlayerDataPath.SOUND.getPath(p));
		}else {
			return PluginConfigManager.getDefaultSortingSoundBoolean();
		}
	}
	
	public static void setAutoSort(Player p, boolean b) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.AUTOSORT, b);
	}

	public static boolean isAutoSort(Player p) {
		if(PluginConfig.getPlayerData().contains(PluginConfig.PlayerDataPath.AUTOSORT.getPath(p))) {
			return PluginConfig.getPlayerData().getBoolean(PluginConfig.PlayerDataPath.AUTOSORT.getPath(p));
		}else {
			return PluginConfigManager.getDefaultAutoSortBoolean();
		}
	}

	public static List<String> getCategoryOrder(Player p) {
		List<String> list = PluginConfig.getPlayerData().getStringList(PluginConfig.ConfigPath.DEFAULT_CATEGORIES.getPath());
		return !list.isEmpty() ? list : PluginConfigManager.getCategoryOrder();
	}

	public static void setCategoryOrder(Player p, List<String> categorizationOrder) {
		PluginConfig.setIntoPlayerData(p, PluginConfig.PlayerDataPath.CATEGORIES_ORDER, categorizationOrder);
	}
}