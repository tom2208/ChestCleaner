package chestcleaner.sorting;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

public class CooldownManager {

	private static CooldownManager instance = null;

	private HashMap<UUID, Long> times;
	private int cooldown = 5000;
	private boolean active = true;

	protected CooldownManager() {
		times = new HashMap<>();
	}

	public boolean isPlayerOnCooldown(Player p) {

		if (!active || p.hasPermission(PluginPermissions.COOLDOWN_IMMUNE.getString())) {
			return true;
		}

		if (times.containsKey(p.getUniqueId())) {
			long differnce = System.currentTimeMillis() - times.get(p.getUniqueId());

			if (differnce >= cooldown) {
				times.put(p.getUniqueId(), System.currentTimeMillis());
				return true;
			}
			MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR, MessageID.SORTING_ON_COOLDOWN, p, String.valueOf((cooldown - differnce) / 1000 + 1));
			return false;

		} else {
			times.put(p.getUniqueId(), System.currentTimeMillis());
			return true;
		}

	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getCooldown() {
		return cooldown;
	}
	
	public void setActive(boolean b) {
		this.active = b;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public static CooldownManager getInstance() {
		if (instance == null) {
			instance = new CooldownManager();
		}
		return instance;
	}

}
