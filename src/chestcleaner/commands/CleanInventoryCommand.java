package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chestcleaner.cooldown.CMRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.InventoryDetector;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * A command class representing the CleanInventory command. CleanInventory
 * Command explained:
 * https://github.com/tom2208/ChestCleaner/wiki/Command--cleaninventory
 *
 * @author Tom2208
 */
public class CleanInventoryCommand implements CommandExecutor, TabCompleter {

	private final String ownSubCommand = "own";

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
		} else if (args.length == 1) {

			return sortPlayerInventory(player, args[0]);

		} else if (args.length == 0) {

			sortInvForPlayer(player);
			return true;
		} else if (args.length == 3) {
			sortInvAtLocation(args[0], args[1], args[2], player.getWorld(), player, sender);
			return true;
		}
		return false;
	}

	/**
	 * The player {@code player} sorts the inventory of the player with the name
	 * {@code playerName}. He needs the correct permissions.
	 * 
	 * @param player     the player who sorts the inventory.
	 * @param playerName the name of the player whose inventory will get sorted.
	 * @return <b>true</b> if a inventory got sorted, <b>false</b> if not.
	 */
	private boolean sortPlayerInventory(Player player, String playerName) {

		if (playerName.equalsIgnoreCase(ownSubCommand) || playerName.equalsIgnoreCase(player.getDisplayName())) {
			if (!player.hasPermission(PluginPermissions.CMD_INV_CLEAN_OWN.getString())) {
				MessageSystem.sendPermissionError(player, PluginPermissions.CMD_INV_CLEAN_OWN);
				return true;
			}
			if (InventorySorter.sortPlayerInventory(player)) {
				MessageSystem.sendSortedMessage(player);
				InventorySorter.playSortingSound(player);
			}
		} else {

			if (!player.hasPermission(PluginPermissions.CMD_INV_CLEAN_OTHERS.getString())) {
				MessageSystem.sendPermissionError(player, PluginPermissions.CMD_INV_CLEAN_OTHERS);
				return true;
			}

			Player player2 = Bukkit.getPlayer(playerName);

			if (player2 == null) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_PLAYER_NOT_ONLINE, player);
			} else {
				if (InventorySorter.sortInventory(player2.getInventory(), player,
						InventoryDetector.getPlayerInventoryList(player2))) {
					MessageSystem.sendSortedMessage(player);
					MessageSystem.sendSortedMessage(player2);
					InventorySorter.playSortingSound(player);
					InventorySorter.playSortingSound(player2);
				}
			}

		}
		return true;
	}

	/**
	 * The player {@code p} sorts a blocks inventory.
	 * 
	 * @param p the player who sorts the inventory.
	 */
	private void sortInvForPlayer(Player p) {
		Block block = BlockDetector.getTargetBlock(p);
		sortBlock(block, p, p);
	}

	/**
	 * Sorts an inventory of a block if it has one.
	 * 
	 * @param xStr     the x coordinate as a String.
	 * @param yStr     the y coordinate as a String.
	 * @param zStr     the z coordinate as a String.
	 * @param worldStr the name of the world of block whose inventory you want to
	 *                 sort.
	 * @param player   the player which executed the inventory (can be null).
	 * @param cs       the sender which executed the command.
	 */
	private void sortInvInWorld(String xStr, String yStr, String zStr, String worldStr, Player player,
			CommandSender cs) {

		World world = Bukkit.getWorld(worldStr);
		if (world == null) {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_WORLD_NAME, cs, worldStr);
		} else {
			sortInvAtLocation(xStr, yStr, zStr, world, player, cs);
		}
	}

	/**
	 * Sorts an inventory of a block if it has one.
	 * 
	 * @param xStr   the x coordinate as a String.
	 * @param yStr   the y coordinate as a String.
	 * @param zStr   the z coordinate as a String.
	 * @param world  the world of block whose inventory you want to sort.
	 * @param player the player which executed the inventory (can be null).
	 * @param sender the sender which executed the command.
	 */
	private void sortInvAtLocation(String xStr, String yStr, String zStr, World world, Player player,
			CommandSender sender) {
		int x = (int) Double.parseDouble(xStr);
		int y = (int) Double.parseDouble(yStr);
		int z = (int) Double.parseDouble(zStr);
		Block block = BlockDetector.getBlockByLocation(new Location(world, x, y, z));
		sortBlock(block, player, sender);
	}

	/**
	 * Sorts the inventory of a block if it has one.
	 * 
	 * @param block  the block which may have an inventory.
	 * @param p      the player who is sorting.
	 * @param sender the sender which executed the command.
	 */
	private void sortBlock(Block block, Player p, CommandSender sender) {
		if (PluginConfigManager.getBlacklistInventory().contains(block.getType())) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_INVENTORY, sender);
		} else if (CMRegistry.isOnCooldown(CMRegistry.CMIdentifier.SORTING, p)) {
			// isPlayerOnCooldown sends error message
		} else if (InventorySorter.sortPlayerBlock(block, p)) {
			MessageSystem.sendSortedMessage(sender);
		} else {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_BLOCK_NO_INVENTORY, sender,
					"(" + block.getX() + " / " + block.getY() + " / " + block.getZ() + ", " + block.getType().name()
							+ ")");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();

		if (args.length == 1) {
			StringUtil.copyPartialMatches(args[0], Arrays.asList(ownSubCommand), completions);
		}

		return completions;
	}
}
