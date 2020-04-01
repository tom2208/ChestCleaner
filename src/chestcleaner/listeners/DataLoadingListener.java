package chestcleaner.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import chestcleaner.config.PlayerDataManager;

public class DataLoadingListener implements Listener {

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent e) {
		PlayerDataManager.getInstance().loadPlayerData(e.getPlayer());
	}

	@EventHandler
	private void onPlayerLeave(PlayerQuitEvent e) {
		PlayerDataManager.getInstance().removePlayerDataFormMemory(e.getPlayer());
	}

}
