package chestcleaner.utils;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.cooldown.CMRegistry;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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

	public static boolean isOnInventoryBlacklist(Block block, CommandSender sender){
		if (PluginConfigManager.getBlacklistInventory().contains(block.getType())) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_BLACKLIST_INVENTORY, sender);
			return true;
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
