package chestcleaner.commands;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.config.serializable.Category;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;
import chestcleaner.sorting.v2.CategorizerManager;
import chestcleaner.utils.StringUtils;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.StringUtil;

import java.io.StringReader;
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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (player == null) {
			if (args.length != 3) {
				MessageSystem.sendConsoleMessage(MessageType.SYNTAX_ERROR, adminConfigSyntax);
				return true;
			}
		}

		if (args.length == 2) {
			switch (args[0]) {
				case patternSubCommand:
					return setPattern(player, args[1]);
				case setAutoSortSubCommand:
					return setAutoSort(player, args[1]);
				case evaluatorSubCommand:
					return setEvaluator(player, args[1]);
				default:
					return sendSyntaxErrorMessage(player);
			}

		} else if (args.length == 3) {

			/* ADMINCONFIG */
			if (args[0].equalsIgnoreCase(adminConfigSubCommand)) {

				if (player != null) {
					if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString())) {
						MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString(), sender);
						return true;
					}
				}
				switch (args[1]) {
					case setDefaultPatternSubCommand:
						return setDefaultPattern(sender, args[2]);
					case setDefaultAutoSortSubCommand:
						return setDefaultAutoSort(sender, args[2]);
					case setDefaultCategoriesSubCommand:
						return setDefaultCategories(sender, args[2]);
					case addCategorySubCommand:
						return addCategory(sender, player, args[2]);
					default:
						MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, adminConfigSyntax, sender);
						return true;
				}
			} else {
				return sendSyntaxErrorMessage(sender);
			}
		} else {
			return sendSyntaxErrorMessage(sender);
		}
	}

	private boolean setDefaultAutoSort(CommandSender sender, String bool) {
		if (StringUtils.isStringTrueOrFalse(bool)) {
			boolean b = Boolean.parseBoolean(bool);

			PluginConfigManager.setDefaultAutoSort(b);

			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, sender,
					String.valueOf(b));
		} else {
			MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, adminConfigSetDefaultSortSyntax, sender);
		}
		return true;
	}

	private boolean setDefaultPattern(CommandSender sender, String patternName) {
		SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

		if (pattern == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, sender);
		} else {
			PluginConfigManager.setDefaultPattern(pattern);
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_PATTER_SET, sender);
		}
		return true;
	}

	private boolean setDefaultCategories(CommandSender sender, String commaSeperatedCategories) {
		List<String> categories = Arrays.asList(commaSeperatedCategories.split(","));

		if (!CategorizerManager.validateExists(categories)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_CATEGORIZER_NAME, sender);
		} else {
			PluginConfigManager.setCategorizationOrder(categories);
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_CATEGORIES_SET, sender);
		}
		return true;
	}


	private boolean addCategory(CommandSender sender, Player player, String type) {
		if (player == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_CATEGORIZER_NAME, sender);
		} else {
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			if (itemInHand.getType().equals(Material.WRITABLE_BOOK) || itemInHand.getType().equals(Material.WRITTEN_BOOK)) {
				try {
					parseBook(((BookMeta)itemInHand.getItemMeta()).getPages());
				} catch (IllegalArgumentException e) {
					MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
							MessageID.INVALID_CATEGORIZER_NAME, sender, e.getMessage());
				}
				MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_CATEGORIES_SET, sender);
			}
		}
		return true;
	}

	private void parseBook(List<String> pages) throws IllegalArgumentException {

		if (pages.isEmpty() || pages.get(0).isEmpty()) {
			throw new IllegalArgumentException("Page is empty");
		}

		String allPages = String.join("\n", pages);
		String[] allLines = allPages.split("\n");
		for (int i = 0; i < allLines.length; i++) {
			allLines[i] = "  " + allLines[i].replace("§0","");
		}
		allPages = "category:\n" + String.join("\n", allLines);

		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new StringReader(allPages));
		Object category = yamlConfiguration.get("category");
		CategorizerManager.addCategory((Category<?>) category);
		// add to config
	}
	
	private boolean setEvaluator(Player player, String evaluatorName) {
		return true;
	}

	private boolean setPattern(Player player, String patternName) {

		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString())) {
			MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
					PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString());
			return true;
		}

		SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

		if (pattern == null) {
			MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, player);
		} else {
			MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.PATTERN_SET, player);
			PlayerDataManager.setSortingPattern(player, pattern);

		}
		return true;
	}

	private boolean setAutoSort(Player player, String bool) {
		if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString())) {
			MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
					PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString());

		} else if (StringUtils.isStringTrueOrFalse(bool)) {
			boolean b = Boolean.parseBoolean(bool);
			PlayerDataManager.setAutoSort(player, b);
			MessageSystem.sendMessageToPlayerWithReplacement(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, player,
					String.valueOf(b));
		} else {
			MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, setAutoSortSyntax, player);
		}
		return true;
	}

	private boolean sendSyntaxErrorMessage(CommandSender sender) {
		MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntax, sender);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

		final List<String> completions = new ArrayList<>();
		final List<String> commandList = Arrays.asList(strCommandList);
		final List<String> adminControlSubCommands = Arrays.asList(strAdminControlSubCommands);

		if (args.length <= 1) {

			StringUtil.copyPartialMatches(args[0], commandList, completions);

		} else if (args.length == 2) {

			if (args[0].equalsIgnoreCase(patternSubCommand))
				StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);
			else if (args[0].equalsIgnoreCase(evaluatorSubCommand))
				StringUtil.copyPartialMatches(args[1], EvaluatorType.getIDList(), completions);
			else if (args[0].equalsIgnoreCase(setAutoSortSubCommand))
				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			else if (args[0].equalsIgnoreCase(adminConfigSubCommand))
				StringUtil.copyPartialMatches(args[1], adminControlSubCommands, completions);

		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase(adminConfigSubCommand)) {
				if (args[1].equalsIgnoreCase(setDefaultPatternSubCommand))
					StringUtil.copyPartialMatches(args[2], SortingPattern.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(evaluatorSubCommand))
					StringUtil.copyPartialMatches(args[2], EvaluatorType.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(setDefaultAutoSortSubCommand))
					StringUtil.copyPartialMatches(args[2], StringUtils.getBooleanValueStringList(),
							completions);
				else if (args[1].equalsIgnoreCase(setDefaultCategoriesSubCommand))
					StringUtils.copyPartialMatchesCommasNoDuplicates(args[2], CategorizerManager.getAllNames(), completions);
			}
		}

		return completions;

	}

	/* sub-commands */
	private final String patternSubCommand = "pattern";
	private final String evaluatorSubCommand = "evaluator";
	private final String setAutoSortSubCommand = "setAutoSort";
	private final String adminConfigSubCommand = "adminConfig";

	private final String setDefaultPatternSubCommand = "setDefaultPattern";
	private final String setDefaultEvaluatorSubCommand = "setDefaultEvaluator";
	private final String setDefaultAutoSortSubCommand = "setDefaultAutosort";
	private final String setDefaultCategoriesSubCommand = "setDefaultCategories";
	private final String addCategorySubCommand = "addCategory";

	private final String[] strCommandList = { patternSubCommand, evaluatorSubCommand, setAutoSortSubCommand,
			adminConfigSubCommand };
	private final String[] strAdminControlSubCommands = { setDefaultPatternSubCommand, setDefaultEvaluatorSubCommand,
			setDefaultAutoSortSubCommand, setDefaultCategoriesSubCommand, addCategorySubCommand};
	/* Syntax Error */

	private final String syntax = "/sortingconfig <" + String.join("/", strCommandList) + ">";
	private final String adminConfigSyntax = "/sortingconfig adminconfig <" + String.join("/", strAdminControlSubCommands) + ">"; // alias
																																		// ac
																																		// for
																																		// adminconfig
	private final String setAutoSortSyntax = "/sortingconfig " + setAutoSortSubCommand + " <true/false>";
	private final String adminConfigSetDefaultSortSyntax = "/sortingconfig adminconfig setDefaultAutosort <true/false>";

}
