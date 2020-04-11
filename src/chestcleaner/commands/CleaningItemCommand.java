package chestcleaner.commands;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.StringUtils;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Bukkit;
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

/**
 * A command class representing the CleaningItem command. CleaningItem Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-cleaningitem
 * 
 * @author Tom2208
 *
 */
public class CleaningItemCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (args.length > 1) {

			/* RENAME SUBCOMMAND */
			if (args[0].equalsIgnoreCase(renameSubCommand)) {

				if (player != null) {
					if (!player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_RENAME.getString())) {
						MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_CLEANING_ITEM_RENAME.getString(), player);
						return true;
					}
				}

				String newname = new String();
				for (int i = 1; i < args.length; i++) {

					if (i == 1)
						newname = args[1];
					else
						newname = newname + " " + args[i];

				}

				newname = newname.replace("&", "\u00A7");
				MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.SET_CLEANING_ITEM_NAME,
						sender, newname);

				ItemStack is = PluginConfigManager.getCleaningItem();
				is.getItemMeta().setDisplayName(newname);
				PluginConfigManager.setCleaningItem(is);

				return true;

				/* SETLORE SUBCOMMAND */
			} else if (args[0].equalsIgnoreCase(setLoreSubCommand)) {

				if (player != null) {
					if (!player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_LORE.getString())) {
						MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_CLEANING_ITEM_SET_LORE.getString(), player);
						return true;
					}

				}

				String lore = args[1];
				for (int i = 2; i < args.length; i++) {
					lore = lore + " " + args[i];
				}

				String[] lorearray = lore.split("/n");

				ArrayList<String> lorelist = new ArrayList<>();

				for (String obj : lorearray) {
					obj = obj.replace("&", "\u00A7");
					lorelist.add(obj);

				}

				ItemStack is = PluginConfigManager.getCleaningItem();
				is.getItemMeta().setLore(lorelist);
				PluginConfigManager.setCleaningItem(is);

				MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.SET_CLEANING_ITEM_LORE, sender);
				return true;

			}

		}

		if (args.length == 1) {

			/* RENAME SUBCOMMAND ERRORS */
			if (args[0].equalsIgnoreCase(renameSubCommand)) {
				MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, renameSytaxError, sender);
				return true;
			}

			/* SETITEM SUBCOMMAND */
			else if (args[0].equalsIgnoreCase(setItemSubCommand)) {

				if (player == null) {
					MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER);
					return true;
				}

				if (player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_ITEM.getString())) {

					ItemStack item = player.getInventory().getItemInMainHand().clone();
					if (item != null) {
						ItemMeta itemMeta = item.getItemMeta();
						Damageable damageable = ((Damageable) itemMeta);
						damageable.setDamage(0);
						item.setItemMeta(itemMeta);
						item.setAmount(1);

						PluginConfigManager.setCleaningItem(item);
						MessageSystem.sendMessageToPlayerWithReplacement(MessageType.SUCCESS,
								MessageID.SET_CLEANING_ITEM, player, item.toString());

						return true;

					} else {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.YOU_HAVE_TO_HOLD_AN_ITEM,
								player);
						return true;
					}
				} else {
					MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_CLEANING_ITEM_SET_ITEM.getString(), player);
					return true;
				}

				/* GET SUBCOMMAND */
			} else if (args[0].equalsIgnoreCase(getSubCommand)) {

				if (player == null) {
					MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER);
					return true;
				}

				if (player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GET.getString())) {

					player.getInventory().addItem(PluginConfigManager.getCleaningItem());
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.YOU_GOT_CLEANING_ITEM, player);
					return true;

				} else {
					MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_CLEANING_ITEM_GET.getString(), player);
					return true;
				}

			}else {
				MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, subCommandsSyntaxError, sender);
				return true;
			}

		} else if (args.length == 2) {

			/* SETACTIVE SUBCOMMAND */
			if (args[0].equalsIgnoreCase(setActiveSubCommand)) {

				if (player != null) {

					if (player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_ACTIVE.getString())) {
						MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_CLEANING_ITEM_SET_ACTIVE.getString(), sender);
						return true;
					}

				}

				if (StringUtils.isStringTrueOrFalse(args[1])) {

					boolean b = Boolean.parseBoolean(args[1]);

					PluginConfigManager.setCleaningItemActive(b);

					MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.CLEANING_ITEM_TOGGLED,
							sender, String.valueOf(b));

					return true;

				} else {
					MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, setActiveSyntaxError, sender);
					return true;
				}

				/* SETDURIBILITYLOSS SUBCOMMAND */
			} else if (args[0].equalsIgnoreCase(setDurabilityLossSubCommand)) {

				if (player != null) {
					if (player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_DURABILITYLOSS.getString())) {
						MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_CLEANING_ITEM_SET_DURABILITYLOSS.getString(), player);
						return true;
					}
				}

				if (StringUtils.isStringTrueOrFalse(args[1])) {

					boolean b = Boolean.parseBoolean(args[1]);

					PluginConfigManager.setDurabilityLossActive(b);
					if (PluginConfigManager.isDurabilityLossActive()) {
						MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DURABILITYLOSS_AVTIVATED, sender);
					} else {
						MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DURABILITYLOSS_DEACTIVATED,
								sender);
					}
					return true;

				} else {
					MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, setDurabilityLossSyntaxError, sender);
					return true;
				}

				/* GIVE SUBCOMMAND */
			} else if (args[0].equalsIgnoreCase(giveSubCommand)) {

				if (player != null) {
					if (player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_GIVE.getString())) {
						MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_CLEANING_ITEM_GIVE.getString(), player);
						return true;
					}
				}

				Player player2 = Bukkit.getPlayer(args[1]);

				if (player2 != null) {

					player2.getInventory().addItem(PluginConfigManager.getCleaningItem());
					MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
							MessageID.PLAYER_GOT_CLEANING_ITEM, sender, player2.getName());

					return true;

				} else {

					if (args[1].equalsIgnoreCase("@a")) {

						Object[] players = Bukkit.getOnlinePlayers().toArray();

						for (Object p : players) {
							Player pl = (Player) p;
							pl.getInventory().addItem(PluginConfigManager.getCleaningItem());
							MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
									MessageID.PLAYER_GOT_CLEANING_ITEM, sender, pl.getName());

						}
						return true;
					}

					MessageSystem.sendMessageToPlayerWithReplacement(MessageType.ERROR, MessageID.PLAYER_NOT_ONLINE,
							player2, args[1]);

					return true;
				}

				/* SETEVENTDETECTIONMODE SUBCOMMAND */
			} else if (args[0].equalsIgnoreCase(setEventDetectionModeSubCommand)) {

				if (player != null) {
					if (player.hasPermission(PluginPermissions.CMD_CLEANING_ITEM_SET_EVENT_MODE.getString())) {
						MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_CLEANING_ITEM_SET_EVENT_MODE.getString(), player);
						return true;

					}
				}
				boolean b = Boolean.parseBoolean(args[1]);
				PluginConfigManager.setEventModeActive(b);
				MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.OPEN_INV_MODE_TOGGLED,
						sender, String.valueOf(b));

				return true;

			} else {
				MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, subCommandsSyntaxError, sender);
				return true;
			}

		} else {
			MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, subCommandsSyntaxError, sender);
			return true;
		}

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();
		final String[] strCleaningitemCommands = { renameSubCommand, setLoreSubCommand, setItemSubCommand,
				getSubCommand, setActiveSubCommand, setDurabilityLossSubCommand, giveSubCommand,
				setEventDetectionModeSubCommand };
		final List<String> cleaningItemCommands = Arrays.asList(strCleaningitemCommands);

		switch (args.length) {
		case 0:
			StringUtil.copyPartialMatches(args[0], cleaningItemCommands, completions);
			break;
		case 1:
			StringUtil.copyPartialMatches(args[0], cleaningItemCommands, completions);
			break;
		case 2:
			if (args[0].equalsIgnoreCase(setActiveSubCommand) || args[0].equalsIgnoreCase(setDurabilityLossSubCommand)
					|| args[0].equalsIgnoreCase(setEventDetectionModeSubCommand)) {

				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			}

		}

		Collections.sort(completions);
		return completions;
	}

	/* sub-commands */
	private final String renameSubCommand = "rename";
	private final String setLoreSubCommand = "setLore";
	private final String setItemSubCommand = "setItem";
	private final String getSubCommand = "get";
	private final String setActiveSubCommand = "setActive";
	private final String setDurabilityLossSubCommand = "setDurabilityLoss";
	private final String giveSubCommand = "give";
	private final String setEventDetectionModeSubCommand = "setEventDetectionMode";

	/* Syntax Error Messages */
	private final String renameSytaxError = "/cleaningitem rename <name>";
	private final String setActiveSyntaxError = "/cleaningitem setactive <true/false>";
	private final String setDurabilityLossSyntaxError = "/cleaningitem setDurabilityLoss <true/false>";
	private final String subCommandsSyntaxError = "/cleaningitem <setitem/setactive/setdurabilityLoss/get/give/rename/setlore/seteventdetectionmode>";

}
