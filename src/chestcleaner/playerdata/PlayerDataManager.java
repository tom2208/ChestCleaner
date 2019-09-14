package chestcleaner.playerdata;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;

public class PlayerDataManager {
	
	private static EvaluatorType defaultEvaluator = EvaluatorType.BACK_BEGIN_STRING;
	private static SortingPattern defaultSortingPattern = SortingPattern.LEFT_TO_RIGHT_TOP_TO_BOTTOM;
	
	private static HashMap<UUID, EvaluatorType> playerEvaluator = new HashMap<>();
	private static HashMap<UUID, SortingPattern> playerPattern = new HashMap<>();
	
	public static void loadPlayerData(Player p){
		SortingPattern pattern = PlayerData.getSortingPattern(p);
		EvaluatorType evaluator = PlayerData.getEvaluatorType(p);
		
		if(pattern != null){
			playerPattern.put(p.getUniqueId(), pattern);
		}
		
		if(evaluator != null){
			playerEvaluator.put(p.getUniqueId(), evaluator);
		}
	}
	
	public static void removePlayerDataFormMemory(Player p){
		playerEvaluator.remove(p);
		playerPattern.remove(p);
	}
	
	public static EvaluatorType getEvaluatorTypOfPlayer(Player p){
		return playerEvaluator.get(p) == null ? playerEvaluator.get(p.getUniqueId()) : defaultEvaluator;
	}
	
	public static SortingPattern getSortingPatternOfPlayer(Player p){
		return playerPattern.get(p) == null ? playerPattern.get(p.getUniqueId()) : defaultSortingPattern;
	}
	
}
