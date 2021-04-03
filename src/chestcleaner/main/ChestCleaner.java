package chestcleaner.main;

import chestcleaner.commands.SortingAdminCommand;
import chestcleaner.commands.BlacklistCommand;
import chestcleaner.commands.CleanInventoryCommand;
import chestcleaner.commands.CleaningItemCommand;
import chestcleaner.commands.SortingConfigCommand;
import chestcleaner.config.PluginConfig;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.config.serializable.ListCategory;
import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.config.serializable.WordCategory;
import chestcleaner.listeners.RefillListener;
import chestcleaner.listeners.SortingListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChestCleaner extends JavaPlugin {

	public static ChestCleaner main;
	private ResourceBundle rb;
	private Locale locale;
	
	@Override
	public void onEnable() {
		main = this;		
		ConfigurationSerialization.registerClass(WordCategory.class);
		ConfigurationSerialization.registerClass(ListCategory.class);
		ConfigurationSerialization.registerClass(MasterCategory.class);
		PluginConfig.getInstance().loadConfig();

		String version = getDescription().getVersion().replace(".", "-");
		String bundleName = getName() + "_" + version;
		getPlugin(this.getClass()).saveResource(bundleName + "_en_GB.properties", false);
		getPlugin(this.getClass()).saveResource(bundleName + "_de_DE.properties", false);

		try {
			URL fileUrl = new File(this.getDataFolder().toString()).toURI().toURL();
			ClassLoader loader = new URLClassLoader(new URL[] { fileUrl });
			rb = ResourceBundle.getBundle(bundleName, locale, loader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Objects.requireNonNull(getCommand(CleanInventoryCommand.COMMAND_ALIAS)).setExecutor(new CleanInventoryCommand());
		Objects.requireNonNull(getCommand(CleaningItemCommand.COMMAND_ALIAS)).setExecutor(new CleaningItemCommand());
		Objects.requireNonNull(getCommand(BlacklistCommand.COMMAND_ALIAS)).setExecutor(new BlacklistCommand());
		Objects.requireNonNull(getCommand(SortingConfigCommand.COMMAND_ALIAS)).setExecutor(new SortingConfigCommand());
		Objects.requireNonNull(getCommand(SortingAdminCommand.COMMAND_ALIAS)).setExecutor(new SortingAdminCommand());

		Bukkit.getPluginManager().registerEvents(new SortingListener(), this);
		Bukkit.getPluginManager().registerEvents(new RefillListener(), this);

		if (PluginConfigManager.isUpdateCheckerActive()) {
			new UpdateChecker(this).checkForUpdate();
		}
	}

	public ResourceBundle getRB() {
		return rb;
	}

	public void setLocale(String language, String country, String variant) {
		locale = new Locale(language, country, variant);
	}

}
