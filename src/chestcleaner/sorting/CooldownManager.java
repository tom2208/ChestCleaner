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
	private final int defaultCooldown = 5000;
	private int cooldown = defaultCooldown;
	private boolean active = true;

	protected CooldownManager() {
		times = new HashMap<>();
	}

	public boolean isPlayerOnCooldown(Player player) {
		
		if(player == null) {
			return true;
		}
		
		if (!active || player.hasPermission(PluginPermissions.COOLDOWN_IMMUNE.getString())) {
			return true;
		}

		if (times.containsKey(player.getUniqueId())) {
			long differnce = System.currentTimeMillis() - times.get(player.getUniqueId());

			if (differnce >= cooldown) {
				times.put(player.getUniqueId(), System.currentTimeMillis());
				return true;
			}
			MessageSystem.sendMessageToPlayerWithReplacement(MessageType.ERROR, MessageID.SORTING_ON_COOLDOWN, player, String.valueOf((cooldown - differnce) / 1000 + 1));
			return false;

		} else {
			times.put(player.getUniqueId(), System.currentTimeMillis());
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
