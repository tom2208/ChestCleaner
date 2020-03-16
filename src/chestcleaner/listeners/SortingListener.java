package chestcleaner.listeners;

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

		Player p = e.getPlayer();
		ItemStack itemMainHand = p.getInventory().getItemInMainHand().clone();
		itemMainHand.setDurability((short) 0);
		itemMainHand.setAmount(1);

		ItemStack itemOffHand = p.getInventory().getItemInOffHand().clone();
		itemOffHand.setDurability((short) 0);
		itemOffHand.setAmount(1);

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			boolean isMainHand = itemMainHand.equals(ChestCleaner.item);
			boolean isOffHand = itemOffHand.equals(ChestCleaner.item);

			// TODO RIGHTCLICK WIRD WOHL ZWEI MAL AUFGERUFEN, WENN MAN IN BIEDEN
			// SLOTS DAS ITEM HÄLT

			if ((isMainHand || isOffHand) && (isMainHand != isOffHand)) {

				if (p.isSneaking()) {

					if (p.hasPermission(PluginPermissions.CLEANING_ITEM_USE_OWN_INV.getString())) {
						if (!CooldownManager.getInstance().isPlayerOnCooldown(p))
							return;

						damageItem(p, isMainHand);
						InventorySorter.sortPlayerInv(p, PlayerDataManager.getInstance().getSortingPatternOfPlayer(p),
								PlayerDataManager.getInstance().getEvaluatorTypOfPlayer(p));
						InventorySorter.playSortingSound(p);

						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);

						e.setCancelled(true);
					}

				} else if (!ChestCleaner.eventmode) {

					if (p.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())) {

						Block b = BlockDetector.getTargetBlock(p);
						
						if(!InventoryDetector.hasInventoryHolder(b)) return; 
						
						if (BlacklistCommand.inventoryBlacklist.contains(b.getType()) || !CooldownManager.getInstance().isPlayerOnCooldown(p)) {
							return;
						}

						if (InventorySorter.sortPlayerBlock(b, p,
								PlayerDataManager.getInstance().getSortingPatternOfPlayer(p),
								PlayerDataManager.getInstance().getEvaluatorTypOfPlayer(p))) {

							damageItem(p, isMainHand);

							MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);
							e.setCancelled(true);
						}

					}

				}
			}

		}

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
	private void damageItem(Player player, boolean isHoldingInMainHand) {

		if (ChestCleaner.durability) {

			ItemStack item;
			if (isHoldingInMainHand) {
				item = player.getInventory().getItemInMainHand();
			} else {
				item = player.getInventory().getItemInOffHand();
			}

	        ItemMeta itemMeta = item.getItemMeta();
            Damageable damageable = ((Damageable) itemMeta);
	        if (!(damageable.getDamage() + 1 >= item.getType().getMaxDurability())){
	            damageable.setDamage(damageable.getDamage() + 1);
	        }else {
	        	item.setAmount(item.getAmount() - 1);
	        }
	        item.setItemMeta(itemMeta);

		}

	}

	@EventHandler
	private void onOpenInventory(InventoryOpenEvent e) {

		if (ChestCleaner.eventmode) {

			Player p = (Player) e.getPlayer();

			if (p.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())) {

				ItemStack itemMainHand = p.getInventory().getItemInMainHand().clone();
				itemMainHand.setDurability((short) 0);
				itemMainHand.setAmount(1);

				ItemStack itemOffHand = p.getInventory().getItemInOffHand().clone();
				itemOffHand.setDurability((short) 0);
				itemOffHand.setAmount(1);

				boolean isMainHand = itemMainHand.equals(ChestCleaner.item);
				boolean isOffHand = itemOffHand.equals(ChestCleaner.item);

				if (isMainHand || isOffHand) {

					if (!CooldownManager.getInstance().isPlayerOnCooldown(p))
						return;

					InventorySorter.sortInventory(e.getInventory(),
							PlayerDataManager.getInstance().getSortingPatternOfPlayer(p),
							PlayerDataManager.getInstance().getEvaluatorTypOfPlayer(p));
					InventorySorter.playSortingSound(p);

					damageItem(p, isMainHand);

					e.setCancelled(true);
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);

				}

			}

		}

	}

	@EventHandler
	private void onCloseInventory(InventoryCloseEvent e) {

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
