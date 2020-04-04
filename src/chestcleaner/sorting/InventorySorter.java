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
	 * @param items an ArrayList of ItemStacks you want to sort
	 * @return full stacked {@code list};
	 */
	private static List<ItemStack> getFullStacks(List<ItemStack> items) {
		// temp list, every item once, amounts get added
		ArrayList<ItemStack> tempItems = new ArrayList<>();

		for (ItemStack item : items) {
			if (blacklist.contains(item.getType())) {
				tempItems.add(item);
			} else {
				ItemStack temp = tempItems.stream()
						.filter(tempItem -> tempItem.isSimilar(item))
						.findFirst().orElse(null);

				if (temp == null) {
					tempItems.add(item);
				} else {
					temp.setAmount(temp.getAmount() + item.getAmount());
				}
			}
		}

		ArrayList<ItemStack> out = new ArrayList<>();

		for (ItemStack item : tempItems) {
			if (blacklist.contains(item.getType())) {
				out.add(item);
			} else if (!item.getType().equals(Material.AIR)){
				// get / set full stacks
				while (item.getAmount() > 0) {
					int amount = Math.min(item.getAmount(), item.getMaxStackSize());
					ItemStack clone = item.clone();
					clone.setAmount(amount);
					out.add(clone);
					item.setAmount(item.getAmount() - amount);
				}
			}
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
			return true;
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
