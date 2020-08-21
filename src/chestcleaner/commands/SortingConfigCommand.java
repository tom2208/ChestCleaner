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
 */
public class SortingConfigCommand implements CommandExecutor, TabCompleter {

	private final int MAX_LINES_PER_PAGE = 8;
	/* sub-commands */
	private final String autosortSubCommand = "autosort";
	private final String categoriesSubCommand = "categories";
	private final String patternSubCommand = "pattern";
	private final String chatNotificationSubCommand = "chatNotification";
	private final String sortingSoundSubCommand = "sortingSound";
	private final String resetSubCommand = "reset";
	private final String refillSubCommand = "refill";
	private final String clickSortSubCommand = "clickSort";
	
	private final String blocksSubCommand = "blocks";
	private final String consumablesSubCommand = "consumables";
	private final String breakablesSubCommand = "breakables";

	private final String autosortProperty = "autosort";
	private final String categoriesProperty = "categoryOrder";
	private final String patternProperty = "sortingpattern";
	private final String chatNotificationProperty = "chat sorting notification";
	private final String sortingSoundProperty = "sorting sound";

	private final String listSubCommand = "list";
	private final String setSubCommand = "set";
	private final String resetCategoriesSubCommand = "reset";

	private final String[] strCommandList = { autosortSubCommand, categoriesSubCommand, patternSubCommand,
			chatNotificationSubCommand, sortingSoundSubCommand, resetSubCommand, refillSubCommand, clickSortSubCommand};
	private final String[] categoriesSubCommandList = { listSubCommand, setSubCommand, resetCategoriesSubCommand };
	private final String[] refillSubCommandList = { blocksSubCommand, consumablesSubCommand, breakablesSubCommand,
			"true", "false" };

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
			if (resetSubCommand.equalsIgnoreCase(args[0])) {
				resetConfiguration(player);
				return true;
			}
			return getConfig(player, args[0]);
		}

		if (args.length == 2) {
			if (autosortSubCommand.equalsIgnoreCase(args[0])) {
				setAutoSort(player, args[1]);
			} else if (patternSubCommand.equalsIgnoreCase(args[0])) {
				setPattern(player, args[1]);
			} else if (chatNotificationSubCommand.equalsIgnoreCase(args[0])) {
				setChatNotificationBool(player, args[1]);
			} else if (sortingSoundSubCommand.equalsIgnoreCase(args[0])) {
				setSortingSoundBool(player, args[1]);
			} else if (refillSubCommand.equalsIgnoreCase(args[0])) {
				setAllRefills(player, args[1]);
			} else if (categoriesSubCommand.equalsIgnoreCase(args[0])
					&& resetCategoriesSubCommand.equalsIgnoreCase(args[1])) {
				resetCategories(player);
			}else if(clickSortSubCommand.equalsIgnoreCase(args[0])) {
				setClickSort(player, args[1]);
			}
			return true;
		}

		if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0])) {
				if (setSubCommand.equalsIgnoreCase(args[1])) {
					setCategories(player, args[2]);
				}
			} else if (refillSubCommand.equalsIgnoreCase(args[0])) {
				return setRefill(player, args[1], args[2]);
			}
			return true;

		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

		final List<String> completions = new ArrayList<>();

		if (args.length <= 1) {
			StringUtil.copyPartialMatches(args[0], Arrays.asList(strCommandList), completions);

		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase(autosortSubCommand) || args[0].equalsIgnoreCase(chatNotificationSubCommand)
					|| args[0].equalsIgnoreCase(sortingSoundSubCommand) || args[0].equalsIgnoreCase(clickSortSubCommand))
				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			else if (args[0].equalsIgnoreCase(categoriesSubCommand))
				StringUtil.copyPartialMatches(args[1], Arrays.asList(categoriesSubCommandList), completions);
			else if (args[0].equalsIgnoreCase(patternSubCommand))
				StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);
			else if (args[0].equalsIgnoreCase(refillSubCommand))
				StringUtil.copyPartialMatches(args[1], Arrays.asList(refillSubCommandList), completions);

		} else if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0]) && setSubCommand.equalsIgnoreCase(args[1])) {
				StringUtils.copyPartialMatchesCommasNoDuplicates(args[2], CategorizerManager.getAllNames(),
						completions);
			} else if (refillSubCommand.equalsIgnoreCase(args[0])) {
				if (args[1].equalsIgnoreCase(blocksSubCommand) || args[1].equalsIgnoreCase(consumablesSubCommand)
						|| args[1].equalsIgnoreCase(breakablesSubCommand)) {
					StringUtil.copyPartialMatches(args[2], StringUtils.getBooleanValueStringList(), completions);
				}
			}
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
		
		}else if (command.equalsIgnoreCase(clickSortSubCommand)) {
			key = clickSortSubCommand;
			value = String.valueOf(PlayerDataManager.isClickSort(p));
		}

		if (key != "" && value != "") {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, p, key,
					value);
			return true;
		}
		return false;
	}
	
	
	private void resetCategories(Player player) {
		if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES_RESET)) {
			PlayerDataManager.resetCategories(player);
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_CATEGORY_RESETED, player);
		}
	}

	
	
	private void setClickSort(Player player, String bool) {
		if(checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_CLICKSORT)) {
			if (!StringUtils.isStringTrueOrFalse(bool)) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
			} else {
				boolean b = Boolean.parseBoolean(bool);
				PlayerDataManager.setClickSort(player, b);
				MessageSystem.sendChangedValue(player, clickSortSubCommand, String.valueOf(b));
			}
		}
	}
	
	/**
	 * Sets the configuration for a refill option.
	 * 
	 * @param player the player who entered the command.
	 * @param arg    the subcommand string.
	 * @param bool   true: sets active, false: sets inactive
	 * @return True if the command can get parsed, otherwise false.
	 */
	private boolean setRefill(Player player, String arg, String bool) {
		if (StringUtils.isStringBoolean(player, bool)) {

			boolean b = Boolean.parseBoolean(bool);
			String property = new String();

			if (arg.equalsIgnoreCase(blocksSubCommand)) {
				PlayerDataManager.setRefillBlocks(player, b);
				property = blocksSubCommand;
			} else if (arg.equalsIgnoreCase(consumablesSubCommand)) {
				PlayerDataManager.setRefillConumables(player, b);
				property = consumablesSubCommand;
			} else if (arg.equalsIgnoreCase(breakablesSubCommand)) {
				PlayerDataManager.setRefillBreakables(player, b);
				property = breakablesSubCommand;
			} else {
				return false;
			}

			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, player,
					property, b);

		}

		return true;
	}

	private void setAllRefills(Player player, String bool) {
		if (!StringUtils.isStringTrueOrFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
		} else {
			boolean change = false;
			boolean b = Boolean.valueOf(bool);
			if (player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_REFILL_BLOCKS.getString())) {
				PlayerDataManager.setRefillBlocks(player, b);
				MessageSystem.sendChangedValue(player, blocksSubCommand, String.valueOf(b));
				change = true;
			}
			if (player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_REFILL_CONSUMABLES.getString())) {
				PlayerDataManager.setRefillConumables(player, b);
				MessageSystem.sendChangedValue(player, consumablesSubCommand, String.valueOf(b));
				change = true;
			}
			if (player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_REFILL_BREAKABLES.getString())) {
				PlayerDataManager.setRefillBreakables(player, b);
				;
				MessageSystem.sendChangedValue(player, breakablesSubCommand, String.valueOf(b));
				change = true;
			}

			if (!change) {
				MessageSystem.sendPermissionError(player, PluginPermissions.CMD_SORTING_CONFIG_REFILL_GENERIC);
			}

		}
	}

	private void resetConfiguration(Player player) {
		if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_RESET)) {
			PlayerDataManager.reset(player);
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_RESET_CONFIG, player);
		}
	}

	private void setChatNotificationBool(Player player, String bool) {
		if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_SET_NOTIFICATION_BOOL)) {
			if (!StringUtils.isStringTrueOrFalse(bool)) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
			} else {
				boolean b = Boolean.parseBoolean(bool);
				PlayerDataManager.setNotification(player, b);
				MessageSystem.sendChangedValue(player, chatNotificationProperty, String.valueOf(b));
			}
		}
	}

	private void setSortingSoundBool(Player player, String bool) {
		if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_SET_SOUND_BOOL)) {
			if (!StringUtils.isStringTrueOrFalse(bool)) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
			} else {
				boolean b = Boolean.parseBoolean(bool);
				PlayerDataManager.setSortingSound(player, b);
				MessageSystem.sendChangedValue(player, sortingSoundProperty, String.valueOf(b));
			}
		}
	}

	private void getCategoryList(CommandSender sender, String pageString) {
		List<String> names = CategorizerManager.getAllNames();
		MessageSystem.sendListPageToCS(names, sender, pageString, MAX_LINES_PER_PAGE);
	}

	private void setPattern(Player player, String patternName) {
		if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_PATTERN)) {

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
		if (checkPermission(player, PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT)) {
			if (!StringUtils.isStringTrueOrFalse(bool)) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, player);
			} else {

				boolean b = Boolean.parseBoolean(bool);
				PlayerDataManager.setAutoSort(player, b);
				MessageSystem.sendChangedValue(player, autosortProperty, String.valueOf(b));
			}
		}
	}

	private boolean checkPermission(Player player, PluginPermissions permission) {

		if (!player.hasPermission(permission.getString())) {
			MessageSystem.sendPermissionError(player, permission);
			return false;
		} else {
			return true;
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
