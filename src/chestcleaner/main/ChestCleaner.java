package chestcleaner.main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
import chestcleaner.config.PluginConfigManager;
import chestcleaner.listeners.SortingListener;
import chestcleaner.listeners.DataLoadingListener;
import chestcleaner.listeners.RefillListener;

public class ChestCleaner extends JavaPlugin {

	public static ItemStack item = new ItemStack(Material.IRON_HOE);

	public static ChestCleaner main;
	private ResourceBundle rb;
	private Locale locale;

	@Override
	public void onEnable() {
		main = this;
		String version = getDescription().getVersion().replace(".", "-");
		PluginConfig.getInstance().loadConfig();
		getPlugin(this.getClass()).saveResource(getName() + "_en_GB_" + version + ".properties", false);
		getPlugin(this.getClass()).saveResource(getName() + "_de_DE_" + version + ".properties", false);
		getPlugin(this.getClass()).saveResource("config.yml", false);
		
		try {
			URL fileUrl = new File(this.getDataFolder().toString()).toURI().toURL();
			ClassLoader loader = new URLClassLoader(new URL[] { fileUrl });
			rb = ResourceBundle.getBundle(getName(), locale, loader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		getCommand("cleaninventory").setExecutor(new CleanInvenotryCommand());
		getCommand("cooldown").setExecutor(new CooldownCommand());
		getCommand("cleaningitem").setExecutor(new CleaningItemCommand());
		getCommand("blacklist").setExecutor(new BlacklistCommand());
		getCommand("sortingconfig").setExecutor(new SortingConfigCommand());
		Bukkit.getPluginManager().registerEvents(new SortingListener(), this);
		Bukkit.getPluginManager().registerEvents(new RefillListener(), this);
		Bukkit.getPluginManager().registerEvents(new DataLoadingListener(), this);

		if (PluginConfigManager.getInstance().isUpdateCheckerActive()) {
			new UpdateChecker(this).checkForUpdate();
		}

	}

	public ResourceBundle getRB() {
		return rb;
	}

	public void setLocale(String language, String country, String variant) {
		locale = new Locale(language, country, variant);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
