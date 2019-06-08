package chestcleaner.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import chestcleaner.commands.BlackListCommand;
import chestcleaner.commands.CleanInvenotryCommand;
import chestcleaner.commands.CleaningItemCommand;
import chestcleaner.commands.TimerCommand;
import chestcleaner.config.Config;
import chestcleaner.listeners.SortingListener;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.listeners.RefillListener;
import chestcleaner.timer.Counter;
import chestcleaner.utils.messages.StringTable;

public class Main extends JavaPlugin {

	public static boolean cleanInvPermission = true;
	public static boolean timer = true;
	public static int time = 5;
	public static ItemStack item = new ItemStack(Material.IRON_HOE);
	public static boolean durability = true;
	public static boolean itemBoolean = true;
	public static boolean eventmode = false;
	public static boolean blockRefill = true;
	public static boolean consumablesRefill = true;

	public static Main main;

	private Counter c = new Counter();

	@Override
	public void onEnable() {
		main = this;
		loadConfig();
		getCommand("cleaninventory").setExecutor(new CleanInvenotryCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("cleaningitem").setExecutor(new CleaningItemCommand());
		getCommand("blacklist").setExecutor(new BlackListCommand());
		Bukkit.getPluginManager().registerEvents(new SortingListener(), this);
		Bukkit.getPluginManager().registerEvents(new RefillListener(), this);
		c.start();
		new UpdateChecker(this).checkForUpdate();

	}

	/**
	 * Loads all variables out of the config, if the config does not exist it
	 * will generate one with the default values for the variables.
	 */
	private void loadConfig() {

		Config.save();

		if (Config.containsStrings()) {
			StringTable.setUpList(Config.getStrings());
		} else {
			StringTable.setUpList(null);
		}

		if (Config.getItem() == null) {
			item = new ItemStack(Material.IRON_HOE);
			Config.setItem(item);
		} else {
			item = Config.getItem();
		}

		if (Config.containsItemBoolean()) {
			itemBoolean = Config.getItemBoolean();
		} else {
			Config.setItemBoolean(true);
		}

		if (Config.containsDurabilityLossBoolean()) {
			durability = Config.getDurabilityLossBoolean();
		} else {
			Config.setDurabilityLossBoolean(true);
		}

		if (Config.containsMode()) {
			eventmode = Config.getMode();
		} else {
			Config.setMode(false);
		}

		if (Config.containsConsumablesRefill()) {
			consumablesRefill = Config.getConsumablesRefill();
		} else {
			Config.setConsumablesRefill(true);
		}

		if (Config.containsBlockRefill()) {
			blockRefill = Config.getBlockRefill();
		} else {
			Config.setBlockRefill(true);
			;
		}

		if (Config.containsCleanInvPermission()) {
			cleanInvPermission = Config.getCleanInvPermission();
		} else {
			Config.setCleanInvPermission(true);
		}
		
		if(Config.containsBlackList()){
			InventorySorter.blacklist = Config.getBlackList();
		}
	}

}
