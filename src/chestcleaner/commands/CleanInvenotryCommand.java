package chestcleaner.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import chestcleaner.config.PluginConfigurationManager;
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

		Player p = (Player) cs;

		if (cs instanceof Player) {
			MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER);
			return true;
		}
		
		if (!p.hasPermission(PluginPermissions.CMD_INV_CLEAN.getString()) && PluginConfigurationManager.getInstance().isCleanInvPermission()) {
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
					PluginPermissions.CMD_INV_CLEAN.getString(), p);
			return true;
		}

		if (args.length == 0) {

			// if cs is a console
			if (!(cs instanceof Player)) {
				MessageSystem.sendConsoleMessage(MessageType.SYNTAX_ERROR, syntax);
				return true;
			}

			Block block = BlockDetector.getTargetBlock(p);

			if (BlacklistCommand.inventoryBlacklist.contains(block.getType())) {
				MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVENTORY_ON_BLACKLIST, p);
				return true;
			}

			if (CooldownManager.getInstance().isPlayerOnCooldown(p)) {

				// if the block has no inventory
				if (!InventorySorter.sortPlayerBlock(block, p)) {

					MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
							MessageID.BLOCK_HAS_NO_INVENTORY, p, "(" + block.getX() + " / " + block.getY() + " / "
									+ block.getZ() + ", " + block.getType().name() + ")");
					return true;

				} else {
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);
					return true;
				}

			}

		} else if (args.length == 3 || args.length == 4) {

			int x = (int) Double.parseDouble(args[0]);
			int y = (int) Double.parseDouble(args[1]);
			int z = (int) Double.parseDouble(args[2]);

			World w;

			if (args.length == 4) {
				w = Bukkit.getWorld(args[3]);
			} else {
				w = p.getWorld();
			}

			if (w == null) {
				MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR, MessageID.NO_WORLD_WITH_THIS_NAME,
						p, args[3]);
				return true;
			}

			if (args.length == 4) {
				w = Bukkit.getWorld(args[3]);
			}

			Block block = BlockDetector.getBlockByLocation(new Location(w, x, y, z));

			if (BlacklistCommand.inventoryBlacklist.contains(block.getType())) {
				MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVENTORY_ON_BLACKLIST, p);
				return true;
			}

			if (!CooldownManager.getInstance().isPlayerOnCooldown(p)) {
				return true;
			}

			if (!InventorySorter.sortPlayerBlock(block, p)) {

				MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR, MessageID.BLOCK_HAS_NO_INVENTORY,
						p, "(" + x + " / " + y + " / " + z + ")");

				return true;

			} else {

				MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);

				return true;
			}

		}
		return true;
	}
	
	private final String syntax = "/cleaninventory <x> <y> <z>";
	
}
