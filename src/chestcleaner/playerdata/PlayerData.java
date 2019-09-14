package chestcleaner.playerdata;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;

public class PlayerData {

	public static File ConfigFile = new File("plugins/ChestCleaner", "playerdata.yml");
	public static FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);

	/**
	 * Saves this {@code FileConfiguration} to the the chestcleaner folder. If
	 * the file does not exist, it will be created. If already exists, it will
	 * be overwritten.
	 * 
	 * This method will save using the system default encoding, or possibly
	 * using UTF8.
	 *
	 */
	public static void save() {

		try {
			Config.save(ConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/* SORTINGPATTERN */
	public static void setSortingPattern(SortingPattern pattern, Player p) {
		Config.set(p.getUniqueId() + ".sortingpattern", pattern.name());
		save();
	}

	public static SortingPattern getSortingPattern(Player p) {
		return SortingPattern.getSortingPatternByName(Config.getString(p.getUniqueId() + ".sortingpattern"));
	}

	public static boolean containsSortingPattern(Player p) {
		return Config.contains(p.getUniqueId() + ".sortingpattern");
	}
	
	/* EVALUATORTYP */
	public static void setEvaluatorTyp(EvaluatorType pattern, Player p) {
		Config.set(p.getUniqueId() + ".evaluatortyp", pattern.name());
		save();
	}

	public static EvaluatorType getEvaluatorType(Player p) {
		return EvaluatorType.getEvaluatorTypByName(Config.getString(p.getUniqueId() + ".evaluatortyp"));
	}

	public static boolean containsEvaluatorTyp(Player p) {
		return Config.contains(p.getUniqueId() + ".evaluatortyp");
	}
	
}
