package chestcleaner.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import chestcleaner.sorting.CooldownManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.messages.MessageSystem;

public class SortingUtils {

	public static boolean sortPlayerInvWithEffects(Player player) {
		if (!CooldownManager.getInstance().isPlayerOnCooldown(player)) {
			if (InventorySorter.sortPlayerInventory(player)) {
				InventorySorter.playSortingSound(player);
				MessageSystem.sendSortedMessage(player);
				return true;
			}
		}
		return false;
	}

	public static boolean sortInventoryWithEffects(Inventory inv, Player player) {
		if (!CooldownManager.getInstance().isPlayerOnCooldown(player)) {
			if (InventorySorter.sortInventory(inv, player)) {
				InventorySorter.playSortingSound(player);
				MessageSystem.sendSortedMessage(player);
				return true;
			}
		}
		return false;
	}

}
