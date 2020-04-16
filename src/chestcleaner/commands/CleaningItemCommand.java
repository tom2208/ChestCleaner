package chestcleaner.commands;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.main.ChestCleaner;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.StringUtils;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A command class representing the CleaningItem command. CleaningItem Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-cleaningitem
 * 
 * @author Tom2208
 *
 */
public class CleaningItemCommand implements CommandExecutor, TabCompleter {

	private final String command = "cleaningitem";
	/* sub-commands */
	private final String getSubCommand = "get";
	private final String setSubCommand = "set";
	private final String giveSubCommand = "give";
	private final String nameSubCommand = "name";
	private final String loreSubCommand = "lore";
	private final String activeSubCommand = "active";
	private final String durabilityLossSubCommand = "durabilityLoss";
	private final String eventDetectionModeSubCommand = "eventDetectionMode";

	private final String nameProperty = command.concat(" ").concat(nameSubCommand);
	private final String loreProperty = command.concat(" ").concat(loreSubCommand);
	private final String activeProperty = command.concat(" ").concat(activeSubCommand);
	private final String durabilityProperty = command.concat(" ").concat(durabilityLossSubCommand);
	private final String eventDetectionProperty = eventDetectionModeSubCommand;

	private final String[] strCommandList = {getSubCommand, setSubCommand, giveSubCommand, nameSubCommand,
			loreSubCommand, activeSubCommand, durabilityLossSubCommand, eventDetectionModeSubCommand};

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (args.length == 1) {
			if (nameSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(sender, nameSubCommand);
			} else if (loreSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(sender, loreSubCommand);
			} else if (activeSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(sender, activeSubCommand);
			} else if (durabilityLossSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(sender, durabilityLossSubCommand);
			} else if (eventDetectionModeSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(sender, eventDetectionModeSubCommand);

			} else if (player == null) {
				return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);

			} else if (setSubCommand.equalsIgnoreCase(args[0])) {
				return setCleaningItem(player);
			} else if (getSubCommand.equalsIgnoreCase(args[0])) {
				return getCleaningItem(player);
			}

		}
		if (args.length >= 2) {
			if (nameSubCommand.equalsIgnoreCase(args[0])) {
				return setItemName(sender, args, player);
			} else if (loreSubCommand.equalsIgnoreCase(args[0])) {
				return setItemLore(sender, args);
			}
		}
		if (args.length == 2) {
			if (activeSubCommand.equalsIgnoreCase(args[0])) {
				return setCleaningItemActive(sender, args[1]);
			} else if (durabilityLossSubCommand.equalsIgnoreCase(args[0])) {
				return setDurabilityLoss(sender, args[1]);
			} else if (eventDetectionModeSubCommand.equalsIgnoreCase(args[0])) {
				return setEventDetectionMode(sender, args[1]);
			} else if (giveSubCommand.equalsIgnoreCase(args[0])) {
				return giveCleaningItem(sender, args[1]);
			}
		}
		return false;
	}

	private boolean getConfig(CommandSender sender, String command) {
		String key = "";
		String value = "";
		switch (command) {
			case nameSubCommand:
				key = nameProperty;
				value = PluginConfigManager.getCleaningItem().getItemMeta().hasDisplayName()
						? PluginConfigManager.getCleaningItem().getItemMeta().getDisplayName()
                        : "<null>";
				break;
			case loreSubCommand:
				key = loreProperty;
				value = PluginConfigManager.getCleaningItem().getItemMeta().hasLore()
						? PluginConfigManager.getCleaningItem().getItemMeta().getLore().toString()
						: "<null>";
				break;
			case activeSubCommand:
				key = activeProperty;
				value = String.valueOf(PluginConfigManager.isCleaningItemActive());
				break;
			case durabilityLossSubCommand:
				key = durabilityProperty;
				value = String.valueOf(PluginConfigManager.isDurabilityLossActive());
				break;
			case eventDetectionModeSubCommand:
				key = eventDetectionProperty;
				value = String.valueOf(PluginConfigManager.isEventModeActive());
				break;
		}
		return MessageSystem.sendMessageToCSWithReplacement(
				MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, sender, key, value);
	}

	private boolean getCleaningItem(Player player) {
		if (!player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GET.getString())) {
			return MessageSystem.sendPermissionError(player, PluginPermissions.CMD_CLEANING_ITEM_GET);
		}

		player.getInventory().addItem(PluginConfigManager.getCleaningItem());
		return MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_CLEANITEM_YOU_GET, player);
	}

	private boolean setCleaningItem(Player player) {

		if (!player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_ITEM.getString())) {
			return MessageSystem.sendPermissionError(player, PluginPermissions.CMD_CLEANING_ITEM_SET_ITEM);
		}

		ItemStack item = player.getInventory().getItemInMainHand().clone();

		if (item.getType() == Material.AIR) {
			return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_HOLD_ITEM, player);
		}

		ItemMeta itemMeta = item.getItemMeta();
		Damageable damageable = ((Damageable) itemMeta);
		damageable.setDamage(0);
		item.setItemMeta(itemMeta);
		item.setAmount(1);

		PluginConfigManager.setCleaningItem(item);
		return MessageSystem.sendChangedValue(player, command, item.toString());
	}

	private boolean giveCleaningItem(CommandSender sender, String playerName) {
		if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GIVE.getString())) {
		    return MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_GIVE);
		}

		Player player2 = Bukkit.getPlayer(playerName);

		if (player2 != null) {
			player2.getInventory().addItem(PluginConfigManager.getCleaningItem());
			return MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
					MessageID.INFO_CLEANITEM_PLAYER_GET, sender, player2.getName());

		} else {
			if (playerName.equalsIgnoreCase("@a")) {
				Object[] players = Bukkit.getOnlinePlayers().toArray();
				for (Object p : players) {
					Player pl = (Player) p;
					pl.getInventory().addItem(PluginConfigManager.getCleaningItem());
					MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
							MessageID.INFO_CLEANITEM_PLAYER_GET, sender, pl.getName());
				}
				return true;
			}

			return MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_PLAYER_NOT_ONLINE,
					sender, playerName);
		}
	}

	private boolean setCleaningItemActive(CommandSender sender, String value) {
		if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_ACTIVE.getString())) {
			return MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_SET_ACTIVE);

		} else if (!StringUtils.isStringTrueOrFalse(value)) {
			return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
		}
		boolean b = Boolean.parseBoolean(value);
		PluginConfigManager.setCleaningItemActive(b);
		return MessageSystem.sendChangedValue(sender, activeProperty, String.valueOf(b));
	}

	private boolean setDurabilityLoss(CommandSender sender, String value) {
		if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_DURABILITYLOSS.getString())) {
			return MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_SET_DURABILITYLOSS);
		} else if (!StringUtils.isStringTrueOrFalse(value)) {
			return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
		}

		boolean b = Boolean.parseBoolean(value);
		PluginConfigManager.setDurabilityLossActive(b);
		return MessageSystem.sendChangedValue(sender, durabilityProperty, String.valueOf(b));
	}

	private boolean setEventDetectionMode(CommandSender sender, String value) {
		if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_EVENT_MODE.getString())) {
			return MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_SET_EVENT_MODE);
		}
		boolean b = Boolean.parseBoolean(value);
		PluginConfigManager.setEventModeActive(b);
		return MessageSystem.sendChangedValue(sender, eventDetectionProperty, String.valueOf(b));
	}

	private boolean setItemLore(CommandSender sender, String[] args) {
		if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_LORE.getString())) {
		    return MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_SET_LORE);
		}

		String lore = args[1];
		for (int i = 2; i < args.length; i++) {
			lore = lore.concat(" ").concat(args[i]);
		}

		String[] lorearray = lore.split("/n");

		ArrayList<String> lorelist = new ArrayList<>();

		for (String obj : lorearray) {
			obj = obj.replace("&", "\u00A7");
			lorelist.add(obj);
		}

		ItemStack is = PluginConfigManager.getCleaningItem();
		ItemMeta im = is.getItemMeta();
		im.setLore(lorelist);
		is.setItemMeta(im);
		PluginConfigManager.setCleaningItem(is);

		return MessageSystem.sendChangedValue(sender, loreProperty, lorelist.toString());
	}

	private boolean setItemName(CommandSender sender, String[] args, Player player) {
		if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_RENAME.getString())) {
		    return MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_RENAME);
		}

		String newname = args[1];
		for (int i = 2; i < args.length; i++) {
			newname = newname.concat(" ").concat(args[i]);
		}

		newname = newname.replace("&", "\u00A7");

		ItemStack is = PluginConfigManager.getCleaningItem();
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(newname);
		is.setItemMeta(im);
		PluginConfigManager.setCleaningItem(is);

		return MessageSystem.sendChangedValue(sender, nameProperty, newname);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();
		final List<String> cleaningItemCommands = Arrays.asList(strCommandList);

		switch (args.length) {
		case 1:
			StringUtil.copyPartialMatches(args[0], cleaningItemCommands, completions);
			break;
		case 2:
			if (args[0].equalsIgnoreCase(activeSubCommand) || args[0].equalsIgnoreCase(durabilityLossSubCommand)
					|| args[0].equalsIgnoreCase(eventDetectionModeSubCommand)) {

				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			} else if (giveSubCommand.equalsIgnoreCase(args[0])) {
				StringUtil.copyPartialMatches(
						args[1], ChestCleaner.main.getServer().getOnlinePlayers()
								.stream().map(Player::getName).collect(Collectors.toList()), completions);
			}
		}

		Collections.sort(completions);
		return completions;
	}
}
