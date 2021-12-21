package chestcleaner.sorting;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SortingEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCanceled = false;

    private Player player;
    private Inventory inventory;
    private List<ItemStack> list;

    public SortingEvent(Player player, Inventory inventory, List<ItemStack> list) {
        this.player = player;
        this.inventory = inventory;
        this.list = list;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Returns the player who is going to sort.
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the inventory that should get sorted.
     * @return the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns the list of items that is taken form the inventory which should be sorted. In the sorting process only
     * only this list gets modified and at the end it will replace the stacks in the real inventory.
     * @return the list
     */
    public List<ItemStack> getList() {
        return list;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCanceled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCanceled = b;
    }

}
