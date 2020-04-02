package chestcleaner.sorting;

import java.util.ArrayList;
import java.util.List;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.v2.Categorizers;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import chestcleaner.utils.InventoryConverter;
import chestcleaner.utils.InventoryDetector;

public class InventorySorter {

	public static ArrayList<Material> blacklist = new ArrayList<>();

	/**
	 * Returns {@code list} sorted in full stacked items.
	 * 
	 * @param list an ArrayList of ItemStacks you want to sort
	 * @return full stacked {@code list};
	 */
	private static List<ItemStack> getFullStacks(List<ItemStack> list) {

		ArrayList<ItemStack> items = new ArrayList<>();
		ArrayList<Integer> amounts = new ArrayList<>();

		boolean blackListedItemUsed = false;

		for (ItemStack item : list) {

			int amount = item.getAmount();

			item.setAmount(1);

			if (blacklist.contains(item.getType())) {
				items.add(item);
				amounts.add(amount);
				blackListedItemUsed = true;
			} else {

				int index = -1;
				for (int j = 0; j < items.size(); j++) {
					if (items.get(j).isSimilar(item)) {
						index = j;
						break;
					}
				}

				if (index >= 0) {
					amounts.set(index, amounts.get(index) + amount);
				} else {
					items.add(item);
					amounts.add(amount);
				}
			}

		}

		ArrayList<ItemStack> out = new ArrayList<>();

		for (int i = 0; i < items.size(); i++) {
			if(items.get(i).getType().equals(Material.AIR)) {
				continue;
			}
			int stacks = (amounts.get(i) / items.get(i).getType().getMaxStackSize());
			for (int j = 0; j < stacks; j++) {
				ItemStack item = items.get(i).clone();
				item.setAmount(items.get(i).getMaxStackSize());
				out.add(item);
			}

			int remainingItems = amounts.get(i) % items.get(i).getMaxStackSize();
			if (remainingItems > 0) {
				ItemStack item = items.get(i).clone();
				item.setAmount(remainingItems);
				out.add(item);
			}

		}

		if (blackListedItemUsed) {
			AmountSorter sorter = new AmountSorter(out);
			return sorter.sortArray();
		}

		return out;

	}

	/**
	 * Sorts any kind of inventory.
	 * 
	 * @param inv the inventory you want to sort.
	 */
	public static boolean sortInventory(Inventory inv, Player p) {

		List<ItemStack> list = InventoryConverter.getArrayListFromInventory(inv);
		List<String> categorizerConfig = PluginConfigManager.getInstance().getCategorizationOrder();
		SortingPattern pattern = SortingPattern.DEFAULT;

		if(list == null) {
			return false;
		}

		if(p != null) {
			// categorizerConfig = PlayerDataManager.getInstance().getCategorizationOrder(p);
			pattern = PlayerDataManager.getInstance().getSortingPatternOfPlayer(p);
		}

		if (list.size() <= 1) {
			InventoryConverter.setItemsOfInventory(inv, list, pattern);
		}

		list = Categorizers.sort(list, categorizerConfig);
		list = getFullStacks(list);

		InventoryConverter.setItemsOfInventory(inv, list, pattern);
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
			if (p != null) {
				playSortingSound(p);
			}
			sortInventory(inv, p);
			return true;
		}

		if (p != null) {
			if (b.getBlockData().getMaterial() == Material.ENDER_CHEST) {
				playSortingSound(p);
				sortInventory(p.getEnderChest(), p);
				return true;
			}
		}

		return false;
	}

	public static void playSortingSound(Player p) {
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PIG_SADDLE, 2F, 2F);
	}

}
