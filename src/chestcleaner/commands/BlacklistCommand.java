package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.config.PluginConfig;
import chestcleaner.config.PluginConfig.ConfigPath;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.MaterialListUtils;
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

	// This List is the global list of blacklisted inventories, it get used in other
	// classes etc.
	public static ArrayList<Material> inventoryBlacklist = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (!(sender instanceof Player)) {
			MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.YOU_HAVE_TO_BE_PLAYER.getID());
			return true;
		}

		Player p = (Player) sender;

		if (p.hasPermission(PluginPermissions.CMD_BLACKLIST.getString())) {

			if (args.length <= 1) {
				sendSyntaxError(p);
				return true;
			}

			if (args.length >= 2 && args.length <= 3) {

				/* initialize list */
				ArrayList<Material> list;
				int listNumber = -1;
				if (args[0].equalsIgnoreCase(sortingSubCommand)) {
					list = InventorySorter.blacklist;
					listNumber = 0;
				} else if (args[0].equalsIgnoreCase(inventoriesSubCommand)) {
					list = inventoryBlacklist;
					listNumber = 1;
				} else {
					sendSyntaxError(p);
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
							MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
									MessageID.MATERIAL_NAME_NOT_EXISTING, p, args[2]);
							return true;
						}

						// addMaterial with item in hand
					} else {
						material = getMaterialFormPlayerHand(p);

						if (material.equals(Material.AIR)) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.YOU_HAVE_TO_HOLD_AN_ITEM, p);
							return true;
						}

					}

					if (list.contains(material)) {
						MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
								MessageID.MATERIAL_ALREADY_ON_BLACKLIST, p, material.name());
						return true;
					}

					list.add(material);
					safeList(listNumber);

					MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS,
							MessageID.MATERIAL_ADDED_TO_BLACKLIST, p, material.name());

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
									MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
											MessageID.INDEX_OUT_OF_BOUNDS, p, String.valueOf(index));
									return true;
								}

							} catch (NumberFormatException ex) {

								MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
										MessageID.MATERIAL_NAME_NOT_EXISTING, p, args[2]);
								return true;

							}

						}

						// removeMaterial with item in hand
					} else {
						material = getMaterialFormPlayerHand(p);

						if (material.equals(Material.AIR)) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.YOU_HAVE_TO_HOLD_AN_ITEM, p);
							return true;
						}

					}

					if (!list.contains(material)) {
						MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
								MessageID.BLACKLIST_DOESNT_CONTAIN_MATERIAL, p, material.name());
						return true;
					}

					list.remove(material);
					safeList(listNumber);

					MessageSystem.sendMessageToPlayerWithReplacements(MessageType.SUCCESS,
							MessageID.MATERIAL_REMOVED_FROM_BLACKLIST, p, material.name());

					return true;

					/*--------------- list ---------------*/
				} else if (args[1].equalsIgnoreCase(listSubCommand)) {

					if (list.size() == 0) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.BLACKLIST_EMPTY, p);
						return true;
					}

					int page = 1;
					int pages = (list.size()-1)/ LIST_PAGE_LENGTH + 1;

					if (args.length == 3) {

						try {

							page = Integer.valueOf(args[2]);

						} catch (NumberFormatException ex) {
							MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
									MessageID.NOT_AN_INTEGER, p, args[1]);
							return true;
						}

						if (!(page > 0 && page <= pages)) {
							MessageSystem.sendMessageToPlayerWithReplacements(MessageType.ERROR,
									MessageID.INVALID_PAGE_NUMBER, p, "1 - " + pages);
							return true;
						}

					}

					MaterialListUtils.sendListPageToPlayer(list, p, page, LIST_PAGE_LENGTH, pages);
					return true;

					/*--------------- clear ---------------*/
				} else if (args[1].equalsIgnoreCase(clearSubCommand)) {

					list.clear();
					safeList(listNumber);
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.BLACKLIST_CLEARED, p);
					return true;

				} else {
					sendSyntaxError(p);
					return true;
				}

			} else {
				sendSyntaxError(p);
				return true;
			}

		} else {
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION, PluginPermissions.CMD_BLACKLIST.getString(), p);
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
	 * @param p the player how receives the message.
	 */
	private void sendSyntaxError(Player p) {
		MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, syntaxErrorMessage, p);
	}

	/**
	 * Saves the list in to the config.yml .
	 * 
	 * @param list 0 meaning sortingBlacklist, 1 inventoryBlacklist.
	 */
	private void safeList(int list) {

		if (list == 0) {
			PluginConfig.getInstance().setIntoConfig(ConfigPath.BLACKLIST.getPath(),
					getStringListFormMaterialList(InventorySorter.blacklist));
		} else if (list == 1) {
			PluginConfig.getInstance().setIntoConfig(ConfigPath.INVENTORY_BLACKLIST.getPath(),
					getStringListFormMaterialList(inventoryBlacklist));
		}

	}

	/**
	 * Converts an ArrayList of Materials into an ArrayList of Strings.
	 * 
	 * @param materialList a ArrayList of Materials.
	 * @return a ArrayList of Strings.
	 */
	private List<String> getStringListFormMaterialList(List<Material> materialList) {
		List<String> list = new ArrayList<>();

		for (Material material : materialList) {
			list.add(material.name());
		}
		return list;
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
	private final String inventoriesSubCommand = "inventories";

	/* syntax messages */
	private final String syntaxErrorMessage = "/blacklist <sorting/inventory> <addMaterial/removeMaterial/list>";

}
