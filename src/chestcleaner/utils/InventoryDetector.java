package chestcleaner.utils;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryDetector {

	/**
	 * <b>Returns the inventory of the block {@code b}. If there is no inventory
	 * it will return {@code null}.</b> This method checks if the object of the
	 * block is an instance of the class org.bukkit.block.Container and returns
	 * its inventory.
	 * 
	 * @param b
	 *            The Block you want to get the inventory form.
	 * @return Returns the inventory of the container of the block, if its has
	 *         no container it returns {@code null}.
	 */
	public static Inventory getInventoryFormBlock(Block b) {
		if (b.getState() instanceof InventoryHolder) {
			InventoryHolder h = (InventoryHolder) b.getState();
			return h.getInventory();
		}
		return null;
	}

	public static boolean hasInventoryHolder(Block b) {
		if (b.getState() instanceof InventoryHolder) {
			return true;
		}
		return false;
	}

	/**
	 * <b>Returns the inventory of the block on the location {@code location} in
	 * the world {@code world}. If the block has no inventory it will return
	 * {@code null}.</b> This method checks if the object of the block is an
	 * instance of the class org.bukkit.block.Container and returns its
	 * inventory.
	 * 
	 * @param location
	 *            The location of the block you want to get the inventory from.
	 * @param world
	 *            The world of the block you want to get the inventory form.
	 * @return Returns the inventory of the container of the block, if its has
	 *         no container it returns {@code null}.
	 */
	public static Inventory getInventoryFormLocation(Location location, World world) {
		return getInventoryFormBlock(world.getBlockAt(location));
	}

	public static ArrayList<ItemStack> getPlayerInventoryList(Player p) {
		ArrayList<ItemStack> items = new ArrayList<>();

		for (int i = 9; i < 36; i++) {
			if (p.getInventory().getItem(i) != null)
				items.add(p.getInventory().getItem(i).clone());
		}

		return items;
	}
	
	public static ItemStack[] getFullInventory(Inventory inv){
		
		ItemStack[] items = new ItemStack[36];
		
		for(int i = 0; i < items.length; i++){
			items[i] = inv.getItem(i);
		}
		
		return items;
		
	}

}