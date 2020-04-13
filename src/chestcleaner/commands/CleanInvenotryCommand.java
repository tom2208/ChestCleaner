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
 *
 */
public class CleanInvenotryCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		Player player = null;

		if (cs instanceof Player) {
			player = (Player) cs;
			if (!player.hasPermission(PluginPermissions.CMD_INV_CLEAN.getString())
					&& PluginConfigManager.isCleanInvPermission()) {
				MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
						PluginPermissions.CMD_INV_CLEAN.getString(), player);
				return true;
			}
		}

		if (args.length == 0) {
			
			if(player == null) {
				MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntaxConsole, cs);
				return true;
			}
			
			Block block = BlockDetector.getTargetBlock(player);

			if (PluginConfigManager.getBlacklistInventory().contains(block.getType())) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVENTORY_ON_BLACKLIST, player);
				return true;
			}

			if (CooldownManager.getInstance().isPlayerOnCooldown(player)) {

				// if the block has no inventory
				if (!InventorySorter.sortPlayerBlock(block, player)) {

					MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
							MessageID.BLOCK_HAS_NO_INVENTORY, player, "(" + block.getX() + " / " + block.getY() + " / "
									+ block.getZ() + ", " + block.getType().name() + ")");
					return true;

				} else {
					MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, player);
					return true;
				}

			}

		} else if (args.length == 3) {
			
			if(player == null) {
				MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntaxConsole, cs);
				return true;
			}else {
				return sortInvAtLocation(args[0], args[1], args[2], null, player, cs);
			}

		} else if(args.length == 4){
			return sortInvAtLocation(args[0], args[1], args[2], args[3], player, cs);
		} else {
			MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntaxConsole, cs);
			return true;
		}
		return true;
	}

	private boolean sortInvAtLocation(String xStr, String yStr, String zStr, String worldStr, Player player,
			CommandSender cs) {

		int x = (int) Double.parseDouble(xStr);
		int y = (int) Double.parseDouble(yStr);
		int z = (int) Double.parseDouble(zStr);

		World world;

		/**
		 * World detecting
		 */
		if (worldStr == null) {
			if (player == null) {
				MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntaxConsole, cs);
				return true;
			} else {
				world = player.getWorld();
			}
		} else {
			world = Bukkit.getWorld(worldStr);
			if (world == null) {
				MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.NO_WORLD_WITH_THIS_NAME, cs, worldStr);
				return true;
			}
		}

		/**
		 * Block detecting
		 */
		Block block = BlockDetector.getBlockByLocation(new Location(world, x, y, z));

		if (PluginConfigManager.getBlacklistInventory().contains(block.getType())) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVENTORY_ON_BLACKLIST, cs);
			return true;
		}

		if (!CooldownManager.getInstance().isPlayerOnCooldown(player)) {
			return true;
		}

		/**
		 * Sorting inventory
		 */
		CommandSender sender = player == null ? cs : player;

		if (InventorySorter.sortPlayerBlock(block, player)) {
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, sender);
			return true;
		} else {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.BLOCK_HAS_NO_INVENTORY, sender,
					"(" + x + " / " + y + " / " + z + ")");
			return true;
		}
	}

	private final String syntaxConsole = "/cleaninventory <x> <y> <z> <world>";

}
