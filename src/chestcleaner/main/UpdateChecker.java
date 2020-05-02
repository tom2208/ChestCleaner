package chestcleaner.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import chestcleaner.utils.PluginPermissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import chestcleaner.listeners.SortingListener;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

public class UpdateChecker {

	private final JavaPlugin javaPlugin;
	private final String localPluginVersion;
	private String spigotPluginVersion;
	
	private static final int ID = 40313;
	private static final long CHECK_INTERVAL = 12_000; // In ticks.
	
	public UpdateChecker(final JavaPlugin javaPlugin) {
		this.javaPlugin = javaPlugin;
		this.localPluginVersion = javaPlugin.getDescription().getVersion();
	}

	public void checkForUpdate() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> {
					// Request the current version of your plugin on SpigotMC.
					try {
						final HttpsURLConnection connection = (HttpsURLConnection) new URL(
								"https://api.spigotmc.org/legacy/update.php?resource=" + ID).openConnection();
						connection.setRequestMethod("GET");
						spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream()))
								.readLine();
					} catch (final IOException e) {
						e.printStackTrace();
						cancel();
						return;
					}

					// Check if the requested version is the same as the one in your plugin.yml.
					if (localPluginVersion.equals(spigotPluginVersion))
						return;

					MessageSystem.sendConsoleMessage(MessageType.SUCCESS, MessageID.INFO_UPDATE_AVAILABLE);

					// Register the PlayerJoinEvent
					Bukkit.getScheduler().runTask(javaPlugin,
							() -> Bukkit.getPluginManager().registerEvents(new SortingListener() {
								@EventHandler(priority = EventPriority.MONITOR)
								public void onPlayerJoin(final PlayerJoinEvent event) {
									final Player player = event.getPlayer();
									if (!player.hasPermission(PluginPermissions.UPDATE_PLUGIN.getString()))
										return;

									MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_UPDATE_AVAILABLE, player);
								}
							}, javaPlugin));

					cancel(); // Cancel the runnable as an update has been found.
				});
			}
		}.runTaskTimer(javaPlugin, 0, CHECK_INTERVAL);
	}
}
