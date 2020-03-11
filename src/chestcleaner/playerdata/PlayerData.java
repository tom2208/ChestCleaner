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
	 * Saves this {@code FileConfiguration} to the the chestcleaner folder. If the
	 * file does not exist, it will be created. If already exists, it will be
	 * overwritten.
	 * 
	 * This method will save using the system default encoding, or possibly using
	 * UTF8.
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

	/* EVALUATORTYP */
	public static void setEvaluatorTyp(EvaluatorType pattern, Player p) {
		Config.set(p.getUniqueId() + ".evaluatortyp", pattern.name());
		save();
	}

	public static EvaluatorType getEvaluatorType(Player p) {
		return EvaluatorType.getEvaluatorTypByName(Config.getString(p.getUniqueId() + ".evaluatortyp"));
	}

	/* AUTOSORT */
	public static void setAutoSort(boolean b, Player p) {
		Config.set(p.getUniqueId() + ".autosort", b);
		save();
	}

	public static boolean containsAutoSort(Player p) {
		return Config.contains(p.getUniqueId() + ".autosort");
	}

	public static boolean getAutoSort(Player p) {
		return Config.getBoolean(p.getUniqueId() + ".autosort");
	}

}
