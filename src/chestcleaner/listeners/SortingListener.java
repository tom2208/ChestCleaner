package chestcleaner.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.CooldownManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.InventoryDetector;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

/**
 * @author Tom2208
 */
public class SortingListener implements org.bukkit.event.Listener {

	@EventHandler
	private void onRightClick(PlayerInteractEvent e) {

		Player player = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (PluginConfigManager.isCleaningItemActive()
					&& isPlayerHoldingACleaningItem(player)
					&& !CooldownManager.getInstance().isPlayerOnCooldown(player)) {

				if (isPlayerAllowedToCleanOwnInv(player) && InventorySorter.sortPlayerInventory(player)) {
					damageItem(player);
					InventorySorter.playSortingSound(player);

					MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_INVENTORY_SORTED, player);
					e.setCancelled(true);

				} else if (!PluginConfigManager.isOpenEvent()
						&& player.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())) {

					Block b = BlockDetector.getTargetBlock(player);

					if (!InventoryDetector.hasInventoryHolder(b)
							|| PluginConfigManager.getBlacklistInventory().contains(b.getType()))
						return;

					if (InventorySorter.sortPlayerBlock(b, player)) {
						damageItem(player);
						MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_INVENTORY_SORTED, player);
						e.setCancelled(true);
					}
				}
			}
		}
	}

	private boolean isPlayerHoldingACleaningItem(Player player) {
		return isPlayerHoldingCleaningItemInMainHand(player) || isPlayerHoldingCleaningItemInOffHand(player);
	}

	private ItemStack getCompairableItem(ItemStack item) {
		ItemStack compItem = item.clone();
		ItemMeta itemMeta = compItem.getItemMeta();
		Damageable damageable = ((Damageable) itemMeta);
		damageable.setDamage(0);
		compItem.setItemMeta(itemMeta);
		return compItem;
	}

	private boolean isPlayerHoldingCleaningItemInMainHand(Player player) {
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType().equals(Material.AIR)) {
			return false;
		}
		return getCompairableItem(item).isSimilar(PluginConfigManager.getCleaningItem());
	}

	private boolean isPlayerHoldingCleaningItemInOffHand(Player player) {
		ItemStack item = player.getInventory().getItemInOffHand();
		if (item.getType().equals(Material.AIR)) {
			return false;
		}
		return getCompairableItem(item).isSimilar(PluginConfigManager.getCleaningItem());
	}

	private boolean isPlayerAllowedToCleanOwnInv(Player player) {
		return player.hasPermission(PluginPermissions.CLEANING_ITEM_USE_OWN_INV.getString()) && player.isSneaking();
	}

	/**
	 * Damages the item in the Hand of the {@code player} (using
	 * player.getItemInHand()), if the {@code durability} (in class Main) is true.
	 * Damaging means, that stackable items (maxStackSize > 1) get reduced in amount
	 * by one, not stackable items get damaged and removed, if they reach the
	 * highest durability .
	 * 
	 * @param player the player who is holding the item, that you want to get
	 *               damaged, in hand.
	 */
	private void damageItem(Player player) {

		if (PluginConfigManager.isDurabilityLossActive()) {

			ItemStack item;
			if (isPlayerHoldingCleaningItemInMainHand(player)) {
				item = player.getInventory().getItemInMainHand();
			} else {
				item = player.getInventory().getItemInOffHand();
			}

			ItemMeta itemMeta = item.getItemMeta();
			Damageable damageable = ((Damageable) itemMeta);

			if (!(damageable.getDamage() + 1 >= item.getType().getMaxDurability())) {
				damageable.setDamage(damageable.getDamage() + 1);
			} else {
				item.setAmount(item.getAmount() - 1);
			}
			item.setItemMeta(itemMeta);

		}

	}

	@EventHandler
	private void onOpenInventory(InventoryOpenEvent e) {

		if (PluginConfigManager.isCleaningItemActive()
				&& PluginConfigManager.isOpenEvent()) {

			Player player = (Player) e.getPlayer();

			if (player.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())
					&& isPlayerHoldingACleaningItem(player)
					&& !CooldownManager.getInstance().isPlayerOnCooldown(player)
					&& InventorySorter.sortInventory(e.getInventory(), player)) {

				damageItem(player);
				InventorySorter.playSortingSound(player);
				MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_INVENTORY_SORTED, player);
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onCloseInventory(InventoryCloseEvent e) {
		// Doing the auto sorting here
		if (e.getInventory().getType().equals(InventoryType.ENDER_CHEST)
				|| e.getInventory().getType().equals(InventoryType.CHEST)) {

			Player p = (Player) e.getPlayer();

			if (PlayerDataManager.isAutoSort(p)
					&& !CooldownManager.getInstance().isPlayerOnCooldown(p)
					&& InventorySorter.sortInventory(e.getInventory(), p)) {

				InventorySorter.playSortingSound(p);
				MessageSystem.sendMessageToCS(MessageType.SUCCESS, MessageID.INFO_INVENTORY_SORTED, p);
			}
		}
	}
}
