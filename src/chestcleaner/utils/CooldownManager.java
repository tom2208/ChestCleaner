package chestcleaner.utils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import chestcleaner.main.ChestCleaner;

public class CooldownManager {

	private static CooldownManager instance = null;
	
	private HashMap<UUID, Long> times;
	private int cooldown = 5000;
	
	protected CooldownManager() {
		times = new HashMap<>();
	}

	public boolean isPlayerOnCooldown(Player p) {
		
		if (ChestCleaner.timer && !p.hasPermission(PluginPermissions.TIMER_NO_EFFECT.getString())) {
			return true;
		}
		
		if (times.containsKey(p.getUniqueId())) {

			long differnce = System.currentTimeMillis() - times.get(p.getUniqueId());

			if (differnce >= cooldown) {
				times.put(p.getUniqueId(), System.currentTimeMillis());
				return true;
			}
			return false;

		} else {
			times.put(p.getUniqueId(), System.currentTimeMillis());
			return true;
		}

	}
	
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
	
	public static CooldownManager getInstance() {
		if(instance == null) {
			instance = new CooldownManager();
		}
		return instance;
	}
	
}
