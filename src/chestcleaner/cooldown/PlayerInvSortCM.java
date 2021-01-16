package chestcleaner.cooldown;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInvSortCM implements CooldownManager{

    private static CooldownManager instance = null;

    private HashMap<UUID, Long> times;
    private final long  immuneTime = 100;

    public PlayerInvSortCM() {
        times = new HashMap<>();
    }

    @Override
    public boolean isOnCooldown(Object obj) {

        if (obj == null) {
            return false;
        }else if(!(obj instanceof Player)){
            return false;
        }

        Player player = (Player) obj;

        boolean immune = false;
        if (!PluginConfigManager.isCooldownActive()
                || player.hasPermission(PluginPermissions.COOLDOWN_IMMUNE.getString())) {
            immune = true;
        }

        if (times.containsKey(player.getUniqueId())) {
            long difference = System.currentTimeMillis() - times.get(player.getUniqueId());
            int cooldown = PluginConfigManager.getCooldown();

            if(immune) {
                if(difference >= immuneTime) {
                    times.put(player.getUniqueId(), System.currentTimeMillis());
                    return false;
                }else {
                    return true;
                }
            }

            if (difference >= cooldown) {
                times.put(player.getUniqueId(), System.currentTimeMillis());
                return false;
            }

            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_YOU_COOLDOWN, player,
                    String.valueOf((cooldown - difference) / 1000 + 1));
            return true;

        } else {
            times.put(player.getUniqueId(), System.currentTimeMillis());
            return false;
        }

    }

}
