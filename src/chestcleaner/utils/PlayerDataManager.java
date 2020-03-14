package chestcleaner.utils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import chestcleaner.config.PluginConfig;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;

/**
 * A singleton class to organize the player data.
 * 
 * @author Tom2208
 *
 */
public class PlayerDataManager {

	private static PlayerDataManager instance = null;

	private HashMap<UUID, EvaluatorType> playerEvaluator;
	private HashMap<UUID, SortingPattern> playerPattern;
	private HashMap<UUID, Boolean> playerAutoSort;
	private boolean defaultAutoSort = false;

	protected PlayerDataManager() {
		playerEvaluator = new HashMap<>();
		playerPattern = new HashMap<>();
		playerAutoSort = new HashMap<>();
	}

	/**
	 * Loads the data of player form the config.
	 * 
	 * @param p the player whose data you want to load.
	 */
	public void loadPlayerData(Player p) {
		SortingPattern pattern = PluginConfig.getInstance().getSortingPattern(p);
		EvaluatorType evaluator = PluginConfig.getInstance().getEvaluatorType(p);
		boolean autosort = PluginConfig.getInstance().getAutoSort(p);

		if (pattern != null) {
			playerPattern.put(p.getUniqueId(), pattern);
		}

		if (evaluator != null) {
			playerEvaluator.put(p.getUniqueId(), evaluator);
		}

		if (PluginConfig.getInstance().containsAutoSort(p)) {
			playerAutoSort.put(p.getUniqueId(), autosort);
		}

	}

	public void removePlayerDataFormMemory(Player p) {
		playerEvaluator.remove(p.getUniqueId());
		playerPattern.remove(p.getUniqueId());
		playerAutoSort.remove(p.getUniqueId());
	}

	public EvaluatorType getEvaluatorTypOfPlayer(Player p) {
		return playerEvaluator.get(p.getUniqueId()) == null ? EvaluatorType.DEFAULT
				: playerEvaluator.get(p.getUniqueId());
	}

	public SortingPattern getSortingPatternOfPlayer(Player p) {
		return playerPattern.get(p.getUniqueId()) == null ? SortingPattern.DEFAULT : playerPattern.get(p.getUniqueId());
	}

	public boolean getAutoSortOfPlayer(Player p) {
		return playerAutoSort.containsKey(p.getUniqueId()) ? playerAutoSort.get(p.getUniqueId()) : isDefaultAutoSort();
	}

	/**
	 * Returns the instance of this singleton if it's null it creates one.
	 * 
	 * @return The Instance of the singleton.
	 */
	public static PlayerDataManager getInstance() {
		if (instance == null) {
			instance = new PlayerDataManager();
		}
		return instance;
	}

	public boolean isDefaultAutoSort() {
		return defaultAutoSort;
	}

	public void setDefaultAutoSort(boolean defaultAutoSort) {
		this.defaultAutoSort = defaultAutoSort;
	}

}
