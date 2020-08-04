package chestcleaner.commands;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.CategorizerManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.StringUtils;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A command class representing the SortingConfig command. SortingConfig Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-sortingconfig
 *
 */
public class SortingConfigCommand implements CommandExecutor, TabCompleter {

	private final int MAX_LINES_PER_PAGE = 8;
	/* sub-commands */
	private final String autosortSubCommand = "autosort";
	private final String categoriesSubCommand = "categories";
	private final String patternSubCommand = "pattern";
	private final String chatNotificationSubCommand = "chatNotification";
	private final String sortingSoundSubCommand = "sortingSound";

	private final String autosortProperty = "default autosort";
	private final String categoriesProperty = "default categoryOrder";
	private final String patternProperty = "default sortingpattern";
	private final String chatNotificationProperty = "chat sorting notification";
	private final String sortingSoundProperty = "sorting sound";

	private final String listSubCommand = "list";
	private final String setSubCommand = "set";

	private final String[] strCommandList = { autosortSubCommand, categoriesSubCommand, patternSubCommand,
			chatNotificationSubCommand, sortingSoundSubCommand };
	private final String[] categoriesSubCommandList = { listSubCommand, setSubCommand };

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (player == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
			return true;
		}
		
		if (args.length >= 2 && categoriesSubCommand.equalsIgnoreCase(args[0])
				&& listSubCommand.equalsIgnoreCase(args[1])) {
			getCategoryList(sender, args.length > 2 ? args[2] : "1");
			return true;
		}

		if (args.length == 1) {
			return getConfig(player, args[0]);
		}

		if (args.length == 2) {
			if (autosortSubCommand.equalsIgnoreCase(args[0])) {
				setAutoSort(player, args[1]);
				return true;

			} else if (patternSubCommand.equalsIgnoreCase(args[0])) {
				setPattern(player, args[1]);
				return true;

			} else if (chatNotificationSubCommand.equalsIgnoreCase(args[0])) {
				setChatNotificationBool(player, args[1]);
				return true;

			} else if (sortingSoundSubCommand.equalsIgnoreCase(args[0])) {
				setSortingSoundBool(player, args[1]);
				return true;
			}
		}

		if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0]) && setSubCommand.equalsIgnoreCase(args[1])) {
				setCategories(player, args[2]);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

		final List<String> completions = new ArrayList<>();
		final List<String> commandList = Arrays.asList(strCommandList);
		final List<String> categoriesSubCommands = Arrays.asList(categoriesSubCommandList);

		if (args.length <= 1) {
			StringUtil.copyPartialMatches(args[0], commandList, completions);

		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase(autosortSubCommand) || args[0].equalsIgnoreCase(chatNotificationSubCommand) || args[0].equalsIgnoreCase(sortingSoundSubCommand))
				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			else if (args[0].equalsIgnoreCase(categoriesSubCommand))
				StringUtil.copyPartialMatches(args[1], categoriesSubCommands, completions);
			else if (args[0].equalsIgnoreCase(patternSubCommand))
				StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);

		} else if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0]) && setSubCommand.equalsIgnoreCase(args[1]))
				StringUtils.copyPartialMatchesCommasNoDuplicates(args[2], CategorizerManager.getAllNames(),
						completions);
		}
		return completions;
	}

	private boolean getConfig(Player p, String command) {
		String key = "";
		String value = "";

		if (command.equalsIgnoreCase(autosortSubCommand)) {
			key = autosortProperty;
			value = String.valueOf(PlayerDataManager.isAutoSort(p));

		} else if (command.equalsIgnoreCase(categoriesSubCommand)) {
			key = categoriesProperty;
			value = PlayerDataManager.getCategoryOrder(p).toString();

		} else if (command.equalsIgnoreCase(patternSubCommand)) {
			key = patternProperty;
			value = PlayerDataManager.getSortingPattern(p).name();

		} else if (command.equalsIgnoreCase(chatNotificationSubCommand)) {
			key = chatNotificationProperty;
			value = String.valueOf(PlayerDataManager.isNotification(p));

		} else if (command.equalsIgnoreCase(sortingSoundSubCommand)) {
			key = sortingSoundProperty;
			value = String.valueOf(PlayerDataManager.isSortingSound(p));

		}
		if(key != "" && value != "") {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, p, key, value);
			return true;
		}
		return false;
	}

	private void setChatNotificationBool(Player player, String bool) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_NOTIFICATION_BOOL.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_SET_NOTIFICATION_BOOL);
		} else if (!StringUtils.isStringTrueOrFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
		} else {
			boolean b = Boolean.parseBoolean(bool);
			PlayerDataManager.setNotification(player, b);
			MessageSystem.sendChangedValue(player, chatNotificationProperty, String.valueOf(b));
		}
	}

	private void setSortingSoundBool(Player player, String bool) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_SOUND_BOOL.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_SET_SOUND_BOOL);
		} else if (!StringUtils.isStringTrueOrFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
		} else {
			boolean b = Boolean.parseBoolean(bool);
			PlayerDataManager.setNotification(player, b);
			MessageSystem.sendChangedValue(player, sortingSoundProperty, String.valueOf(b));
		}
	}

	private void getCategoryList(CommandSender sender, String pageString) {
		List<String> names = CategorizerManager.getAllNames();
		MessageSystem.sendListPageToCS(names, sender, pageString, MAX_LINES_PER_PAGE);
	}

	private void setPattern(Player player, String patternName) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_PATTERN);
		} else {

			SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);
			if (pattern != null) {
				PlayerDataManager.setSortingPattern(player, pattern);
				MessageSystem.sendChangedValue(player, patternProperty, pattern.name());
			} else {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_PATTERN_ID, player);
			}
		}
	}

	private void setAutoSort(Player player, String bool) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT);
		} else if (!StringUtils.isStringTrueOrFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
		} else {

			boolean b = Boolean.parseBoolean(bool);
			PlayerDataManager.setAutoSort(player, b);
			MessageSystem.sendChangedValue(player, autosortProperty, String.valueOf(b));
		}
	}

	private void setCategories(Player player, String commaSeperatedCategories) {
		List<String> categories = Arrays.asList(commaSeperatedCategories.split(","));

		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES);
		} else if (!CategorizerManager.validateExists(categories)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, player);
		} else {
			PlayerDataManager.setCategoryOrder(player, categories);
			MessageSystem.sendChangedValue(player, categoriesProperty, categories.toString());
		}
	}
}
