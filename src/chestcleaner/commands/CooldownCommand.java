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
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (!(cs instanceof Player)) {
			MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER);
			return true;
		}

		Player p = (Player) cs;

		if (p.hasPermission(PluginPermissions.CMD_COOLDOWN.getString())) {

			if (args.length == 2) {

				/* SETACTIVE SUBCOMMAND */
				if (args[0].equalsIgnoreCase(setActiveSubCommand)) {

					return setActiveSubCommand(args[1], p);

					/* SETCOOLDOWN SUBCOMMAND */
				} else if (args[0].equalsIgnoreCase(setCooldownSubCommand)) {

					return setCooldownSubCommand(args[1], p);

				} else {
					sendSytaxErrorToPlayer(p);
					return true;
				}

			} else {
				sendSytaxErrorToPlayer(p);
				return true;
			}

		} else {
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION,
					PluginPermissions.CMD_COOLDOWN.getString(), p);
			return true;
		}

	}

	private boolean setCooldownSubCommand(String arg, Player player) {

		try {

			int time = Integer.valueOf(arg);
			if (CooldownManager.getInstance().getCooldown() != time) {
				CooldownManager.getInstance().setCooldown(time * 1000);
				PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_TIME, time * 1000);
			}
			MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.TIMER_TIME, player,
					String.valueOf(time));

		} catch (NumberFormatException ex) {

			MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.NOT_AN_INTEGER, player,
					arg);

		}

		return true;
	}

	private boolean setActiveSubCommand(String arg, Player player) {
		String trueStr = PluginConfigManager.getInstance().getTrueString();
		String falseStr = PluginConfigManager.getInstance().getFalseString();

		if (arg.equalsIgnoreCase(trueStr)) {

			if (!CooldownManager.getInstance().isActive()) {
				PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_ACTIVE, true);
				CooldownManager.getInstance().setActive(true);
			}
			MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.COOLDOWN_TOGGLE, player,
					trueStr);
			return true;

		} else if (arg.equalsIgnoreCase(falseStr)) {

			if (CooldownManager.getInstance().isActive()) {
				PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_ACTIVE, false);
				CooldownManager.getInstance().setActive(false);
			}
			MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.COOLDOWN_TOGGLE, player,
					falseStr);
			return true;

		} else {
			sendSytaxErrorToPlayer(player);
			return true;
		}
	}

	/**
	 * Sends a syntax error of the command CooldownCommand to the player
	 * {@code player}.
	 * 
	 * @param player the player who should get the error message.
	 */
	private void sendSytaxErrorToPlayer(Player player) {
		MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, cooldownSyntax, player);
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

	private final String cooldownSyntax = "/cooldown <setActive/setCooldown>";
}
