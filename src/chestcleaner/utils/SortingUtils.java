package chestcleaner.utils;

import chestcleaner.cooldown.CMRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.messages.MessageSystem;

public class SortingUtils {

	public static boolean sortPlayerInvWithEffects(Player player) {
		if (!CMRegistry.isOnCooldown(CMRegistry.CMIdentifier.SORTING, player)) {
			if (InventorySorter.sortPlayerInventory(player)) {
				InventorySorter.playSortingSound(player);
				MessageSystem.sendSortedMessage(player);
				return true;
			}
		}
		return false;
	}

	public static boolean sortInventoryWithEffects(Inventory inv, Player player) {
		if (!CMRegistry.isOnCooldown(CMRegistry.CMIdentifier.SORTING, player)) {
			if (InventorySorter.sortInventory(inv, player)) {
				InventorySorter.playSortingSound(player);
				MessageSystem.sendSortedMessage(player);
				return true;
			}
		}
		return false;
	}

}
