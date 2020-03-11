package chestcleaner.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.main.ChestCleaner;
import chestcleaner.main.ChestCleaner.ConfigPath;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.MaterialListUtils;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

/**
 * A command class representing the blacklist command. Blacklist Command
 * explained: https://github.com/tom2208/ChestCleaner/wiki/Command-blacklist
 * 
 * @author Tom2208
 *
 */
public class BlacklistCommand implements CommandExecutor, TabCompleter {

	// A list of all first argument (index 0) sub-commands (we use this for the
	// TabCompleter)
	private final List<String> commandList = new ArrayList<>();
	// A list of Strings with the types of blacklists.
	private final List<String> lists = new ArrayList<>();
	// The length of a page one the list if it gets displayed in the in game chat.
	private final int LIST_PAGE_LENGTH = 8;
	// This List is the global list of blacklisted inventories, it get used in other
	// classes etc.
	public static ArrayList<Material> inventoryBlacklist = new ArrayList<>();

	public BlacklistCommand() {
		lists.add("sorting");
		lists.add("inventories");

		commandList.add("addMaterial");
		commandList.add("removeMaterial");
		commandList.add("list");
		commandList.add("clear");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (!(sender instanceof Player)) {
			MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.NOT_A_PLAYER);
			return true;
		}

		Player p = (Player) sender;

		if (p.hasPermission("chestcleaner.cmd.blacklist")) {

			if (args.length <= 1) {
				sendSyntaxError(p);
				return true;
			}

			if (args.length >= 2 && args.length <= 3) {

				/* initialize list */
				ArrayList<Material> list;
				int listNumber = -1;
				if (args[0].equalsIgnoreCase(lists.get(0))) {
					list = InventorySorter.blacklist;
					listNumber = 0;
				} else if (args[0].equalsIgnoreCase(lists.get(1))) {
					list = inventoryBlacklist;
					listNumber = 1;
				} else {
					sendSyntaxError(p);
					return true;
				}

				/** subCommands */

				/*--------------- addMaterial ---------------*/
				if (args[1].equalsIgnoreCase(commandList.get(0))) {

					Material material;

					// addMaterial with name
					if (args.length == 3) {

						material = Material.getMaterial(args[2]);

						if (material == null) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR,
									StringTable.getMessage(MessageID.NO_MATERIAL_FOUND, "%material", args[2]), p);
							return true;
						}

						// addMaterial with item in hand
					} else {
						material = getMaterialFormPlayerHand(p);

						if (material.equals(Material.AIR)) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.HOLD_AN_ITEM, p);
							return true;
						}

					}

					if (list.contains(material)) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR,
								StringTable.getMessage(MessageID.IS_ALREADY_ON_BLACKLIST, "%material", material.name()),
								p);
						return true;
					}

					list.add(material);
					safeList(listNumber);

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
							StringTable.getMessage(MessageID.SET_TO_BLACKLIST, "%material", material.name()), p);
					return true;

					/*--------------- removeMaterial ---------------*/
				} else if (args[1].equalsIgnoreCase(commandList.get(1))) {

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
									MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable.getMessage(
											MessageID.INDEX_OUT_OF_BOUNDS, "%biggestindex", String.valueOf(index)), p);
									return true;
								}

							} catch (NumberFormatException ex) {

								MessageSystem.sendMessageToPlayer(MessageType.ERROR,
										StringTable.getMessage(MessageID.NO_MATERIAL_FOUND, "%material", args[2]), p);
								return true;

							}

						}

						// removeMaterial with item in hand
					} else {
						material = getMaterialFormPlayerHand(p);

						if (material.equals(Material.AIR)) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.HOLD_AN_ITEM, p);
							return true;
						}

					}

					if (!list.contains(material)) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable
								.getMessage(MessageID.BLACKLIST_DOESNT_CONTAINS, "%material", material.name()), p);
						return true;
					}

					list.remove(material);
					safeList(listNumber);

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
							StringTable.getMessage(MessageID.REMOVED_FORM_BLACKLIST, "%material", material.name()), p);
					return true;

					/*--------------- list ---------------*/
				} else if (args[1].equalsIgnoreCase(commandList.get(2))) {

					if (list.size() == 0) {
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.BLACKLIST_IS_EMPTY, p);
						return true;
					}

					int page = 1;
					int pages = list.size() / LIST_PAGE_LENGTH + 1;

					if (args.length == 3) {

						try {

							page = Integer.valueOf(args[2]);

						} catch (NumberFormatException ex) {

							MessageSystem.sendMessageToPlayer(MessageType.ERROR,
									StringTable.getMessage(MessageID.INVALID_INPUT_FOR_INTEGER, "%index", args[1]), p);
							return true;

						}

						if (!(page > 0 && page <= pages)) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR,
									StringTable.getMessage(MessageID.INVALID_PAGE_NUMBER, "%range", "1 - " + pages), p);
							return true;
						}

					}

					MaterialListUtils.sendListPageToPlayer(list, p, page, LIST_PAGE_LENGTH, pages);
					return true;

					/*--------------- clear ---------------*/
				} else if (args[1].equalsIgnoreCase(commandList.get(3))) {

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
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION, "chestcleaner.cmd.blacklist", p);
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
		MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
				"/blacklist <sorting/inventory> <addMaterial/removeMaterial/list>", p);
	}

	/**
	 * Saves the list in to the config.yml .
	 * 
	 * @param list 0 meaning sortingBlacklist, 1 inventoryBlacklist.
	 */
	private void safeList(int list) {

		if (list == 0) {
			ChestCleaner.main.getConfig().set(ConfigPath.BLACKLIST.getPath(), InventorySorter.blacklist);
		} else if (list == 1) {
			ChestCleaner.main.getConfig().set(ConfigPath.INVENTORY_BLACKLIST.getPath(), inventoryBlacklist);
		}

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();

		if (args.length <= 1) {

			StringUtil.copyPartialMatches(args[0], lists, completions);

		} else if (args.length == 2) {

			StringUtil.copyPartialMatches(args[1], commandList, completions);

		}

		return completions;
	}

}
