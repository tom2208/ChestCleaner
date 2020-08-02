package chestcleaner.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.CooldownManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * A command class representing the cleaninventory command. CleanInventory
 * Command explained:
 * https://github.com/tom2208/ChestCleaner/wiki/Command--cleaninventory
 *
 * @author Tom2208
 */
public class CleanInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (!sender.hasPermission(PluginPermissions.CMD_INV_CLEAN.getString())) {
            MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_INV_CLEAN);
            return true;
        }

        if (args.length == 4) {
            sortInvInWorld(args[0], args[1], args[2], args[3], player, sender);

        } else if (player == null) {
            return false;
        } else if (args.length == 0) {
            sortInvForPlayer(player);
            return true;
        } else if (args.length == 3) {
            sortInvAtLocation(args[0], args[1], args[2], player.getWorld(), player, sender);
            return true;
        }
        return false;
    }

    private void sortInvForPlayer(Player p) {
        Block block = BlockDetector.getTargetBlock(p);
        sortBlock(block, p, p);
    }

    private void sortInvInWorld(String xStr, String yStr, String zStr, String worldStr, Player player,
								   CommandSender cs) {

        World world = Bukkit.getWorld(worldStr);
        if (world == null) {
            MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
                    MessageID.ERROR_WORLD_NAME, cs, worldStr);
        }else {
        
        	sortInvAtLocation(xStr, yStr, zStr, world, player, cs);
        }
    }

    private void sortInvAtLocation(String xStr, String yStr, String zStr, World world, Player player,
                                      CommandSender sender) {
        int x = (int) Double.parseDouble(xStr);
        int y = (int) Double.parseDouble(yStr);
        int z = (int) Double.parseDouble(zStr);
        Block block = BlockDetector.getBlockByLocation(new Location(world, x, y, z));
        sortBlock(block, player, sender);
    }


    private void sortBlock(Block block, Player p, CommandSender sender) {
        if (PluginConfigManager.getBlacklistInventory().contains(block.getType())) {
            MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_INVENTORY, sender);
        }else if (CooldownManager.getInstance().isPlayerOnCooldown(p)) {
            // isPlayerOnCooldown sends error message
        }else if (InventorySorter.sortPlayerBlock(block, p)) {
            MessageSystem.sendSortedMessage(sender);
        } else {
        	MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
                MessageID.ERROR_BLOCK_NO_INVENTORY, sender, "(" + block.getX() + " / "
                        + block.getY() + " / " + block.getZ() + ", " + block.getType().name() + ")");
        }
    }
}
