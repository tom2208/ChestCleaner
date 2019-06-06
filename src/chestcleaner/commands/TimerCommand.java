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

import chestcleaner.config.Config;
import chestcleaner.main.Main;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

public class TimerCommand implements CommandExecutor, TabCompleter {

	private final List<String> timerCommands = new ArrayList<>();

	public TimerCommand() {
		timerCommands.add("setActive");
		timerCommands.add("setTime");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (!(cs instanceof Player)) {
			MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.NOT_A_PLAYER);
			return true;
		}

		Player p = (Player) cs;

		if (p.hasPermission("chestcleaner.cmd.timer")) {

			if (args.length == 2) {

				/* SETACTIVE SUBCOMMAND */
				if (args[0].equalsIgnoreCase(timerCommands.get(0))) {

					if (args[1].equalsIgnoreCase("true")) {

						if (!Main.timer) {
							Config.setTimerPermission(true);
							Main.timer = true;
						}
						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.TIMER_ACTIVATED, p);
						return true;

					} else if (args[1].equalsIgnoreCase("false")) {

						if (Main.timer) {
							Config.setTimerPermission(false);
							Main.timer = false;
						}
						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.TIMER_DEACTIVATED, p);
						return true;

					} else {
						MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, " /timer <setActive> <true/false>",
								p);
						return true;
					}

					/* SETTIMER SUBCOMMAND */
				} else if (args[0].equalsIgnoreCase(timerCommands.get(1))) {

					int time = Integer.valueOf(args[1]);
					if (Main.time != time) {
						Main.time = time;
						Config.setTime(time);
					}
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
							StringTable.getMessage(MessageID.TIMER_NEW_TIME, "%time", String.valueOf(time)), p);
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
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION, "chestcleaner.cmd.timer", p);
			return true;
		}

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();
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
		return completions;
	}

}
