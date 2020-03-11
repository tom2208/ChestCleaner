package chestcleaner.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import chestcleaner.playerdata.PlayerDataManager;

public class DataLoadingListener implements Listener {

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent e) {
		PlayerDataManager.loadPlayerData(e.getPlayer());
	}

	@EventHandler
	private void onPlayerLeave(PlayerQuitEvent e) {
		PlayerDataManager.removePlayerDataFormMemory(e.getPlayer());
	}

}
