package chestcleaner.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import chestcleaner.main.Main;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.timer.Timer;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

/**
 * @author Tom
 */
public class CleanInvenotryCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		Player p = (Player) cs;
		boolean isPlayer = cs instanceof Player;
		if (isPlayer) {
			if (!p.hasPermission("chestcleaner.cmd.cleaninventory") && Main.cleanInvPermission) {
				MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION, "chestcleaner.cmd.cleaninventory", p);
				return true;
			}
		}

		if (args.length == 0) {

			// if cs is a console
			if (!(cs instanceof Player)) {
				MessageSystem.sendConsoleMessage(MessageType.SYNTAX_ERROR, "/cleaninventory <x> <y> <z>");
				return true;
			}

			Block block = BlockDetector.getTargetBlock(p);

			if(BlacklistCommand.inventoryBlacklist.contains(block.getType())){
				MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVENTORY_ON_BLACKLIST, p);
				return true;
			}
			
			if (Timer.playerCheck(p)) {
				
				// if the block has no inventory
				if (!InventorySorter.sortPlayerBlock(block, p)) {

					MessageSystem.sendMessageToPlayer(MessageType.ERROR,
							StringTable.getMessage(MessageID.BLOCK_HAS_NO_INV, "%location",
									"(" + block.getX() + " / " + block.getY() + " / " + block.getZ() + ", "+ block.getType().name() +")"),
							p);
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

			if (isPlayer) {
				if (args.length == 4)
					w = Bukkit.getWorld(args[3]);
				else
					w = p.getWorld();
				if (w == null) {
					MessageSystem.sendMessageToPlayer(MessageType.ERROR,
							StringTable.getMessage(MessageID.INVALID_WORLD_NAME, "%worldname", args[3]), p);

					return true;
				}
			} else {
				w = Bukkit.getWorld(args[3]);
				if (w == null) {
					MessageSystem.sendConsoleMessage(MessageType.ERROR,
							StringTable.getMessage(MessageID.INVALID_WORLD_NAME, "%worldname", args[3]));
					return true;
				}

			}

			if (args.length == 4) {
				w = Bukkit.getWorld(args[3]);
			}

			Block block = BlockDetector.getBlockByLocation(new Location(w, x, y, z));

			if(BlacklistCommand.inventoryBlacklist.contains(block.getType())){
				MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVENTORY_ON_BLACKLIST, p);
				return true;
			}
			
			if (isPlayer && !Timer.playerCheck(p)) {
				return true;
			}
			
			if (!InventorySorter.sortPlayerBlock(block, p)) {

				if (isPlayer) {
					MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable.getMessage(
							MessageID.BLOCK_HAS_NO_INV, "%location", "(" + x + " / " + y + " / " + z + ")"), p);
				} else {
					MessageSystem.sendConsoleMessage(MessageType.ERROR, StringTable.getMessage(
							MessageID.BLOCK_HAS_NO_INV, "%location", "(" + x + " / " + y + " / " + z + ")"));
				}

				return true;

			} else {
				if (isPlayer) {
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);
				} else {
					MessageSystem.sendConsoleMessage(MessageType.SUCCESS, MessageID.INVENTORY_SORTED);
				}

				return true;
			}

		}
		return true;
	}

}
