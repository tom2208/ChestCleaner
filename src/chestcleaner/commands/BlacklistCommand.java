package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import chestcleaner.config.PluginConfigManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * A command class representing the blacklist command. Blacklist Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-blacklist
 * 
 * @author Tom2208
 *
 */
public class BlacklistCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;

			if (!player.hasPermission(PluginPermissions.CMD_BLACKLIST.getString())) {
				MessageSystem.sendMessageToCS(MessageType.MISSING_PERMISSION,
						PluginPermissions.CMD_BLACKLIST.getString(), player);
				return true;
			}

		}

		if (args.length >= 2 && args.length <= 3) {

			/* initialize list */
			List<Material> list;
			int listNumber = -1;
			if (args[0].equalsIgnoreCase(sortingSubCommand)) {
				list = PluginConfigManager.getBlacklistSorting();
				listNumber = 0;
			} else if (args[0].equalsIgnoreCase(inventoriesSubCommand)) {
				list = PluginConfigManager.getBlacklistInventory();
				listNumber = 1;
			} else {
				sendSyntaxError(sender);
				return true;
			}

			/** subCommands */

			/*--------------- addMaterial ---------------*/
			if (args[1].equalsIgnoreCase(addMaterialSubCommand)) {

				Material material;

				// addMaterial with name
				if (args.length == 3) {

					material = Material.getMaterial(args[2]);

					if (material == null) {
						MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
								MessageID.MATERIAL_NAME_NOT_EXISTING, sender, args[2]);
						return true;
					}

					// addMaterial with item in hand
				} else {

					if (player == null) {
						MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntaxMaterialErrorMessage, sender);
						return true;
					}

					material = getMaterialFormPlayerHand(player);

					if (material.equals(Material.AIR)) {
						MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.YOU_HAVE_TO_HOLD_AN_ITEM,
								player);
						return true;
					}

				}

				if (list.contains(material)) {
					MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
							MessageID.MATERIAL_ALREADY_ON_BLACKLIST, sender, material.name());
					return true;
				}

				list.add(material);
				safeList(listNumber, list);

				MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.MATERIAL_ADDED_TO_BLACKLIST,
						sender, material.name());

				return true;

				/*--------------- removeMaterial ---------------*/
			} else if (args[1].equalsIgnoreCase(removeMaterialSubCommand)) {

				Material material;

				// removeMaterial with name
				if (args.length == 3) {

					material = Material.getMaterial(args[2]);

					if (material == null) {

						try {

							int index = Integer.valueOf(args[2]);

							if (index - 1 >= 0 && index - 1 < list.size()) {

								material = list.get(index - 1);

							} else {
								MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
										MessageID.INDEX_OUT_OF_BOUNDS, sender, String.valueOf(index));
								return true;
							}

						} catch (NumberFormatException ex) {

							MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
									MessageID.MATERIAL_NAME_NOT_EXISTING, sender, args[2]);
							return true;

						}

					}

					// removeMaterial with item in hand
				} else {
					
					if (player == null) {
						MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntaxMaterialErrorMessage, sender);
						return true;
					}
					
					material = getMaterialFormPlayerHand(player);

					if (material.equals(Material.AIR)) {
						MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.YOU_HAVE_TO_HOLD_AN_ITEM,
								player);
						return true;
					}

				}

				if (!list.contains(material)) {
					MessageSystem.sendMessageToCSWithReplacement(MessageType.ERROR,
							MessageID.BLACKLIST_DOESNT_CONTAIN_MATERIAL, sender, material.name());
					return true;
				}

				list.remove(material);
				safeList(listNumber, list);

				MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS,
						MessageID.MATERIAL_REMOVED_FROM_BLACKLIST, sender, material.name());

				return true;

				/*--------------- list ---------------*/
			} else if (args[1].equalsIgnoreCase(listSubCommand)) {

				if (list.size() == 0) {
					MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.BLACKLIST_EMPTY, sender);
					return true;
				}

				String pageString = args.length == 3 ? args[2] : "1";

				List<String> names  = list.stream().map(Enum::name).collect(Collectors.toList());
				MessageSystem.sendListPageToCS(names, sender, pageString, LIST_PAGE_LENGTH);
				return true;

				/*--------------- clear ---------------*/
			} else if (args[1].equalsIgnoreCase(clearSubCommand)) {

				list.clear();
				safeList(listNumber, list);
				MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.BLACKLIST_CLEARED, sender);
				return true;

			} else {
				sendSyntaxError(sender);
				return true;
			}

		} else {
			sendSyntaxError(sender);
			return true;
		}

	}

	/**
	 * Returns the Material of a hand.
	 * 
	 * @param p The player of the hand.
	 * @return It returns the material of you main hand if it is not AIR otherwise
	 *         the Material of your off hand.
	 */
	private Material getMaterialFormPlayerHand(Player p) {
		if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			if (!p.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
				return p.getInventory().getItemInOffHand().getType();
			}
		}
		return p.getInventory().getItemInMainHand().getType();
	}

	/**
	 * Sends the correct syntax the Player {@code p}.
	 * 
	 * @param sender the player who receives the message.
	 */
	private void sendSyntaxError(CommandSender sender) {
		MessageSystem.sendMessageToCS(MessageType.SYNTAX_ERROR, syntaxErrorMessage, sender);
	}

	/**
	 * Saves the list in to the config.yml .
	 * 
	 * @param list 0 meaning sortingBlacklist, 1 inventoryBlacklist.
	 */
	private void safeList(int list, List<Material> items) {
		if (list == 0) {
			PluginConfigManager.setBlacklistSorting(items);
		} else if (list == 1) {
			PluginConfigManager.setBlacklistInventory(items);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();
		final String[] strCommandList = { addMaterialSubCommand, removeMaterialSubCommand, listSubCommand,
				clearSubCommand };
		final String[] strLists = { sortingSubCommand, inventoriesSubCommand };
		final List<String> commandList = Arrays.asList(strCommandList);
		final List<String> lists = Arrays.asList(strLists);

		if (args.length <= 1) {

			StringUtil.copyPartialMatches(args[0], lists, completions);

		} else if (args.length == 2) {

			StringUtil.copyPartialMatches(args[1], commandList, completions);

		}

		return completions;
	}

	// The length of a page one the list if it gets displayed in the in game chat.
	private final int LIST_PAGE_LENGTH = 8;

	/* sub-commands */
	private final String addMaterialSubCommand = "addMaterial";
	private final String removeMaterialSubCommand = "removeMaterial";
	private final String listSubCommand = "list";
	private final String clearSubCommand = "clear";

	private final String sortingSubCommand = "sorting";
	private final String inventoriesSubCommand = "inventory";

	/* syntax messages */
	private final String syntaxErrorMessage = "/blacklist <sorting/inventory> <addMaterial/removeMaterial/list>";
	private final String syntaxMaterialErrorMessage = "/blacklist <sorting/inventory> <addMaterial/removeMaterial> <material>";

}
