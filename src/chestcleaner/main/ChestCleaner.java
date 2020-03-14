package chestcleaner.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.commands.CleanInvenotryCommand;
import chestcleaner.commands.CleaningItemCommand;
import chestcleaner.commands.SortingConfigCommand;
import chestcleaner.commands.TimerCommand;
import chestcleaner.config.Config;
import chestcleaner.listeners.SortingListener;
import chestcleaner.listeners.DataLoadingListener;
import chestcleaner.listeners.RefillListener;
import chestcleaner.timer.Counter;

public class ChestCleaner extends JavaPlugin {

	public static boolean cleanInvPermission = true;
	public static boolean timer = true;
	public static int time = 5;
	public static ItemStack item = new ItemStack(Material.IRON_HOE);
	public static boolean durability = true;
	public static boolean itemBoolean = true;
	public static boolean eventmode = false;
	public static boolean blockRefill = true;
	public static boolean consumablesRefill = true;

	public static ChestCleaner main;

	private Counter c = new Counter();

	@Override
	public void onEnable() {
		main = this;
		Config.getInstance().loadConfig();
		getCommand("cleaninventory").setExecutor(new CleanInvenotryCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("cleaningitem").setExecutor(new CleaningItemCommand());
		getCommand("blacklist").setExecutor(new BlacklistCommand());
		getCommand("sortingconfig").setExecutor(new SortingConfigCommand());
		Bukkit.getPluginManager().registerEvents(new SortingListener(), this);
		Bukkit.getPluginManager().registerEvents(new RefillListener(), this);
		Bukkit.getPluginManager().registerEvents(new DataLoadingListener(), this);
		c.start();
		new UpdateChecker(this).checkForUpdate();
	}

}
