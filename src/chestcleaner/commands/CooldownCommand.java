package chestcleaner.commands;

import java.util.ArrayList;
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

	private final List<String> timerCommands = new ArrayList<>();

	public CooldownCommand() {
		timerCommands.add("setActive");
		timerCommands.add("setTime");
	}

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
				if (args[0].equalsIgnoreCase(timerCommands.get(0))) {

					if (args[1].equalsIgnoreCase("true")) {

						if (!CooldownManager.getInstance().isActive()) {
							PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_ACTIVE, true);
							CooldownManager.getInstance().setActive(true);
						}
						MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.COOLDOWN_TOGGLE, p, "true");
						return true;

					} else if (args[1].equalsIgnoreCase("false")) {

						if (CooldownManager.getInstance().isActive()) {
							PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_ACTIVE, false);
							CooldownManager.getInstance().setActive(false);
						}
						MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.COOLDOWN_TOGGLE, p, "false");
						return true;

					} else {
						MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, " /timer <setActive> <true/false>",
								p);
						return true;
					}

					/* SETTIMER SUBCOMMAND */
				} else if (args[0].equalsIgnoreCase(timerCommands.get(1))) {

					int time = Integer.valueOf(args[1]);
					if (CooldownManager.getInstance().getCooldown() != time) {
						CooldownManager.getInstance().setCooldown(time * 1000);
						PluginConfig.getInstance().setIntoConfig(ConfigPath.COOLDOWN_TIME, time * 1000);
					}
					MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS, MessageID.TIMER_TIME, p, String.valueOf(time));

					return true;

				} else {
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, "/timer <setActive/setTime>", p);
					return true;
				}

			} else {
				MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, "/timer <setActive/setTime>", p);
				return true;
			}

		} else {
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION, PluginPermissions.CMD_COOLDOWN.getString(), p);
			return true;
		}

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			switch (args.length) {
			case 0:
				StringUtil.copyPartialMatches(args[0], timerCommands, completions);
				break;
			case 1:
				StringUtil.copyPartialMatches(args[0], timerCommands, completions);
				break;
			case 2:
				/* SETACTIVE */
				if (args[0].equalsIgnoreCase(timerCommands.get(0))) {

					ArrayList<String> commands = new ArrayList<>();
					commands.add("true");
					commands.add("false");

					StringUtil.copyPartialMatches(args[1], commands, completions);
				}

			}

			Collections.sort(completions);
		}
		return completions;
	}

}
