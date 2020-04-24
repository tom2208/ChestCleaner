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

	private final String autosortProperty = autosortSubCommand;
	private final String categoriesProperty = "categoryOrder";
	private final String patternProperty = "sortingpattern";

	private final String listSubCommand = "list";
	private final String setSubCommand = "set";

	private final String[] strCommandList = { autosortSubCommand, categoriesSubCommand, patternSubCommand };
	private final String[] categoriesSubCommandList = { listSubCommand, setSubCommand };

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (args.length >= 2
				&& categoriesSubCommand.equalsIgnoreCase(args[0])
				&& listSubCommand.equalsIgnoreCase(args[1])) {
			return getCategoryList(sender, args.length > 2 ? args[2] : "1");
		}

		if (player == null) {
			return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
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
				key = autosortProperty;
				value = String.valueOf(PlayerDataManager.isAutoSort(p));
				break;
			case categoriesSubCommand:
				key = categoriesProperty;
				value = PlayerDataManager.getCategoryOrder(p).toString();
				break;
			case patternSubCommand:
				key = patternProperty;
				value = PlayerDataManager.getSortingPattern(p).name();
				break;
		}
		return MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, p, key, value);
	}

	private boolean getCategoryList(CommandSender sender, String pageString) {
		List<String> names = CategorizerManager.getAllNames();
		return MessageSystem.sendListPageToCS(names, sender, pageString, MAX_LINES_PER_PAGE);
	}

	private boolean setPattern(Player player, String patternName) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString())) {
			return MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_PATTERN);
		}

		SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);
		if (pattern != null) {
			PlayerDataManager.setSortingPattern(player, pattern);
			return MessageSystem.sendChangedValue(player, patternProperty, pattern.name());
		}
		return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_PATTERN_ID, player);
	}

	private boolean setAutoSort(Player player, String bool) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString())) {
			MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT);
		} else if (!StringUtils.isStringTrueOrFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
		}

		boolean b = Boolean.parseBoolean(bool);
		PlayerDataManager.setAutoSort(player, b);
		return MessageSystem.sendChangedValue(player, autosortProperty, String.valueOf(b));
	}

	private boolean setCategories(Player player, String commaSeperatedCategories) {
		List<String> categories = Arrays.asList(commaSeperatedCategories.split(","));

		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES.getString())) {
			return MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES);
		} else if (!CategorizerManager.validateExists(categories)) {
			return MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, player);
		}
		PlayerDataManager.setCategoryOrder(player, categories);
		return MessageSystem.sendChangedValue(player, categoriesProperty, categories.toString());
	}
}
