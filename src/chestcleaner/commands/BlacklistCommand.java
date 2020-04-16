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

	private final String sortingSubCommand = "sorting";
	private final String inventorySubCommand = "inventory";

	private final String[] subCommandList = {addSubCommand, removeSubCommand, listSubCommand, clearSubCommand };
	private final String[] strList = { sortingSubCommand, inventorySubCommand};

	private enum BlacklistType {SORTING, INVENTORY}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		Player player = null;
		List<Material> list = null;
		BlacklistType listType = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (args.length >= 2 && args.length <= 3) {
			if (args[0].equalsIgnoreCase(sortingSubCommand)) {
				list = PluginConfigManager.getBlacklistSorting();
				listType = BlacklistType.SORTING;
			} else if (args[0].equalsIgnoreCase(inventorySubCommand)) {
				list = PluginConfigManager.getBlacklistInventory();
				listType = BlacklistType.INVENTORY;
			} else {
				return false;
			}
		}

		// subCommands
		if (args.length == 2) {
			if (listSubCommand.equalsIgnoreCase(args[1])) {
				return printBlacklist(sender, "1", list);
			} else if (clearSubCommand.equalsIgnoreCase(args[1])) {
				return clearBlacklist(sender, listType, list);
			}
			if (player == null) {
				return false;

			} else if (addSubCommand.equalsIgnoreCase(args[1])) {
				return addMaterial(sender, listType, list, getMaterialFromPlayerHand(player));
			} else if (removeSubCommand.equalsIgnoreCase(args[1])) {
				return removeMaterial(sender, listType, list, getMaterialFromPlayerHand(player));
			}
		}
		if (args.length == 3) {
			if (listSubCommand.equalsIgnoreCase(args[1])) {
				return printBlacklist(sender, args[2], list);
			} else if (addSubCommand.equalsIgnoreCase(args[1])) {
				return addMaterialName(sender, listType, list, args[2]);
			} else if (removeSubCommand.equalsIgnoreCase(args[1])) {
				return removeMaterialName(sender, listType, list, args[2]);
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();
		final List<String> commandList = Arrays.asList(subCommandList);
		final List<String> lists = Arrays.asList(strList);

		if (args.length <= 1) {
			StringUtil.copyPartialMatches(args[0], lists, completions);
		} else if (args.length == 2) {
			StringUtil.copyPartialMatches(args[1], commandList, completions);
		} else if (args.length == 3) {
			if (addSubCommand.equalsIgnoreCase(args[1]) || removeSubCommand.equalsIgnoreCase(args[1]))
				StringUtil.copyPartialMatches(
						args[2],
						Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()),
						completions);
		}

		return completions;
	}

	private boolean addMaterialName(CommandSender sender, BlacklistType type, List<Material> list, String name) {
		Material material = Material.getMaterial(name);
		if (material == null) {
			return MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
					MessageID.ERROR_MATERIAL_NAME, sender, name);
		}
		return addMaterial(sender, type, list, material);
	}

	private boolean addMaterial(CommandSender sender, BlacklistType type, List<Material> list, Material material) {
		if (list.contains(material)) {
			return MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
					MessageID.ERROR_BLACKLIST_EXISTS, sender, material.name());
		}

		list.add(material);
		saveList(type, list);

		return MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
				MessageID.INFO_BLACKLIST_ADD, sender, material.name());
	}

	private boolean removeMaterialName(CommandSender sender, BlacklistType type, List<Material> list, String name) {
		Material material = Material.getMaterial(name);

		if (material == null) {
			try {
				int index = Integer.parseInt(name);
				// expect 1 based index input, bc not all players are programmers
				if (index > 0 && index <= list.size()) {
					material = list.get(index - 1);
				} else {
					return MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
							MessageID.ERROR_VALIDATION_INDEX_BOUNDS, sender, String.valueOf(index));
				}
			} catch (NumberFormatException ex) {
				return MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
						MessageID.ERROR_MATERIAL_NAME, sender, name);
			}

		}
		return removeMaterial(sender, type, list, material);
	}

	private boolean removeMaterial(CommandSender sender, BlacklistType type, List<Material> list, Material material) {
		if (!list.contains(material)) {
			return MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
					MessageID.ERROR_BLACKLIST_NOT_EXISTS, sender, material.name());
		}

		list.remove(material);
		saveList(type, list);

		return MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
				MessageID.INFO_BLACKLIST_DEL, sender, material.name());
	}

	private boolean printBlacklist(CommandSender sender, String pageString, List<Material> list) {
		if (list.size() == 0) {
			return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_EMPTY, sender);
		}

		List<String> names  = list.stream().map(Enum::name).collect(Collectors.toList());
		return MessageSystem.sendListPageToCS(names, sender, pageString, LIST_PAGE_LENGTH);
	}

	private boolean clearBlacklist(CommandSender sender, BlacklistType type, List<Material> list) {
		list.clear();
		saveList(type, list);
		return MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_BLACKLIST_CLEARED, sender);
	}

	/**
	 * Returns the Material of a hand.
	 * 
	 * @param p The player of the hand.
	 * @return It returns the material of you main hand if it is not AIR otherwise
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
	 * Saves the list in to the config.yml .
	 */
	private void saveList(BlacklistType type, List<Material> items) {
		if (type == BlacklistType.SORTING) {
			PluginConfigManager.setBlacklistSorting(items);
		} else if (type == BlacklistType.INVENTORY) {
			PluginConfigManager.setBlacklistInventory(items);
		}
	}
}
