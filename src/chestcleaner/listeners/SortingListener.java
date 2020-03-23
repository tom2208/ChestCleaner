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

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.main.ChestCleaner;
import chestcleaner.sorting.CooldownManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.InventoryDetector;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.PlayerDataManager;
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

			if (isPlayerHoldingACleaningItem(player)) {

				if (isPlayerAllowedToCleanOwnInv(player)) {

					if (!CooldownManager.getInstance().isPlayerOnCooldown(player))
						return;

					damageItem(player);
					InventorySorter.sortPlayerInv(player);
					InventorySorter.playSortingSound(player);

					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, player);

					e.setCancelled(true);

				} else if (!PluginConfigManager.getInstance().isEventModeActive()) {

					if (player.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())) {

						Block b = BlockDetector.getTargetBlock(player);

						if (!InventoryDetector.hasInventoryHolder(b))
							return;

						if (BlacklistCommand.inventoryBlacklist.contains(b.getType())
								|| !CooldownManager.getInstance().isPlayerOnCooldown(player)) {
							return;
						}

						damageItem(player);

						if (InventorySorter.sortPlayerBlock(b, player)) {

							MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, player);
							e.setCancelled(true);
						}

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
		return getCompairableItem(item).isSimilar(ChestCleaner.item);
	}

	private boolean isPlayerHoldingCleaningItemInOffHand(Player player) {
		ItemStack item = player.getInventory().getItemInOffHand();
		if (item.getType().equals(Material.AIR)) {
			return false;
		}
		return getCompairableItem(item).isSimilar(ChestCleaner.item);
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

		if (PluginConfigManager.getInstance().isDurabilityLossActive()) {

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

		if (PluginConfigManager.getInstance().isEventModeActive()) {

			Player player = (Player) e.getPlayer();

			if (player.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())) {

				if (isPlayerHoldingACleaningItem(player)) {

					if (!CooldownManager.getInstance().isPlayerOnCooldown(player))
						return;

					InventorySorter.sortInventory(e.getInventory(),
							PlayerDataManager.getInstance().getSortingPatternOfPlayer(player),
							PlayerDataManager.getInstance().getEvaluatorTypOfPlayer(player));
					InventorySorter.playSortingSound(player);

					e.setCancelled(true);
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, player);

					damageItem(player);

				}

			}

		}

	}

	@EventHandler
	private void onCloseInventory(InventoryCloseEvent e) {
		/**
		 * Doing the auto sorting here
		 */
		if (e.getInventory().getType().equals(InventoryType.ENDER_CHEST)
				|| e.getInventory().getType().equals(InventoryType.CHEST)) {

			Player p = (Player) e.getPlayer();

			if (PlayerDataManager.getInstance().getAutoSortOfPlayer(p)) {

				if (!CooldownManager.getInstance().isPlayerOnCooldown(p)) {
					return;
				}

				InventorySorter.sortInventoryByPlayer(e.getInventory(), p);
				InventorySorter.playSortingSound(p);
				MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);

			}

		}

	}

}
