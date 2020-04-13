package chestcleaner.commands;

import chestcleaner.config.PlayerDataManager;
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
 * @author Tom2208
 *
 */
public class SortingConfigCommand implements CommandExecutor, TabCompleter {

    private final int MAX_LINES_PER_PAGE = 8;
	/* sub-commands */
	private final String autosortSubCommand = "autosort";
	private final String categoriesSubCommand = "categories";
	private final String patternSubCommand = "pattern";

	private final String listSubCommand = "list";
	private final String setSubCommand = "set";

	private final String[] strCommandList = { autosortSubCommand, categoriesSubCommand, patternSubCommand };
	private final String[] categoriesSubCommandList = { listSubCommand, setSubCommand };
	/* Syntax Error */

	private final String syntax = "/sortingconfig <" + String.join("/", strCommandList) + ">";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (categoriesSubCommand.equalsIgnoreCase(args[0]) && listSubCommand.equalsIgnoreCase(args[1])) {
			return getCategoryList(sender, args.length > 2 ? args[2] : "1");
		}

		if (player == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER, sender);
			return true;
		}

		if (args.length == 1) {
			if (autosortSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(player, autosortSubCommand);
			} else if (categoriesSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(player, categoriesSubCommand);
			} else if (patternSubCommand.equalsIgnoreCase(args[0])) {
				return getConfig(player, patternSubCommand);
			}
		}
		if (args.length == 2) {
			if (autosortSubCommand.equalsIgnoreCase(args[0])) {
				return setAutoSort(player, args[1]);
			} else if (patternSubCommand.equalsIgnoreCase(args[0])) {
				return setPattern(player, args[1]);
			}
		}
		if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0])
					&& setSubCommand.equalsIgnoreCase(args[1])) {
				return setCategories(player, args[2]);
			}
		}
		MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntax, sender);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

		final List<String> completions = new ArrayList<>();
		final List<String> commandList = Arrays.asList(strCommandList);
		final List<String> categoriesSubCommands = Arrays.asList(categoriesSubCommandList);

		if (args.length <= 1) {
			StringUtil.copyPartialMatches(args[0], commandList, completions);

		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase(autosortSubCommand))
				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			else if (args[0].equalsIgnoreCase(categoriesSubCommand))
				StringUtil.copyPartialMatches(args[1], categoriesSubCommands, completions);
			else if (args[0].equalsIgnoreCase(patternSubCommand))
				StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);

		} else if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0])
					&& setSubCommand.equalsIgnoreCase(args[1]))
				StringUtils.copyPartialMatchesCommasNoDuplicates(args[2], CategorizerManager.getAllNames(), completions);
		}
		return completions;
	}

	private boolean getConfig(Player p, String command) {
		String key = "";
		String value = "";
		switch (command) {
			case autosortSubCommand:
				key = "autosort";
				value = String.valueOf(PlayerDataManager.isAutoSort(p));
				break;
			case categoriesSubCommand:
				key = "categoriesOrder";
				value = PlayerDataManager.getCategoryOrder(p).toString();
				break;
			case patternSubCommand:
				key = "sortingpattern";
				value = PlayerDataManager.getSortingPattern(p).name();
				break;
		}
		MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.CURRENT_VALUE, p, key, value);
		return true;
	}

	private boolean getCategoryList(CommandSender sender, String pageString) {
		List<String> names = CategorizerManager.getAllNames();
		MessageSystem.sendListPageToCS(names, sender, pageString, MAX_LINES_PER_PAGE);
		return true;
	}

	private boolean setPattern(Player player, String patternName) {

		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString())) {
			MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
					PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString(), player);
			return true;
		}

		SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

		if (pattern != null) {
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.PATTERN_SET, player);
			PlayerDataManager.setSortingPattern(player, pattern);
		} else {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, player);
		}
		return true;
	}

	private boolean setAutoSort(Player player, String bool) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString())) {
			MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
					PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString(), player);

		} else if (StringUtils.isStringTrueOrFalse(bool)) {
			boolean b = Boolean.parseBoolean(bool);
			PlayerDataManager.setAutoSort(player, b);
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, player,
					String.valueOf(b));
		} else {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.NOT_A_BOOLEAN, player);
		}
		return true;
	}

	private boolean setCategories(Player player, String commaSeperatedCategories) {
		List<String> categories = Arrays.asList(commaSeperatedCategories.split(","));

		if (CategorizerManager.validateExists(categories)) {
			PlayerDataManager.setCategoryOrder(player, categories);
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.CATEGORY_ORDER_SET, player);
		} else {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_CATEGORY_NAME, player);
		}
		return true;
	}

}
