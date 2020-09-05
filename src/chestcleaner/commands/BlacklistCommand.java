package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import chestcleaner.config.PluginConfigManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * A command class representing the blacklist command. Blacklist Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-blacklist
 * 
 * @author Tom2208
 *
 */
public class BlacklistCommand implements CommandExecutor, TabCompleter {
	// The lineNumbers of a page when the list gets displayed in the in game chat.
	private final int LIST_PAGE_LENGTH = 8;
	/* sub-commands */
	private final String addSubCommand = "add";
	private final String removeSubCommand = "remove";
	private final String listSubCommand = "list";
	private final String clearSubCommand = "clear";

	private final String stackingSubCommand = "stacking";
	private final String inventorySubCommand = "inventory";
	private final String autorefillSubCommand = "autorefill";

	private final String[] subCommandList = { addSubCommand, removeSubCommand, listSubCommand, clearSubCommand };
	private final String[] strList = { stackingSubCommand, inventorySubCommand, autorefillSubCommand };

	private enum BlacklistType {
		STACKING, INVENTORY, AUTOREFILL
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		Player player = null;
		List<Material> list = null;
		BlacklistType listType = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (args.length >= 2 && args.length <= 3) {
			if (args[0].equalsIgnoreCase(stackingSubCommand)) {
				list = PluginConfigManager.getBlacklistStacking();
				listType = BlacklistType.STACKING;
			} else if (args[0].equalsIgnoreCase(inventorySubCommand)) {
				list = PluginConfigManager.getBlacklistInventory();
				listType = BlacklistType.INVENTORY;
			} else if (args[0].equalsIgnoreCase(autorefillSubCommand)) {
				list = PluginConfigManager.getBlacklistAutoRefill();
				listType = BlacklistType.AUTOREFILL;
			} else {
				return false;
			}
		}

		// subCommands
		if (args.length == 2) {
			if (listSubCommand.equalsIgnoreCase(args[1])) {
				printBlacklist(sender, "1", list);
				return true;
			} else if (clearSubCommand.equalsIgnoreCase(args[1])) {
				clearBlacklist(sender, listType);
				return true;
			}
			if (player == null) {
				return false;

			} else if (addSubCommand.equalsIgnoreCase(args[1])) {
				addMaterial(sender, listType, list, getMaterialFromPlayerHand(player));
				return true;
			} else if (removeSubCommand.equalsIgnoreCase(args[1])) {
				removeMaterial(sender, listType, list, getMaterialFromPlayerHand(player));
				return true;
			}
		}
		if (args.length == 3) {
			if (listSubCommand.equalsIgnoreCase(args[1])) {
				printBlacklist(sender, args[2], list);
				return true;
			} else if (addSubCommand.equalsIgnoreCase(args[1])) {
				addMaterialName(sender, listType, list, args[2]);
				return true;
			} else if (removeSubCommand.equalsIgnoreCase(args[1])) {
				removeMaterialName(sender, listType, list, args[2]);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();

		if (args.length <= 1) {
			StringUtil.copyPartialMatches(args[0], Arrays.asList(strList), completions);
		} else if (args.length == 2) {
			StringUtil.copyPartialMatches(args[1], Arrays.asList(subCommandList), completions);
		} else if (args.length == 3) {
			if (addSubCommand.equalsIgnoreCase(args[1]) || removeSubCommand.equalsIgnoreCase(args[1]))
				StringUtil.copyPartialMatches(args[2], Arrays.stream(Material.values())
						.map(material -> material.name().toLowerCase()).collect(Collectors.toList()), completions);
		}

		return completions;
	}

	private void addMaterialName(CommandSender sender, BlacklistType type, List<Material> list, String name) {
		Material material = Material.getMaterial(name.toUpperCase());
		if (material == null) {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_MATERIAL_NAME, sender,
					name);
		} else {
			addMaterial(sender, type, list, material);
		}
	}

	/**
	 * Adds {@code material} to the blacklist.
	 * 
	 * @param sender   the sender which executed the command.
	 * @param type     the list on which the material gets added.
	 * @param list     a list which gets modified and set to the config.
	 * @param material the material that gets added to the list.
	 */
	private void addMaterial(CommandSender sender, BlacklistType type, List<Material> list, Material material) {
		if (list.contains(material)) {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_BLACKLIST_EXISTS, sender,
					material.name().toLowerCase());
		} else {

			list.add(material);
			saveList(type, list);

			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_BLACKLIST_ADD, sender,
					material.name().toLowerCase());
		}
	}

	/**
	 * Removes a material with the name {@code name} form a blacklist.
	 * 
	 * @param sender the sender which executed the command.
	 * @param type   the type of blacklist form which you want to remove the
	 *               material.
	 * @param list   the list which gets modified and then set to the config.
	 * @param name   the name of the material you want to remove.
	 */
	private void removeMaterialName(CommandSender sender, BlacklistType type, List<Material> list, String name) {
		Material material = Material.getMaterial(name.toUpperCase());

		if (material == null) {
			try {
				int index = Integer.parseInt(name);
				// expect 1 based index input, bc of list and not all players are programmers
				if (index > 0 && index <= list.size()) {
					material = list.get(index - 1);
				} else {
					MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
							MessageID.ERROR_VALIDATION_INDEX_BOUNDS, sender, String.valueOf(index));
					return;
				}
			} catch (NumberFormatException ex) {
				MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_MATERIAL_NAME, sender,
						name);
				return;
			}

		}
		removeMaterial(sender, type, list, material);
	}

	/**
	 * Removes a {@code material} form a blacklist.
	 * 
	 * @param sender   the sender which executed the command.
	 * @param type     the type of blacklist form which you want to remove the
	 *                 material.
	 * @param list     the list which gets modified and then set to the config.
	 * @param material the material you want to remove.
	 */
	private void removeMaterial(CommandSender sender, BlacklistType type, List<Material> list, Material material) {
		if (!list.contains(material)) {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_BLACKLIST_NOT_EXISTS,
					sender, material.name().toLowerCase());
		} else {

			list.remove(material);
			saveList(type, list);

			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_BLACKLIST_DEL, sender,
					material.name().toLowerCase());
		}
	}

	/**
	 * Sends a page with the page number {@code pageString} of the list to the
	 * {@code sender}.
	 * 
	 * @param sender     the sender which will receive the the list.
	 * @param pageString the page of the list which gets sent.
	 * @param list       The list which contains the page/part of the list you want
	 *                   to send.
	 */
	private void printBlacklist(CommandSender sender, String pageString, List<Material> list) {
		if (list.size() == 0) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_EMPTY, sender);
		} else {

			List<String> names = list.stream().map(item -> item.name().toLowerCase()).collect(Collectors.toList());
			MessageSystem.sendListPageToCS(names, sender, pageString, LIST_PAGE_LENGTH);
		}
	}

	/**
	 * Clears the specific blacklist in the config.
	 * 
	 * @param sender the sender which executed the command. It becomes a success
	 *               message.
	 * @param type   the type of the list. It determines the blacklist which gets
	 *               cleared.
	 */
	private void clearBlacklist(CommandSender sender, BlacklistType type) {
		List<Material> list = new ArrayList<Material>();
		saveList(type, list);
		MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_BLACKLIST_CLEARED, sender);
	}

	/**
	 * Returns the Material of the item in a hand (prefers the main hand, if it's
	 * empty it take the off handF).
	 * 
	 * @param p the player of the hand.
	 * @return it returns the material of you main hand if it is not AIR otherwise
	 *         the Material of your off hand.
	 */
	private Material getMaterialFromPlayerHand(Player p) {
		if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!p.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
				return p.getInventory().getItemInOffHand().getType();
			}
		}
		return p.getInventory().getItemInMainHand().getType();
	}

	/**
	 * Saves the list in to the config.yml.
	 * 
	 * @param type  the type of blacklist you want to save.
	 * @param items the list which gets set to the config.
	 */
	private void saveList(BlacklistType type, List<Material> items) {
		if (type.equals(BlacklistType.STACKING)) {
			PluginConfigManager.setBlacklistStacking(items);
		} else if (type.equals(BlacklistType.INVENTORY)) {
			PluginConfigManager.setBlacklistInventory(items);
		} else if (type.equals(BlacklistType.AUTOREFILL)) {
			PluginConfigManager.setBlacklistAutoRefill(items);
		}
	}
}
