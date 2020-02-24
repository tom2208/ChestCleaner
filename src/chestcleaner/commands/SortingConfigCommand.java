package chestcleaner.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.config.Config;
import chestcleaner.playerdata.PlayerData;
import chestcleaner.playerdata.PlayerDataManager;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

/**
 * A command class representing the SortingConfig command. SortingConfig Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-sortingconfig
 * 
 * @author Tom2208
 *
 */
public class SortingConfigCommand implements CommandExecutor, TabCompleter {

	private final List<String> commandList = new ArrayList<>();
	private final List<String> booleans = new ArrayList<>();
	private final List<String> admincontrolSubCMD = new ArrayList<>();

	public SortingConfigCommand() {

		commandList.add("pattern");
		commandList.add("evaluator");
		commandList.add("setautosort");
		commandList.add("adminconfig");

		booleans.add("true");
		booleans.add("false");

		admincontrolSubCMD.add("setdefaultpattern");
		admincontrolSubCMD.add("setdefaultevaluator");
		admincontrolSubCMD.add("setdefaultautosort");

	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (!(cs instanceof Player)) {
			return true;
		}

		Player p = (Player) cs;

		if (args.length == 2) {

			/* PATTERN */
			if (args[0].equalsIgnoreCase(commandList.get(0))) {

				if (!p.hasPermission("chestcleaner.cmd.sortingconfig.pattern")) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							"chestcleaner.cmd.sortingconfig.pattern");
					return true;
				}

				SortingPattern pattern = SortingPattern.getSortingPatternByName(args[1]);

				if (pattern == null) {

					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.NO_PATTERN_ID, p);
					return true;

				} else {

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.NEW_PATTERN_SET, p);
					PlayerData.setSortingPattern(pattern, p);
					PlayerDataManager.loadPlayerData(p);
					return true;

				}

				/* EVALUATOR */
			} else if (args[0].equalsIgnoreCase(commandList.get(1))) {

				if (!p.hasPermission("chestcleaner.cmd.sortingconfig.evaluator")) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							"chestcleaner.cmd.sortingconfig.evaluator");
					return true;
				}

				EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(args[1]);

				if (evaluator == null) {

					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.NO_EVALUATOR_ID, p);
					return true;

				} else {

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.NEW_EVALUATOR_SET, p);
					PlayerData.setEvaluatorTyp(evaluator, p);
					PlayerDataManager.loadPlayerData(p);
					return true;

				}

				/* SETAUTOSORT */
			} else if (args[0].equalsIgnoreCase(commandList.get(2))) {

				if (!p.hasPermission("chestcleaner.cmd.sortingconfig.setautosort")) {
					MessageSystem.sendConsoleMessage(MessageType.MISSING_PERMISSION,
							"chestcleaner.cmd.sortingconfig.setautosort");
					return true;
				}

				boolean b = false;
				if (args[1].equalsIgnoreCase("true")) {
					b = true;
				} else if (!args[1].equalsIgnoreCase("false")) {
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
							"/sortingconfig setautosort <true/false>", p);
					return true;
				}

				MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
						StringTable.getMessage(MessageID.AUTOSORT_WAS_SET, "%boolean", String.valueOf(b)), p);
				PlayerData.setAutoSort(b, p);
				PlayerDataManager.loadPlayerData(p);
				return true;

			} else {
				sendSyntaxErrorMessage(p);
				return true;
			}

		} else if (args.length == 3) {

			/* ADMINCONFIG */
			if (args[0].equalsIgnoreCase(commandList.get(3))) {

				if (!p.hasPermission("chestcleaner.cmd.sorting.config.admincontrol")) {
					MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
							"chestcleaner.cmd.sorting.config.admincontrol", p);
					return true;
				}

				/* SETDEFAULTPATTERN */
				if (args[1].equalsIgnoreCase(admincontrolSubCMD.get(0))) {

					SortingPattern pattern = SortingPattern.getSortingPatternByName(args[2]);

					if (pattern == null) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.NO_PATTERN_ID, p);
						return true;
					}

					Config.setDefaultSortingPattern(pattern);
					SortingPattern.DEFAULT = pattern;
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.NEW_DEFAULT_SORTING_PATTERN, p);
					return true;

					/* SETDEFAULTEVALUATOR */
				} else if (args[1].equalsIgnoreCase(admincontrolSubCMD.get(1))) {

					EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(args[2]);

					if (evaluator == null) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.NO_EVALUATOR_ID, p);
						return true;
					}

					Config.setDefaultEvaluator(evaluator);
					EvaluatorType.DEFAULT = evaluator;
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.NEW_DEFAULT_EVALUATOR, p);
					return true;

					/* SETDEFAULTAUTOSORTING */
				} else if (args[1].equalsIgnoreCase(admincontrolSubCMD.get(2))) {

					Boolean b = false;

					if (args[2].equalsIgnoreCase("true")) {
						b = true;
					} else if (!args[2].equalsIgnoreCase("false")) {
						MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
								"/sortingconfig adminconfig setdefaultautosort <true/false>", p);
						return true;
					}

					PlayerDataManager.defaultAutoSort = b;
					
					Config.setDefaultAutoSort(b);
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
							StringTable.getMessage(MessageID.DEFUALT_AUTOSORT, "%boolean", String.valueOf(b)), p);
					return true;

				} else {
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
							"/sortingconfig adminconfig <setdefaultpattern/setdefaultevaluator/setdefaultautosort>", p);
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
		MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
				"/sortingconfig <pattern/evaluator/setautosort/adminconfig>", p);
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {

		final List<String> completions = new ArrayList<>();

		if (args.length <= 1) {

			StringUtil.copyPartialMatches(args[0], commandList, completions);

		} else if (args.length == 2) {

			if (args[0].equalsIgnoreCase(commandList.get(0)))
				StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);
			else if (args[0].equalsIgnoreCase(commandList.get(1)))
				StringUtil.copyPartialMatches(args[1], EvaluatorType.getIDList(), completions);
			else if (args[0].equalsIgnoreCase(commandList.get(2)))
				StringUtil.copyPartialMatches(args[1], booleans, completions);
			else if (args[0].equalsIgnoreCase(commandList.get(3)))
				StringUtil.copyPartialMatches(args[1], admincontrolSubCMD, completions);

		} else if (args.length == 3) {
			if(args[0].equalsIgnoreCase(commandList.get(3))){
				if (args[1].equalsIgnoreCase(admincontrolSubCMD.get(0)))
					StringUtil.copyPartialMatches(args[2], SortingPattern.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(admincontrolSubCMD.get(1)))
					StringUtil.copyPartialMatches(args[2], EvaluatorType.getIDList(), completions);
				else if (args[1].equalsIgnoreCase(admincontrolSubCMD.get(2)))
					StringUtil.copyPartialMatches(args[2], booleans, completions);
			}
		}

		return completions;

	}

}
