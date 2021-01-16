package chestcleaner.cooldown;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCM implements CooldownManager {

    private MessageID msgId;
    private Map<UUID, Long> map;
    private final long immuneTime = 100;

    public PlayerCM(MessageID msgId) {
        this.msgId = msgId;
        map = new HashMap<>();
    }

    @Override
    public boolean isOnCooldown(Object obj) {

        if (obj == null) {
            return false;
        } else if (!(obj instanceof Player)) {
            return false;
        }

        Player player = (Player) obj;

        boolean immune = false;
        if (!PluginConfigManager.isCooldownActive()
                || player.hasPermission(PluginPermissions.COOLDOWN_IMMUNE.getString())) {
            immune = true;
        }

        if (map.containsKey(player.getUniqueId())) {
            long difference = System.currentTimeMillis() - map.get(player.getUniqueId());
            int cooldown = PluginConfigManager.getCooldown();

            if (immune) {
                if (difference >= immuneTime) {
                    map.put(player.getUniqueId(), System.currentTimeMillis());
                    return false;
                } else {
                    return true;
                }
            }

            if (difference >= cooldown) {
                map.put(player.getUniqueId(), System.currentTimeMillis());
                return false;
            }

            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, msgId, player,
                    String.valueOf((cooldown - difference) / 1000 + 1));
            return true;

        } else {
            map.put(player.getUniqueId(), System.currentTimeMillis());
            return false;
        }

    }
}
