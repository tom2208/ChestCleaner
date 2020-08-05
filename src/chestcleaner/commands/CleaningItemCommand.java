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
	private final String openEventSubCommand = "openEvent";
	
	private final String nameProperty = command.concat(" ").concat(nameSubCommand);
	private final String loreProperty = command.concat(" ").concat(loreSubCommand);
	private final String activeProperty = command.concat(" ").concat(activeSubCommand);
	private final String durabilityProperty = command.concat(" ").concat(durabilityLossSubCommand);
	private final String openEventProperty = openEventSubCommand;

	private final String[] strCommandList = { getSubCommand, setSubCommand, giveSubCommand, nameSubCommand,
			loreSubCommand, activeSubCommand, durabilityLossSubCommand, openEventSubCommand };

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (args.length == 1) {
			if (nameSubCommand.equalsIgnoreCase(args[0])) {
				getConfig(sender, nameSubCommand);
				return true;
			} else if (loreSubCommand.equalsIgnoreCase(args[0])) {
				getConfig(sender, loreSubCommand);
				return true;
			} else if (activeSubCommand.equalsIgnoreCase(args[0])) {
				getConfig(sender, activeSubCommand);
				return true;
			} else if (durabilityLossSubCommand.equalsIgnoreCase(args[0])) {
				getConfig(sender, durabilityLossSubCommand);
				return true;
			} else if (openEventSubCommand.equalsIgnoreCase(args[0])) {
				getConfig(sender, openEventSubCommand);
				return true;

			} else if (player == null) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
				return true;

			} else if (setSubCommand.equalsIgnoreCase(args[0])) {
				setCleaningItem(player);
				return true;
			} else if (getSubCommand.equalsIgnoreCase(args[0])) {
				getCleaningItem(player);
				return true;
			}

		}
		if (args.length >= 2) {
			if (nameSubCommand.equalsIgnoreCase(args[0])) {
				setItemName(sender, args, player);
				return true;
			} else if (loreSubCommand.equalsIgnoreCase(args[0])) {
				setItemLore(sender, args);
				return true;
			}
		}
		if (args.length == 2) {
			if (activeSubCommand.equalsIgnoreCase(args[0])) {
				setCleaningItemActive(sender, args[1]);
				return true;
			} else if (durabilityLossSubCommand.equalsIgnoreCase(args[0])) {
				setDurabilityLoss(sender, args[1]);
				return true;
			} else if (openEventSubCommand.equalsIgnoreCase(args[0])) {
				setOpenEventMode(sender, args[1]);
				return true;
			} else if (giveSubCommand.equalsIgnoreCase(args[0])) {
				giveCleaningItem(sender, args[1]);
				return true;
			}
		}
		return false;
	}

	private void getConfig(CommandSender sender, String command) {
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
		case openEventSubCommand:
			key = openEventProperty;
			value = String.valueOf(PluginConfigManager.isOpenEvent());
			break;
		}
		MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, sender, key,
				value);
	}

	private void getCleaningItem(Player player) {
		if (!player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GET.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_CLEANING_ITEM_GET);
		} else {

			player.getInventory().addItem(PluginConfigManager.getCleaningItem());
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_CLEANITEM_YOU_GET, player);
		}
	}

	private void setCleaningItem(Player player) {

		if (!player.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_ADMIN_ITEM_SET);
		} else {

			ItemStack item = player.getInventory().getItemInMainHand().clone();

			if (item.getType() == Material.AIR) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_HOLD_ITEM, player);
			} else {

				ItemMeta itemMeta = item.getItemMeta();
				Damageable damageable = ((Damageable) itemMeta);
				damageable.setDamage(0);
				item.setItemMeta(itemMeta);
				item.setAmount(1);

				PluginConfigManager.setCleaningItem(item);
				MessageSystem.sendChangedValue(player, command, item.toString());
			}
		}
	}

	private void giveCleaningItem(CommandSender sender, String playerName) {
		if (!sender.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GIVE.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_CLEANING_ITEM_GIVE);
		} else {

			Player player2 = Bukkit.getPlayer(playerName);

			if (player2 != null) {
				player2.getInventory().addItem(PluginConfigManager.getCleaningItem());
				MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CLEANITEM_PLAYER_GET,
						sender, player2.getName());

			} else {
				if (playerName.equalsIgnoreCase("@a")) {
					Object[] players = Bukkit.getOnlinePlayers().toArray();
					for (Object p : players) {
						Player pl = (Player) p;
						pl.getInventory().addItem(PluginConfigManager.getCleaningItem());
						MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
								MessageID.INFO_CLEANITEM_PLAYER_GET, sender, pl.getName());
					}
				} else {

					MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_PLAYER_NOT_ONLINE,
							sender, playerName);
				}
			}
		}
	}

	private void setCleaningItemActive(CommandSender sender, String value) {
		if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_ACTIVE.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_ACTIVE);

		} else if (!StringUtils.isStringTrueOrFalse(value)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
		} else {
			boolean b = Boolean.parseBoolean(value);
			PluginConfigManager.setCleaningItemActive(b);
			MessageSystem.sendChangedValue(sender, activeProperty, String.valueOf(b));
		}
	}

	private void setDurabilityLoss(CommandSender sender, String value) {
		if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_DURABILITYLOSS.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_DURABILITYLOSS);
		} else if (!StringUtils.isStringTrueOrFalse(value)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
		} else {

			boolean b = Boolean.parseBoolean(value);
			PluginConfigManager.setDurabilityLossActive(b);
			MessageSystem.sendChangedValue(sender, durabilityProperty, String.valueOf(b));
		}
	}

	private void setOpenEventMode(CommandSender sender, String value) {
		if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_EVENT_MODE.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_EVENT_MODE);
		} else {
			boolean b = Boolean.parseBoolean(value);
			PluginConfigManager.setOpenEvent(b);
			MessageSystem.sendChangedValue(sender, openEventProperty, String.valueOf(b));
		}
	}

	private void setItemLore(CommandSender sender, String[] args) {
		if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_SET_LORE.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_SET_LORE);
		} else {

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

			MessageSystem.sendChangedValue(sender, loreProperty, lorelist.toString());
		}
	}

	private void setItemName(CommandSender sender, String[] args, Player player) {
		if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_ITEM_RENAME.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_ITEM_RENAME);
		} else {

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

			MessageSystem.sendChangedValue(sender, nameProperty, newname);
		}
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
					|| args[0].equalsIgnoreCase(openEventSubCommand)) {

				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			} else if (giveSubCommand.equalsIgnoreCase(args[0])) {
				StringUtil.copyPartialMatches(args[1], ChestCleaner.main.getServer().getOnlinePlayers().stream()
						.map(Player::getName).collect(Collectors.toList()), completions);
			}
		}

		Collections.sort(completions);
		return completions;
	}
}
