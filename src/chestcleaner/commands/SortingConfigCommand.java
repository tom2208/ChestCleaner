package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chestcleaner.sorting.v2.Categorizers;
import chestcleaner.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfig;
import chestcleaner.config.PluginConfig.ConfigPath;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

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

			/* PATTERN */
			if (args[0].equalsIgnoreCase(patternSubCommand)) {

				if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString());
					return true;
				}

				setPattern(player, args[1]);
				return true;

				/* EVALUATOR */
			} else if (args[0].equalsIgnoreCase(evaluatorSubCommand)) {

				if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_EVALUATOR.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_EVALUATOR.getString());
					return true;
				}

				setEvaluator(player, args[1]);
				return true;

				/* SETAUTOSORT */
			} else if (args[0].equalsIgnoreCase(setAutoSortSubCommand)) {

				if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString());
					return true;
				}

				setAutoSort(player, args[1]);
				return true;

			} else {
				sendSyntaxErrorMessage(player);
				return true;
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

				/* SETDEFAULTPATTERN */
				if (args[1].equalsIgnoreCase(setDefaultPatternSubCommand)) {

					setDefaultPattern(sender, args[2]);
					return true;

					/* SETDEFAULTEVALUATOR */
				} else if (args[1].equalsIgnoreCase(setDefaultEvaluatorSubCommand)) {

					setDefaultEvaluator(sender, args[2]);
					return true;

					/* SETDEFAULTAUTOSORTING */
				} else if (args[1].equalsIgnoreCase(setDefaultAutoSortSubCommand)) {

					setDefaultAutoSort(sender, args[2]);
					return true;

					/* SETDEFAULTCATEGORIES */
				} else if (args[1].equalsIgnoreCase(setDefaultCategoriesSubCommand)) {

					setDefaultCategories(sender, args[2]);
					return true;

				} else {
					MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, adminConfigSyntax, sender);
					return true;
				}

			} else {
				sendSyntaxErrorMessage(sender);
				return true;
			}

		} else {
			sendSyntaxErrorMessage(sender);
			return true;
		}

	}

	private void setDefaultAutoSort(CommandSender sender, String bool) {
		boolean b = false;

		if (PluginConfigManager.isStringTrue(bool)) {
			b = true;
		}
		if (!PluginConfigManager.isStringFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, adminConfigSetDefaultSortSyntax, sender);
		} else {

			PlayerDataManager.getInstance().setDefaultAutoSort(b);

			PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_AUTOSORT, b);
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, sender,
					String.valueOf(b));
		}
	}

	private void setDefaultEvaluator(CommandSender sender, String evaluatorName) {
		EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(evaluatorName);

		if (evaluator == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INAVLID_EVALUATOR_ID, sender);
		} else {

			PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_EVALUATOR, evaluator);

			EvaluatorType.DEFAULT = evaluator;
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_EVALUATOR_SET, sender);
		}
	}

	private void setDefaultCategories(CommandSender sender, String commaSeperatedCategories) {
		List<String> categories = Arrays.asList(commaSeperatedCategories.split(","));

		if (!Categorizers.validateExists(categories)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_CATEGORIZER_NAME, sender);
		} else {
			PluginConfig.getInstance().setIntoConfig(ConfigPath.CATEGORIES_ORDER, categories);
			PluginConfigManager.getInstance().setCategorizationOrder(categories);
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_CATEGORIES_SET, sender);
		}
	}

	private void setDefaultPattern(CommandSender sender, String patternName) {
		SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

		if (pattern == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, sender);
		} else {

			PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_SORTING_PATTERN, pattern);

			SortingPattern.DEFAULT = pattern;
			MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_PATTER_SET, sender);
		}
	}
	
	private void setEvaluator(Player player, String evaluatorName) {

		EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(evaluatorName);

		if (evaluator == null) {

			MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INAVLID_EVALUATOR_ID, player);

		} else {

			MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.EVALUATOR_SET, player);
			PluginConfig.getInstance().setEvaluatorTyp(evaluator, player);
			PlayerDataManager.getInstance().loadPlayerData(player);

		}

	}

	private void setPattern(Player player, String patternName) {

		SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

		if (pattern == null) {

			MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, player);

		} else {

			MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.PATTERN_SET, player);
			PluginConfig.getInstance().setSortingPattern(pattern, player);
			PlayerDataManager.getInstance().loadPlayerData(player);

		}
	}

	private boolean setAutoSort(Player player, String bool) {

		boolean b = false;
		if (PluginConfigManager.isStringTrue(bool)) {
			b = true;
		} else if (!PluginConfigManager.isStringFalse(bool)) {
			MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, setAutoSortSyntax, player);
			return true;
		}

		MessageSystem.sendMessageToPlayerWithReplacement(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, player,
				String.valueOf(b));
		PluginConfig.getInstance().setAutoSort(b, player);
		PlayerDataManager.getInstance().loadPlayerData(player);
		return true;
	}

	private void sendSyntaxErrorMessage(CommandSender sender) {
		MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntax, sender);
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

		final List<String> completions = new ArrayList<>();
		final String[] strCommandList = { patternSubCommand, evaluatorSubCommand, setAutoSortSubCommand,
				adminConfigSubCommand };
		final String[] strAdminControlSubCommands = { setDefaultPatternSubCommand, setDefaultEvaluatorSubCommand,
				setDefaultAutoSortSubCommand, setDefaultCategoriesSubCommand };
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
				StringUtil.copyPartialMatches(args[1], PluginConfigManager.getBooleanValueStringList(), completions);
			else if (args[0].equalsIgnoreCase(adminConfigSubCommand))
				StringUtil.copyPartialMatches(args[1], adminControlSubCommands, completions);

		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase(adminConfigSubCommand)) {
				if (args[1].equalsIgnoreCase(setDefaultPatternSubCommand))
					StringUtil.copyPartialMatches(args[2], SortingPattern.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(evaluatorSubCommand))
					StringUtil.copyPartialMatches(args[2], EvaluatorType.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(setDefaultAutoSortSubCommand))
					StringUtil.copyPartialMatches(args[2], PluginConfigManager.getBooleanValueStringList(),
							completions);
				else if (args[1].equalsIgnoreCase(setDefaultCategoriesSubCommand))
					MessageUtils.copyPartialMatchesCommasNoDuplicates(args[2], Categorizers.getAllNames(), completions);
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

	/* Syntax Error */

	private final String syntax = "/sortingconfig <pattern/evaluator/setautosort/adminconfig>";
	private final String adminConfigSyntax = "/sortingconfig adminconfig <setdefaultpattern/setdefaultevaluator/setdefaultautosort/setdefaultcategories>"; // alias
																																		// ac
																																		// for
																																		// adminconfig
	private final String setAutoSortSyntax = "/sortingconfig setautosort <true/false>";
	private final String adminConfigSetDefaultSortSyntax = "/sortingconfig adminconfig setdefaultautosort <true/false>";

}
