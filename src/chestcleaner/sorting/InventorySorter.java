package chestcleaner.sorting;

import java.util.ArrayList;
import java.util.List;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import chestcleaner.utils.InventoryConverter;
import chestcleaner.utils.InventoryDetector;

public class InventorySorter {

	/**
	 * Returns {@code list} sorted in full stacked items.
	 * The amount of the ItemStack is increased beyond MaxStackSize, but only one ItemStack exists for each Material.
	 * Items on the stacking blacklist dont get their ammount increased. They are simply added to the list.
	 * So there may be multiple ItemStacks for materials on the stacking blacklist.
	 * 
	 * @param items an ArrayList of ItemStacks you want to sort
	 * @return full stacked {@code list};
	 */
	private static List<ItemStack> reduceStacks(List<ItemStack> items) {
		// temp list, every item once, amounts get added
		ArrayList<ItemStack> newList = new ArrayList<>();

		for (ItemStack item : items) {
			if (PluginConfigManager.getBlacklistStacking().contains(item.getType())) {
				newList.add(item);
			} else {
				ItemStack existingItem = newList.stream()
						.filter(tempItem -> tempItem.isSimilar(item))
						.findFirst().orElse(null);

				if (existingItem == null) {
					newList.add(item);
				} else {
					existingItem.setAmount(existingItem.getAmount() + item.getAmount());
				}
			}
		}
		return newList;
	}

	/**
	 * Returns {@code list} sorted in maxStackSize ItemStacks.
	 * If the amount is larger than maxStackSize, it will create a new ItemStack for that material.
	 * Items on the stacking blacklist are simply added to the list.
	 */
	private static List<ItemStack> expandStacks(List<ItemStack> items) {
		ArrayList<ItemStack> newList = new ArrayList<>();

		for (ItemStack item : items) {
			if (PluginConfigManager.getBlacklistStacking().contains(item.getType())) {
				newList.add(item);
			} else if (!item.getType().equals(Material.AIR)){
				while (item.getAmount() > 0) {
					int amount = Math.min(item.getAmount(), item.getMaxStackSize());
					ItemStack clone = item.clone();
					clone.setAmount(amount);
					newList.add(clone);
					item.setAmount(item.getAmount() - amount);
				}
			}
		}
		return newList;
	}

	public static boolean sortPlayerInventory(Player p) {
		return sortInventory(p.getInventory(), p, InventoryDetector.getPlayerInventoryList(p));
	}

	public static boolean sortInventory(Inventory inv, Player p) {
		return sortInventory(inv, p, InventoryConverter.getArrayListFromInventory(inv));
	}

	/**
	 * Sorts any kind of inventory.
	 * 
	 * @param inv the inventory you want to sort.
	 */
	public static boolean sortInventory(Inventory inv, Player p, List<ItemStack> items) {

		SortingEvent event = new SortingEvent(p, inv, items);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()){
			return false;
		}

		List<String> categoryNames = PluginConfigManager.getCategoryOrder();
		SortingPattern pattern = PluginConfigManager.getDefaultPattern();

		if(items == null || items.isEmpty()) {
			return false;
		}

		if(p != null) {
			if (p.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_CATEGORIES.getString())) {
				categoryNames = PlayerDataManager.getCategoryOrder(p);
			}
			if (p.hasPermission(PluginPermissions.CMD_SORTING_CONFIG_PATTERN.getString())) {
				pattern = PlayerDataManager.getSortingPattern(p);
			}
		}

		if (!CategorizerManager.validateExists(categoryNames)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_CATEGORY_INVALID, p);
			return false;
		}

		if (items.size() <= 1) {
			InventoryConverter.setItemsOfInventory(inv, items, pattern);
			return true;
		}

		items = reduceStacks(items);
		items = CategorizerManager.sort(items, categoryNames);
		items = expandStacks(items);

		InventoryConverter.setItemsOfInventory(inv, items, pattern);
		return true;
	}

	/**
	 * Checks if the block has an inventory or if it is an enderchest and sorts it.
	 * 
	 * @param b Block you want to get sorted.
	 * @param p the player or owner of an enderchest inventory.
	 * @return returns true if an inventory got sorted, otherwise false.
	 */
	public static boolean sortPlayerBlock(Block b, Player p) {

		Inventory inv = InventoryDetector.getInventoryFormBlock(b);

		if (inv != null) {
			return sortInventory(inv, p);
		}

		if (p != null && b != null) {
			if (b.getBlockData().getMaterial() == Material.ENDER_CHEST) {
				return sortInventory(p.getEnderChest(), p);
			}
		}

		return false;
	}

	public static void playSortingSound(Player p) {
		boolean flag;
		if(PlayerDataManager.containsSortingSound(p)) {
			flag = PlayerDataManager.isSortingSound(p);
		}else {
			flag = PluginConfigManager.getDefaultSortingSoundBoolean();
		}
		
		if(flag) {
			p.getWorld().playSound(p.getLocation(), PluginConfigManager.getDefaultSortingSound(),
					PluginConfigManager.getDefaultVolume(), PluginConfigManager.getDefaultPitch());
		}
	}

}
