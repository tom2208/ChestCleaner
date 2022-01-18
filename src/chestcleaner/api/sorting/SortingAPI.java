package chestcleaner.api.sorting;

import chestcleaner.sorting.InventorySorter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Handy methods to sort inventories.
 */
public class SortingAPI {

    /**
     * Triggers a SortingEvent to sort an inventory.
     * <p>Careful! The method sorts inventories without respect of the kind of inventory.
     * If you want to sort a player inventory without effecting armor slots,
     * hotbar or second hand use the sortPlayerInventory() method.</p>
     *
     * @param inventory the inventory.
     * @param player    the player who is sorting (can be null)
     * @return returns true after the inventory got sorted thus the event passed (did not get canceled).
     */
    public static boolean sortInventory(Inventory inventory, Player player) {
        return InventorySorter.sortInventory(inventory, player);
    }

    /**
     * Triggers a SortingEvent to sort an inventory.
     * <p>Careful! The method sorts inventories without respect of the kind of inventory.
     * If you want to sort a player inventory without effecting armor slots,
     * hotbar or second hand use the sortPlayerInventory() method.</p>
     *
     * @param inventory the inventory.
     * @return returns true after the inventory got sorted thus the event passed (did not get canceled).
     */
    public static boolean sortInventory(Inventory inventory) {
        return InventorySorter.sortInventory(inventory, null);
    }

    public static boolean sortPlayerInventory(Inventory inventory, Player player){

    }

}
