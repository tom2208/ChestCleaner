package chestcleaner.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * This class includes all methods to save and read game data (variables for
 * this plugin).
 * 
 * @author tom2208
 */
public class Config {

	public static File ConfigFile = new File("plugins/ChestCleaner", "config.yml");
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

	public static void setCleanInvPermission(boolean b) {
		Config.set("cleanInventorypermissionactive", b);
		save();
	}

	public static boolean getCleanInvPermission() {
		return Config.getBoolean("cleanInventorypermissionactive");
	}

	public static boolean containsCleanInvPermission() {
		return Config.contains("cleanInventorypermissionactive");
	}

	public static void setTimerPermission(boolean b) {
		Config.set("timer.active", b);
		save();
	}

	public static boolean getTimerPermission() {
		return Config.getBoolean("timer.active");
	}

	public static boolean containsTimerPermission() {
		return Config.contains("timer.active");
	}

	public static void setTime(int t) {
		Config.set("timer.time", t);
		save();
	}

	public static int getTime() {
		return Config.getInt("timer.time");
	}

	public static boolean containsTime() {
		return Config.contains("timer.time");
	}

	public static void setStrings(List<String> list) {
		Config.set("Strings", list);
		save();
	}

	public static List<String> getStrings() {
		return Config.getStringList("Strings");
	}

	public static boolean containsStrings() {
		return Config.contains("Strings");
	}

	public static void setItem(ItemStack is) {
		Config.set("cleaningItem", is);
		save();
	}

	public static ItemStack getItem() {
		return Config.getItemStack("cleaningItem");
	}

	public static void setItemBoolean(boolean b) {
		Config.set("active", b);
		save();
	}

	public static boolean getItemBoolean() {
		return Config.getBoolean("active");
	}

	public static boolean containsItemBoolean() {
		if (Config.contains("active"))
			return true;
		return false;
	}

	public static void setDurabilityBoolean(boolean b) {
		Config.set("durability", b);
		save();
	}

	public static boolean getDurabilityBoolean() {
		return Config.getBoolean("durability");
	}

	public static boolean containsDurabilityBoolean() {
		if (Config.contains("openinventoryeventmode"))
			return true;
		return false;
	}

	public static void setMode(boolean b) {
		Config.set("openinventoryeventmode", b);
		save();
	}

	public static boolean getMode() {
		return Config.getBoolean("openinventoryeventmode");
	}

	public static boolean containsMode() {
		if (Config.contains("openinventoryeventmode"))
			return true;
		return false;
	}

	public static void setConsumablesRefill(boolean b) {
		Config.set("consumablesrefill", b);
		save();
	}

	public static boolean getConsumablesRefill() {
		return Config.getBoolean("consumablesrefill");
	}

	public static boolean containsConsumablesRefill() {
		if (Config.contains("consumablesrefill"))
			return true;
		return false;
	}

	public static void setBlockRefill(boolean b) {
		Config.set("blockrefill", b);
		save();
	}

	public static boolean getBlockRefill() {
		return Config.getBoolean("blockrefill");
	}

	public static boolean containsBlockRefill() {
		if (Config.contains("blockrefill"))
			return true;
		return false;
	}

}
