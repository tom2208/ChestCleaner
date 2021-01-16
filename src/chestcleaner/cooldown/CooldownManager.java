package chestcleaner.cooldown;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public interface CooldownManager {

	boolean isOnCooldown(Object obj);

}
