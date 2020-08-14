package chestcleaner.commands;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.config.serializable.Category;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.CategorizerManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.StringUtils;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A command class representing the SortingConfig command. SortingConfig Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-sortingconfig
 *
 */
public class SortingAdminCommand implements CommandExecutor, TabCompleter {

	/* sub-commands */
	private final String autosortSubCommand = "autosort";
	private final String categoriesSubCommand = "categories";
	private final String cooldownSubCommand = "cooldown";
	private final String patternSubCommand = "pattern";
	private final String chatNotificationSubCommand = "chatNotification";
	private final String sortingSoundSubCommand = "sortingSound";
	private final String refillSubCommand = "refill";
	
	private final String setSubCommand = "set";
	private final String activeSubCommand = "active";
	private final String addFromBookSubCommand = "addFromBook";
	private final String getAsBookSubCommand = "getAsBook";
	
	private final String blocksSubCommand = "blocks";
	private final String consumablesSubCommand = "consumables";
	private final String breakablesSubCommand = "breakables";
	
	private final String autosortProperty = "default autosort";
	private final String categoriesProperty = "default categoryOrder";
	private final String cooldownProperty = "cooldown (in ms)";
	private final String patternProperty = "default sortingpattern";
	private final String activeProperty = "cooldownActive";
	private final String chatNotificationProperty = "chat sorting notification";
	private final String soundProperty = "sorting sound";
	private final String allRefillsProperty = "all refills";
	
	private final String[] strCommandList = { autosortSubCommand, categoriesSubCommand, cooldownSubCommand,
			patternSubCommand, chatNotificationSubCommand, sortingSoundSubCommand, refillSubCommand};
	private final String[] categoriesSubCommandList = { setSubCommand, addFromBookSubCommand, getAsBookSubCommand };
	private final String[] cooldownSubCommandList = { setSubCommand, activeSubCommand };
	private final String[] refillSubCommandList = {blocksSubCommand, consumablesSubCommand, breakablesSubCommand, "true", "false"};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (args.length == 1) {
			return getConfig(sender, args[0]);

		} else if (args.length == 2) {
			if (autosortSubCommand.equalsIgnoreCase(args[0])) {
				setDefaultAutoSort(sender, args[1]);
				return true;
			} else if (categoriesSubCommand.equalsIgnoreCase(args[0])
					&& addFromBookSubCommand.equalsIgnoreCase(args[1])) {
				addFromBook(sender, player);
				return true;
			} else if (cooldownSubCommand.equalsIgnoreCase(args[0]) && activeSubCommand.equalsIgnoreCase(args[1])) {
				getConfig(sender, activeSubCommand);
				return true;
			} else if (patternSubCommand.equalsIgnoreCase(args[0])) {
				setDefaultPattern(sender, args[1]);
				return true;
			} else if (chatNotificationSubCommand.equalsIgnoreCase(args[0])) {
				setChatNotification(sender, args[1]);
				return true;
			} else if (sortingSoundSubCommand.equalsIgnoreCase(args[0])) {
				setSound(sender, args[1]);
				return true;
			} else if(refillSubCommand.equalsIgnoreCase(args[0])) {
				setAllRefills(sender, args[1]);
				return true;
			}

		} else if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0]) && setSubCommand.equalsIgnoreCase(args[1])) {
				setDefaultCategories(sender, args[2]);
				return true;
				
			} else if (categoriesSubCommand.equalsIgnoreCase(args[0])
					&& getAsBookSubCommand.equalsIgnoreCase(args[1])) {
				getBook(sender, player, args[2]);
				return true;
				
			} else if (cooldownSubCommand.equalsIgnoreCase(args[0])) {
				if(activeSubCommand.equalsIgnoreCase(args[1])) {
					setCooldownActive(sender, args[2]);
				}else if(setSubCommand.equalsIgnoreCase(args[1])) {
					setCooldownTime(sender, args[2]);
				}
				return true;
				
			} else if (refillSubCommand.equalsIgnoreCase(args[0])) {
				return setRefill(sender, args[1], args[2]);
			}
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
					|| args[0].equalsIgnoreCase(sortingSoundSubCommand))
				StringUtil.copyPartialMatches(args[1], StringUtils.getBooleanValueStringList(), completions);
			else if (args[0].equalsIgnoreCase(categoriesSubCommand))
				StringUtil.copyPartialMatches(args[1], Arrays.asList(categoriesSubCommandList), completions);
			else if (args[0].equalsIgnoreCase(cooldownSubCommand))
				StringUtil.copyPartialMatches(args[1], Arrays.asList(cooldownSubCommandList), completions);
			else if (args[0].equalsIgnoreCase(patternSubCommand))
				StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);
			else if (args[0].equalsIgnoreCase(refillSubCommand))
				StringUtil.copyPartialMatches(args[1], Arrays.asList(refillSubCommandList), completions);
			
		} else if (args.length == 3) {
			if (categoriesSubCommand.equalsIgnoreCase(args[0])) {
				if (setSubCommand.equalsIgnoreCase(args[1]))
					StringUtils.copyPartialMatchesCommasNoDuplicates(args[2], CategorizerManager.getAllNames(),
							completions);
				
				if (getAsBookSubCommand.equalsIgnoreCase(args[1]))
					StringUtil.copyPartialMatches(args[2], PluginConfigManager.getAllCategories().stream()
							.map(Category::getName).collect(Collectors.toList()), completions); //
				
			} else if (cooldownSubCommand.equalsIgnoreCase(args[0]) && activeSubCommand.equalsIgnoreCase(args[1])) {
				StringUtil.copyPartialMatches(args[2], StringUtils.getBooleanValueStringList(), completions);
				
			} else if(refillSubCommand.equalsIgnoreCase(args[0])) {
				if(args[1].equalsIgnoreCase(blocksSubCommand) || args[1].equalsIgnoreCase(consumablesSubCommand) || args[1].equalsIgnoreCase(breakablesSubCommand)) {
					StringUtil.copyPartialMatches(args[2], StringUtils.getBooleanValueStringList(), completions);
				}
			}
		}
		return completions;
	}

	private boolean getConfig(CommandSender sender, String command) {
		String key = "";
		String value = "";

		if (command.equalsIgnoreCase(autosortSubCommand)) {
			key = autosortProperty;
			value = String.valueOf(PluginConfigManager.getDefaultAutoSortBoolean());

		} else if (command.equalsIgnoreCase(categoriesSubCommand)) {
			key = categoriesProperty;
			value = PluginConfigManager.getCategoryOrder().toString();

		} else if (command.equalsIgnoreCase(cooldownSubCommand)) {
			key = cooldownProperty;
			value = String.valueOf(PluginConfigManager.getCooldown());

		} else if (command.equalsIgnoreCase(activeSubCommand)) {
			key = activeProperty;
			value = String.valueOf(PluginConfigManager.isCooldownActive());

		} else if (command.equalsIgnoreCase(patternSubCommand)) {
			key = patternProperty;
			value = PluginConfigManager.getDefaultPattern().name();

		} else if (command.equalsIgnoreCase(chatNotificationSubCommand)) {
			key = chatNotificationProperty;
			value = String.valueOf(PluginConfigManager.getDefaultChatNotificationBoolean());
			
		} else if (command.equalsIgnoreCase(sortingSoundSubCommand)) {
			key = sortingSoundSubCommand;
			value = String.valueOf(PluginConfigManager.getDefaultSortingSoundBoolean());

		}

		if(key != "" && value != "") {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, sender, key, value);
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the configuration for a refill option.
	 * @param sender the sender who enters the command.
	 * @param arg the subcommand string.
	 * @param bool true: sets active, false: sets inactive
	 * @return True if the command can get parsed, otherwise false.
	 */
	private boolean setRefill(CommandSender sender, String arg, String bool){
		if(StringUtils.isStringBoolean(sender, bool)) {
			
			boolean b = Boolean.parseBoolean(bool);
			String property = new String();
			
			if(arg.equalsIgnoreCase(blocksSubCommand)) {
				PluginConfigManager.setDefaultBlockRefill(b);
				property = blocksSubCommand;
			}else if(arg.equalsIgnoreCase(consumablesSubCommand)) {
				PluginConfigManager.setDefaultConsumablesRefill(b);
				property = consumablesSubCommand;
			}else if(arg.equalsIgnoreCase(breakablesSubCommand)) {
				PluginConfigManager.setDefaultBreakableRefill(b);
				property = breakablesSubCommand;
			}else {
				return false;
			}
			
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CURRENT_VALUE, sender, property, b);
			
		}
		
		return true;
	}
	
	private void setAllRefills(CommandSender sender, String bool) {
		if(StringUtils.isStringBoolean(sender, bool)) {
			boolean b = Boolean.parseBoolean(bool);
			PluginConfigManager.setDefaultBlockRefill(b);
			PluginConfigManager.setDefaultConsumablesRefill(b);
			PluginConfigManager.setDefaultBreakableRefill(b);
			MessageSystem.sendChangedValue(sender, allRefillsProperty, String.valueOf(b));
		}
	}

	private void setChatNotification(CommandSender sender, String bool) {
		if (StringUtils.isStringBoolean(sender, bool)) {
			boolean b = Boolean.parseBoolean(bool);
			PluginConfigManager.setDefaultChatNotificationBoolean(b);
			MessageSystem.sendChangedValue(sender, chatNotificationProperty, String.valueOf(b));
		}
	}

	private void setSound(CommandSender sender, String bool) {
		if (StringUtils.isStringBoolean(sender, bool)) {
			boolean b = Boolean.parseBoolean(bool);
			PluginConfigManager.setDefaultSortingSoundBoolean(b);
			MessageSystem.sendChangedValue(sender, soundProperty, String.valueOf(b));
		}
	}

	private void setDefaultAutoSort(CommandSender sender, String bool) {
		if (!StringUtils.isStringTrueOrFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
		} else {

			boolean b = Boolean.parseBoolean(bool);
			PluginConfigManager.setDefaultAutoSort(b);
			MessageSystem.sendChangedValue(sender, autosortProperty, String.valueOf(b));
		}
	}

	private void setDefaultPattern(CommandSender sender, String patternName) {
		SortingPattern pattern = SortingPattern.getSortingPatternByName(patternName);

		if (pattern == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_PATTERN_ID, sender);
		} else {
			PluginConfigManager.setDefaultPattern(pattern);
			MessageSystem.sendChangedValue(sender, patternProperty, pattern.name());
		}
	}

	private void setDefaultCategories(CommandSender sender, String commaSeperatedCategories) {
		List<String> categories = Arrays.asList(commaSeperatedCategories.split(","));

		if (!CategorizerManager.validateExists(categories)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, sender);
		} else {
			PluginConfigManager.setCategoryOrder(categories);
			MessageSystem.sendChangedValue(sender, categoriesProperty, categories.toString());
		}
	}

	private void addFromBook(CommandSender sender, Player player) {
		if (player == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
		} else {
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			if (itemInHand.getType().equals(Material.WRITABLE_BOOK)
					|| itemInHand.getType().equals(Material.WRITTEN_BOOK)) {
				String name = CategorizerManager.addFromBook(((BookMeta) itemInHand.getItemMeta()).getPages(), sender);
				MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.INFO_CATEGORY_NEW, sender,
						name);
			} else {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_BOOK, sender);
			}
		}
	}

	private void getBook(CommandSender sender, Player player, String categoryString) {
		if (player == null) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_YOU_NOT_PLAYER, sender);
		} else {
			Category category = PluginConfigManager.getCategoryByName(categoryString);
			if (category == null) {
				MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_NAME, sender);
			} else {
				ItemStack book = category.getAsBook();
				player.getWorld().dropItem(player.getLocation(), book);
			}
		}
	}

	private void setCooldownTime(CommandSender sender, String arg) {

		if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_COOLDOWN.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_COOLDOWN);
		} else {

			try {
				int time = Integer.parseInt(arg);
				PluginConfigManager.setCooldown(time);
				MessageSystem.sendChangedValue(sender, cooldownProperty, String.valueOf(time));

			} catch (NumberFormatException ex) {
				MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.ERROR_VALIDATION_INTEGER,
						sender, arg);
			}
		}
	}

	private void setCooldownActive(CommandSender sender, String arg) {
		if (!sender.hasPermission(PluginPermissions.CMD_ADMIN_COOLDOWN.getString())) {
			MessageSystem.sendPermissionError(sender, PluginPermissions.CMD_ADMIN_COOLDOWN);
		} else if (!StringUtils.isStringTrueOrFalse(arg)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
		} else {
			boolean state = Boolean.getBoolean(arg);
			PluginConfigManager.setCooldownActive(state);
			MessageSystem.sendChangedValue(sender, activeProperty, String.valueOf(state));
		}

	}

}
