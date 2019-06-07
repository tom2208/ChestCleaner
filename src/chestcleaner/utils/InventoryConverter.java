package chestcleaner.utils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryConverter {

	/**
	 * <b>Converts an inventory into a ArrayList of ItemStacks and returns
	 * it.</b> Air will get removed.
	 * 
	 * @param inv
	 *            The inventory you want to convert into an ArrayList.
	 * @return Returns an ArrayList of ItemStacks you got from the inventory.
	 * @throws IllegalArgumentException
	 *             throws if the argument Inventory {@code inv} is equal to
	 *             null.
	 */
	public static ArrayList<ItemStack> getArrayListFormInventory(Inventory inv) {

		if (inv == null) {
			throw new IllegalArgumentException();
		}

		ArrayList<ItemStack> list = new ArrayList<>();

		for (ItemStack item : inv) {
			if (item != null) {
				if (!item.getType().equals(Material.AIR)) {
					list.add(item);
				}
			}
		}
		return list;
	}

	/**
	 * <b>Sets the items of the {@code inventory} to the ItemStacks of the
	 * ArrayList {@code items}.</b> The method clears the inventory before
	 * putting the items into the inventory.
	 * 
	 * @param inv
	 *            The inventory you want to put the items in.
	 * @param items
	 *            The list of items you want to put into the cleared inventory.
	 * @throws IllegalArgumentException
	 *             throws if the argument ItemStack {@code items} or the
	 *             Inventory {@code inv} is equal to null.
	 */
	public static void setItemsOfInventory(Inventory inv, ArrayList<ItemStack> items) {

		if (items == null || inv == null) {
			throw new IllegalArgumentException();
		}

		inv.clear();

		for (int i = 0; i < items.size(); i++) {
			inv.setItem(i, items.get(i));
		}

	}

	/**
	 * Sets the Item Stacks of the ArrayList into the slots of the
	 * player-inventory, just effects index 9 (including) to index 35
	 * (including).That means the hotbar or other important slots (armor slots,
	 * second hand slot) are getting avoided.
	 * 
	 * @param items
	 *            The list of ItemStacks you want to put into the player
	 *            inventory. Its size should be <= 27.
	 * @param p
	 *            the player whose inventory will get effected.
	 * @throws IllegalArgumentException
	 *             throws if the argument ItemStack {@code items} or the
	 *             Inventory {@code inv} is equal to null.
	 */
	public static void setPlayerInventory(ArrayList<ItemStack> items, Player p) {

		if (items == null || p == null) {
			throw new IllegalArgumentException();
		}

		for (int i = 9; i < 36; i++) {
			p.getInventory().clear(i);
		}

		for (int i = 0; i < items.size(); i++) {
			p.getInventory().setItem(i + 9, items.get(i));
		}

	}

}
