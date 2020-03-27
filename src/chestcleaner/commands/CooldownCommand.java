package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import chestcleaner.sorting.CooldownManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * A command class representing the Timer command. Timer Command explained:
 * https://github.com/tom2208/ChestCleaner/wiki/Command-timer
 * 
 * @author Tom2208
 *
 */
public class CooldownCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


		
		if(sender instanceof Player) {
			if (!sender.hasPermission(PluginPermissions.CMD_COOLDOWN.getString())) {
				MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
						PluginPermissions.CMD_COOLDOWN.getString(), (Player) sender);
				return true;
			}
		}
		
		if (args.length == 2) {

			/* SETACTIVE SUBCOMMAND */
			if (args[0].equalsIgnoreCase(setActiveSubCommand)) {

				return setActiveSubCommand(args[1], sender);

				/* SETCOOLDOWN SUBCOMMAND */
			} else if (args[0].equalsIgnoreCase(setCooldownSubCommand)) {

				return setCooldownSubCommand(args[1], sender);

			} else {
				sendSytaxError(sender);
				return true;
			}

		} else {
			sendSytaxError(sender);
			return true;
		}

	}

	private boolean setCooldownSubCommand(String arg, CommandSender sender) {

		try {
			int time = Integer.valueOf(arg);
			if (CooldownManager.getInstance().getCooldown() != time) {
				CooldownManager.getInstance().setCooldown(time * 1000);
				PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_TIME, time * 1000);
			}
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.TIMER_TIME, sender,
					String.valueOf(time));

		} catch (NumberFormatException ex) {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.NOT_AN_INTEGER, sender, arg);
		}

		return true;
	}

	private boolean setActiveSubCommand(String arg, CommandSender sender) {
		String trueStr = PluginConfigManager.getInstance().getTrueString();
		String falseStr = PluginConfigManager.getInstance().getFalseString();

		if (arg.equalsIgnoreCase(trueStr)) {

			if (!CooldownManager.getInstance().isActive()) {
				PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_ACTIVE, true);
				CooldownManager.getInstance().setActive(true);
			}
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.COOLDOWN_TOGGLE, sender,
					trueStr);
			return true;

		} else if (arg.equalsIgnoreCase(falseStr)) {

			if (CooldownManager.getInstance().isActive()) {
				PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_ACTIVE, false);
				CooldownManager.getInstance().setActive(false);
			}
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.COOLDOWN_TOGGLE, sender,
					falseStr);
			return true;

		} else {
			sendSytaxError(sender);
			return true;
		}
	}

	/**
	 * Sends a syntax error of the command CooldownCommand to the CommandServer
	 * {@code sender}.
	 * 
	 * @param sender the CommandServer which should get the error message.
	 */
	private void sendSytaxError(CommandSender sender) {
		MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, cooldownSyntax, sender);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();
		String[] subCommandsListStr = { setActiveSubCommand, setCooldownSubCommand };
		List<String> subCommandsList = Arrays.asList(subCommandsListStr);

		if (args.length == 1) {
			switch (args.length) {
			case 0:
				StringUtil.copyPartialMatches(args[0], subCommandsList, completions);
				break;
			case 1:
				StringUtil.copyPartialMatches(args[0], subCommandsList, completions);
				break;
			case 2:
				/* SETACTIVE */
				if (args[0].equalsIgnoreCase(setActiveSubCommand)) {
					StringUtil.copyPartialMatches(args[1], PluginConfigManager.getBooleanValueStringList(),
							completions);
				}

			}

			Collections.sort(completions);
		}
		return completions;
	}

	private final String setCooldownSubCommand = "setCooldown";
	private final String setActiveSubCommand = "setActive";

	private final String cooldownSyntax = "/cooldown <setActive/setCooldown> <arg>";
}
