package chestcleaner.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import chestcleaner.config.Config;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

public class BlackListCommand implements CommandExecutor, TabCompleter {

	private final List<String> blackListCommands = new ArrayList<>();
	private final int LIST_LENGTH = 8;

	public BlackListCommand() {
		blackListCommands.add("addMaterial");
		blackListCommands.add("removeMaterial");
		blackListCommands.add("list");
		blackListCommands.add("clear");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (!(sender instanceof Player)) {
			MessageSystem.sendConsoleMessage(MessageType.ERROR, MessageID.NOT_A_PLAYER);
			return true;
		}

		Player p = (Player) sender;

		if (p.hasPermission("chestcleaner.cmd.blacklist")) {

			if (args.length == 0) {
				MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
						"/blacklist <addMaterial/removeMaterial/list>", p);
				return true;
			}

			if (args[0].equalsIgnoreCase(blackListCommands.get(0))
					|| args[0].equalsIgnoreCase(blackListCommands.get(1))) {

				if (args.length == 2) {

					/* ADD MATERIAL WITH NAME */
					if (args[0].equalsIgnoreCase(blackListCommands.get(0))) {

						Material material = Material.getMaterial(args[1]);

						if (material != null) {

							if (InventorySorter.blacklist.contains(material)) {
								MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable.getMessage(
										MessageID.IS_ALREADY_ON_BLACKLIST, "%material", material.name()), p);
								return true;
							}

							InventorySorter.blacklist.add(material);
							Config.setBlackList(InventorySorter.blacklist);

							MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
									StringTable.getMessage(MessageID.SET_TO_BLACKLIST, "%material", material.name()),
									p);
							return true;

						} else {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR,
									StringTable.getMessage(MessageID.NO_MATERIAL_FOUND, "%material", args[1]), p);
							return true;
						}

						/* REMOVE MATERIAL WITH NAME OR ID */
					} else if (args[0].equalsIgnoreCase(blackListCommands.get(1))) {

						for (int i = 0; i < InventorySorter.blacklist.size(); i++) {

							if (args[1].equalsIgnoreCase(InventorySorter.blacklist.get(i).name())) {

								MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
										StringTable.getMessage(MessageID.REMOVED_FORM_BLACKLIST, "%material",
												InventorySorter.blacklist.get(i).name()),
										p);

								InventorySorter.blacklist.remove(i);
								return true;

							}

						}

						try {
							int index = Integer.valueOf(args[1]) - 1;
							if (index > -1 && index < InventorySorter.blacklist.size()) {
								MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
										StringTable.getMessage(MessageID.REMOVED_FORM_BLACKLIST, "%material",
												InventorySorter.blacklist.get(index).name()),
										p);

								InventorySorter.blacklist.remove(index);
								Config.setBlackList(InventorySorter.blacklist);
							} else {

								MessageSystem.sendMessageToPlayer(
										MessageType.ERROR, StringTable.getMessage(MessageID.INDEX_OUT_OF_BOUNDS,
												"%biggestindex", String.valueOf(InventorySorter.blacklist.size() - 1)),
										p);
								return true;

							}
							return true;

						} catch (NumberFormatException ex) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR,
									StringTable.getMessage(MessageID.BLACKLIST_DOESNT_CONTAINS, "%material", args[1]),
									p);
							return true;
						}

					}

				}

				ItemStack item = p.getInventory().getItemInMainHand();

				if (item != null && item.getType() != Material.AIR) {

					/* ADD MATERIAL FORM HAND*/
					if (args[0].equalsIgnoreCase(blackListCommands.get(0))) {

						if (args.length == 1) {
							
							if(InventorySorter.blacklist.contains(item.getType())){
								MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable.getMessage(
										MessageID.IS_ALREADY_ON_BLACKLIST, "%material", item.getType().name()), p);
								return true;
							}
							
							InventorySorter.blacklist.add(item.getType());
							Config.setBlackList(InventorySorter.blacklist);

							MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, StringTable
									.getMessage(MessageID.SET_TO_BLACKLIST, "%material", item.getType().name()), p);
						} else {
							MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
									"/blacklist additem <materialname>", p);
						}
						return true;

						/* REMOVE MATERIAL FORM HAND*/
					} else if (args[0].equalsIgnoreCase(blackListCommands.get(1))) {

						if (InventorySorter.blacklist.size() == 0) {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.BLACKLIST_IS_EMPTY, p);
							return true;
						}

						if (args.length == 1) {

							if (InventorySorter.blacklist.contains(item.getType())) {

								InventorySorter.blacklist.remove(item.getType());
								MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, StringTable.getMessage(
										MessageID.REMOVED_FORM_BLACKLIST, "%material", item.getType().name()), p);
								Config.setBlackList(InventorySorter.blacklist);
								return true;

							} else {
								MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable.getMessage(
										MessageID.BLACKLIST_DOESNT_CONTAINS, "%material", item.getType().name()), p);
								Config.setBlackList(InventorySorter.blacklist);
								return true;
							}

						} else {
							MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
									"/blacklist removeMaterial <materialname/index>", p);
							return true;
						}

					} else {
						MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
								"/blacklist <addMaterial/removeMaterial/list>", p);
						return true;
					}

				} else {
					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.HOLD_AN_ITEM, p);
					return true;
				}

			} else if (args[0].equalsIgnoreCase(blackListCommands.get(2))) {

				if (InventorySorter.blacklist.size() == 0) {
					MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.BLACKLIST_IS_EMPTY, p);
					return true;
				}

				/* LIST */

				int pageNumber = InventorySorter.blacklist.size() / LIST_LENGTH;
				if (InventorySorter.blacklist.size() % LIST_LENGTH != 0)
					pageNumber++;

				if (args.length == 1) {

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
							StringTable.getMessage(MessageID.BLACKLIST_TITLE, "%page", "1 / " + pageNumber), p);

					for (int i = 0; i < LIST_LENGTH; i++) {

						if (InventorySorter.blacklist.size() == i) {
							return true;
						} else {
							MessageSystem.sendMessageToPlayer(MessageType.UNHEADED_INFORMATION,
									(i + 1) + ". " + InventorySorter.blacklist.get(i).name(), p);
						}

					}

					if (InventorySorter.blacklist.size() > LIST_LENGTH) {
						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
								StringTable.getMessage(MessageID.NEXT_ENTRIES, "%nextpage", String.valueOf(2)), p);
					}
					return true;

				} else if (args.length == 2) {

					try {

						int index = Integer.valueOf(args[1]);

						if (index > 0 && index <= pageNumber) {

							MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, StringTable
									.getMessage(MessageID.BLACKLIST_TITLE, "%page", index + " / " + pageNumber), p);

							for (int i = (index - 1) * LIST_LENGTH; i < index * LIST_LENGTH; i++) {
								if (InventorySorter.blacklist.size() == i) {
									return true;
								} else {
									MessageSystem.sendMessageToPlayer(MessageType.UNHEADED_INFORMATION,
											(i + 1) + ". " + InventorySorter.blacklist.get(i).name(), p);
								}
							}

							if (pageNumber > index) {
								MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, StringTable
										.getMessage(MessageID.NEXT_ENTRIES, "%nextpage", String.valueOf(index + 1)), p);
							}
							return true;

						} else {
							MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable
									.getMessage(MessageID.INVALID_PAGE_NUMBER, "%range", "1 - " + pageNumber), p);
							return true;
						}

					} catch (NumberFormatException ex) {

						MessageSystem.sendMessageToPlayer(MessageType.ERROR,
								StringTable.getMessage(MessageID.INVALID_INPUT_FOR_INTEGER, "%index", args[1]), p);
						return true;

					}

				} else {
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, "/list <pagenumber>", p);
					return true;
				}

				/* CLEAR */
			} else if (args[0].equalsIgnoreCase(blackListCommands.get(3))) {

				InventorySorter.blacklist.clear();
				Config.setBlackList(InventorySorter.blacklist);
				MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.BLACKLIST_CLEARED, p);
				return true;

			} else {
				MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR,
						"/blacklist <addMaterial/removeMaterial/list>", p);
				return true;
			}

		} else {
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION, "chestcleaner.cmd.blacklist", p);
			return true;
		}

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		final List<String> completions = new ArrayList<>();

		StringUtil.copyPartialMatches(args[0], blackListCommands, completions);

		Collections.sort(completions);
		return completions;
	}

}
