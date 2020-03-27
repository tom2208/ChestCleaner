package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.config.PluginConfig;
import chestcleaner.config.PluginConfig.ConfigPath;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.PlayerDataManager;
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
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		Player player = null;
		if(cs instanceof Player) {
			player = (Player) cs;
		}
		
		if(player == null) {
			if(args.length != 3) {
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

				SortingPattern pattern = SortingPattern.getSortingPatternByName(args[1]);

				if (pattern == null) {

					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, player);
					return true;

				} else {

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.PATTERN_SET, player);
					PluginConfig.getInstance().setSortingPattern(pattern, player);
					PlayerDataManager.getInstance().loadPlayerData(player);
					return true;

				}

				/* EVALUATOR */
			} else if (args[0].equalsIgnoreCase(evaluatorSubCommand)) {

				if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_EVALUATOR.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_EVALUATOR.getString());
					return true;
				}

				EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(args[1]);

				if (evaluator == null) {

					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INAVLID_EVALUATOR_ID, player);
					return true;

				} else {

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.EVALUATOR_SET, player);
					PluginConfig.getInstance().setEvaluatorTyp(evaluator, player);
					PlayerDataManager.getInstance().loadPlayerData(player);
					return true;

				}

				/* SETAUTOSORT */
			} else if (args[0].equalsIgnoreCase(setAutoSortSubCommand)) {

				if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString());
					return true;
				}

				boolean b = false;
				if (PluginConfigManager.isStringTrue(args[1])) {
					b = true;
				} else if (!PluginConfigManager.isStringFalse(args[1])) {
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, setAutoSortSyntax, player);
					return true;
				}

				MessageSystem.sendMessageToPlayerWithReplacement(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, player,
						String.valueOf(b));
				PluginConfig.getInstance().setAutoSort(b, player);
				PlayerDataManager.getInstance().loadPlayerData(player);
				return true;

			} else {
				sendSyntaxErrorMessage(player);
				return true;
			}

		} else if (args.length == 3) {

			/* ADMINCONFIG */
			if (args[0].equalsIgnoreCase(adminConfigSubCommand)) {
				
				if(player != null) {
					if (!player.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString())) {
						MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
								PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString(), cs);
						return true;
					}
				}
					
				/* SETDEFAULTPATTERN */
				if (args[1].equalsIgnoreCase(setDefaultPatternSubCommand)) {

					SortingPattern pattern = SortingPattern.getSortingPatternByName(args[2]);

					if (pattern == null) {
						MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, cs);
						return true;
					}

					PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_SORTING_PATTERN, pattern);

					SortingPattern.DEFAULT = pattern;
					MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_PATTER_SET, cs);
					return true;

					/* SETDEFAULTEVALUATOR */
				} else if (args[1].equalsIgnoreCase(setDefaultEvaluatorSubCommand)) {

					EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(args[2]);

					if (evaluator == null) {
						MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.INAVLID_EVALUATOR_ID, cs);
						return true;
					}

					PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_EVALUATOR, evaluator);

					EvaluatorType.DEFAULT = evaluator;
					MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.DEFAULT_EVALUATOR_SET, cs);
					return true;

					/* SETDEFAULTAUTOSORTING */
				} else if (args[1].equalsIgnoreCase(setDefaultAutoSortSubCommand)) {

					Boolean b = false;

					if (PluginConfigManager.isStringTrue(args[2])) {
						b = true;
					} else if (!PluginConfigManager.isStringFalse(args[2])) {
						MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, adminConfigSetDefaultSortSyntax, cs);
						return true;
					}

					PlayerDataManager.getInstance().setDefaultAutoSort(b);

					PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_AUTOSORT, b);
					MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
							MessageID.AUTOSORTING_TOGGLED, cs, String.valueOf(b));

					return true;

				} else {
					MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, adminConfigSyntax, cs);
					return true;
				}

			} else {
				sendSyntaxErrorMessage(cs);
				return true;
			}

		} else {
			sendSyntaxErrorMessage(cs);
			return true;
		}

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
				setDefaultAutoSortSubCommand };
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
			if (args[0].equalsIgnoreCase(commandList.get(3))) {
				if (args[1].equalsIgnoreCase(setDefaultPatternSubCommand))
					StringUtil.copyPartialMatches(args[2], SortingPattern.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(evaluatorSubCommand))
					StringUtil.copyPartialMatches(args[2], EvaluatorType.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(setDefaultAutoSortSubCommand))
					StringUtil.copyPartialMatches(args[2], PluginConfigManager.getBooleanValueStringList(),
							completions);
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

	/* Syntax Error */

	private final String syntax = "/sortingconfig <pattern/evaluator/setautosort/adminconfig>";
	private final String adminConfigSyntax = "/sortingconfig adminconfig <setdefaultpattern/setdefaultevaluator/setdefaultautosort>";
	private final String setAutoSortSyntax = "/sortingconfig setautosort <true/false>";
	private final String adminConfigSetDefaultSortSyntax = "/sortingconfig adminconfig setdefaultautosort <true/false>";

}
