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
import chestcleaner.config.PluginConfigurationManager;
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

		if (!(cs instanceof Player)) {
			return true;
		}

		Player p = (Player) cs;

		if (args.length == 2) {

			/* PATTERN */
			if (args[0].equalsIgnoreCase(patternSubCommand)) {

				if (!p.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString());
					return true;
				}

				SortingPattern pattern = SortingPattern.getSortingPatternByName(args[1]);

				if (pattern == null) {

					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, p);
					return true;

				} else {

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.PATTERN_SET, p);
					PluginConfig.getInstance().setSortingPattern(pattern, p);
					PlayerDataManager.getInstance().loadPlayerData(p);
					return true;

				}

				/* EVALUATOR */
			} else if (args[0].equalsIgnoreCase(evaluatorSubCommand)) {

				if (!p.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_EVALUATOR.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_EVALUATOR.getString());
					return true;
				}

				EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(args[1]);

				if (evaluator == null) {

					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INAVLID_EVALUATOR_ID, p);
					return true;

				} else {

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.EVALUATOR_SET, p);
					PluginConfig.getInstance().setEvaluatorTyp(evaluator, p);
					PlayerDataManager.getInstance().loadPlayerData(p);
					return true;

				}

				/* SETAUTOSORT */
			} else if (args[0].equalsIgnoreCase(setAutoSortSubCommand)) {

				if (!p.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString())) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_SET_AUTOSORT.getString());
					return true;
				}

				boolean b = false;
				if (PluginConfigurationManager.isStringTrue(args[1])) {
					b = true;
				} else if (!PluginConfigurationManager.isStringFalse(args[1])) {
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, setAutoSortSyntax, p);
					return true;
				}

				MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.AUTOSORTING_TOGGLED, p,
						String.valueOf(b));
				PluginConfig.getInstance().setAutoSort(b, p);
				PlayerDataManager.getInstance().loadPlayerData(p);
				return true;

			} else {
				sendSyntaxErrorMessage(p);
				return true;
			}

		} else if (args.length == 3) {

			/* ADMINCONFIG */
			if (args[0].equalsIgnoreCase(adminConfigSubCommand)) {

				if (!p.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString())) {
					MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
							PluginPermissions.CMD_SORTING_CONFIG_ADMIN_CONTROL.getString(), p);
					return true;
				}

				/* SETDEFAULTPATTERN */
				if (args[1].equalsIgnoreCase(setDefaultPatternSubCommand)) {

					SortingPattern pattern = SortingPattern.getSortingPatternByName(args[2]);

					if (pattern == null) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INVALID_PATTERN_ID, p);
						return true;
					}

					PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_SORTING_PATTERN, pattern);

					SortingPattern.DEFAULT = pattern;
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.DEFAULT_PATTER_SET, p);
					return true;

					/* SETDEFAULTEVALUATOR */
				} else if (args[1].equalsIgnoreCase(setDefaultEvaluatorSubCommand)) {

					EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(args[2]);

					if (evaluator == null) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.INAVLID_EVALUATOR_ID, p);
						return true;
					}

					PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_EVALUATOR, evaluator);

					EvaluatorType.DEFAULT = evaluator;
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.DEFAULT_EVALUATOR_SET, p);
					return true;

					/* SETDEFAULTAUTOSORTING */
				} else if (args[1].equalsIgnoreCase(setDefaultAutoSortSubCommand)) {

					Boolean b = false;

					if (PluginConfigurationManager.isStringTrue(args[2])) {
						b = true;
					} else if (!PluginConfigurationManager.isStringFalse(args[2])) {
						MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, adminConfigSetDefaultSortSyntax, p);
						return true;
					}

					PlayerDataManager.getInstance().setDefaultAutoSort(b);

					PluginConfig.getInstance().setIntoConfig(ConfigPath.DEFAULT_AUTOSORT, b);
					MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS,
							MessageID.AUTOSORTING_TOGGLED, p, String.valueOf(b));

					return true;

				} else {
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, adminConfigSyntax, p);
					return true;
				}

			} else {
				sendSyntaxErrorMessage(p);
				return true;
			}

		} else {
			sendSyntaxErrorMessage(p);
			return true;
		}

	}

	private void sendSyntaxErrorMessage(Player p) {
		MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, syntax, p);
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
				StringUtil.copyPartialMatches(args[1], PluginConfigurationManager.getBooleanValueStringList(),
						completions);
			else if (args[0].equalsIgnoreCase(adminConfigSubCommand))
				StringUtil.copyPartialMatches(args[1], adminControlSubCommands, completions);

		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase(commandList.get(3))) {
				if (args[1].equalsIgnoreCase(setDefaultPatternSubCommand))
					StringUtil.copyPartialMatches(args[2], SortingPattern.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(evaluatorSubCommand))
					StringUtil.copyPartialMatches(args[2], EvaluatorType.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(setDefaultAutoSortSubCommand))
					StringUtil.copyPartialMatches(args[2], PluginConfigurationManager.getBooleanValueStringList(),
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
