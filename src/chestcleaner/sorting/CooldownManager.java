package chestcleaner.sorting;

import java.util.HashMap;
import java.util.UUID;

import chestcleaner.config.PluginConfigManager;
import org.bukkit.entity.Player;

import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

public class CooldownManager {

	private static CooldownManager instance = null;

	private HashMap<UUID, Long> times;
	private final long  imuneTime = 100;
	
	protected CooldownManager() {
		times = new HashMap<>();
	}

	public boolean isPlayerOnCooldown(Player player) {

		if (player == null) {
			return false;
		}
		
		boolean immune = false;
		if (!PluginConfigManager.isCooldownActive()
				|| player.hasPermission(PluginPermissions.COOLDOWN_IMMUNE.getString())) {
			immune = true;
		}

		if (times.containsKey(player.getUniqueId())) {
			long differnce = System.currentTimeMillis() - times.get(player.getUniqueId());
			int cooldown = PluginConfigManager.getCooldown();
			
			if(immune) {
				if(differnce >= imuneTime) {
					times.put(player.getUniqueId(), System.currentTimeMillis());
					return false;
				}else {
					return true;
				}
			}
			
			if (differnce >= cooldown) {
				times.put(player.getUniqueId(), System.currentTimeMillis());
				return false;
			}
			
			MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_YOU_COOLDOWN, player,
					String.valueOf((cooldown - differnce) / 1000 + 1));
			return true;

		} else {
			times.put(player.getUniqueId(), System.currentTimeMillis());
			return false;
		}

	}

	public static CooldownManager getInstance() {
		if (instance == null) {
			instance = new CooldownManager();
		}
		return instance;
	}

}
