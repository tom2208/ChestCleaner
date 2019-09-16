package chestcleaner.playerdata;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;

public class PlayerDataManager {

	public static boolean defaultAutoSort = false;
	
	private static HashMap<UUID, EvaluatorType> playerEvaluator = new HashMap<>();
	private static HashMap<UUID, SortingPattern> playerPattern = new HashMap<>();
	private static HashMap<UUID, Boolean> playerAutoSort = new HashMap<>();
	
	public static void loadPlayerData(Player p){
		SortingPattern pattern = PlayerData.getSortingPattern(p);
		EvaluatorType evaluator = PlayerData.getEvaluatorType(p);
		boolean autosort = PlayerData.getAutoSort(p);
		
		if(pattern != null){
			playerPattern.put(p.getUniqueId(), pattern);
		}
		
		if(evaluator != null){
			playerEvaluator.put(p.getUniqueId(), evaluator);
		}
		
		if(!PlayerData.containsAutoSort(p)){
			autosort = defaultAutoSort;
		}
		
		playerAutoSort.put(p.getUniqueId(), autosort);
		
	}
	
	public static void removePlayerDataFormMemory(Player p){
		playerEvaluator.remove(p);
		playerPattern.remove(p);
		playerAutoSort.remove(p);
	}
	
	public static EvaluatorType getEvaluatorTypOfPlayer(Player p){
		return playerEvaluator.get(p) == null ? playerEvaluator.get(p.getUniqueId()) : EvaluatorType.DEFAULT;
	}
	
	public static SortingPattern getSortingPatternOfPlayer(Player p){
		return playerPattern.get(p) == null ? playerPattern.get(p.getUniqueId()) : SortingPattern.DEFAULT;
	}
	
	public static boolean getAutoSortOfPlayer(Player p){
		return playerAutoSort.containsKey(p.getUniqueId()) ? playerAutoSort.get(p.getUniqueId()) : defaultAutoSort;
	}
	
}
