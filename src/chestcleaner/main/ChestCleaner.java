package chestcleaner.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.commands.CleanInvenotryCommand;
import chestcleaner.commands.CleaningItemCommand;
import chestcleaner.commands.SortingConfigCommand;
import chestcleaner.commands.CooldownCommand;
import chestcleaner.config.PluginConfig;
import chestcleaner.listeners.SortingListener;
import chestcleaner.listeners.DataLoadingListener;
import chestcleaner.listeners.RefillListener;

public class ChestCleaner extends JavaPlugin {

	public static boolean cleanInvPermission = true;
	public static ItemStack item = new ItemStack(Material.IRON_HOE);
	public static boolean durability = true;
	public static boolean itemBoolean = true;
	public static boolean eventmode = false;
	public static boolean blockRefill = true;
	public static boolean consumablesRefill = true;
	private boolean updateCheckerActive = true;

	public static ChestCleaner main;
	private ResourceBundle rb;
	private Locale locale;

	@Override
	public void onEnable() {
		main = this;
		PluginConfig.getInstance().loadConfig();
		getCommand("cleaninventory").setExecutor(new CleanInvenotryCommand());
		getCommand("cooldown").setExecutor(new CooldownCommand());
		getCommand("cleaningitem").setExecutor(new CleaningItemCommand());
		getCommand("blacklist").setExecutor(new BlacklistCommand());
		getCommand("sortingconfig").setExecutor(new SortingConfigCommand());
		Bukkit.getPluginManager().registerEvents(new SortingListener(), this);
		Bukkit.getPluginManager().registerEvents(new RefillListener(), this);
		Bukkit.getPluginManager().registerEvents(new DataLoadingListener(), this);

		if (updateCheckerActive) {
			new UpdateChecker(this).checkForUpdate();
		}

		getPlugin(this.getClass()).saveResource("ChestCleaner_en_GB.properties", false);

		try {
			URL fileUrl = new File(this.getDataFolder().toString()).toURI().toURL();
			ClassLoader loader = new URLClassLoader(new URL[] { fileUrl });
			rb = ResourceBundle.getBundle(getName(), locale, loader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}


	}

	public void setUpdateCheckerActive(boolean b) {
		this.updateCheckerActive = b;
	}

	public ResourceBundle getRB() {
		return rb;
	}

	public void setLocale(String lang, String country) {
		locale = new Locale(lang, country);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
