package chestcleaner.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import chestcleaner.main.Main;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.timer.Timer;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

/**
 * @author Tom2208
 */
public class SortingListener implements org.bukkit.event.Listener {

	@EventHandler
	private void onRightClick(PlayerInteractEvent e) {

		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand().clone();
		item.setDurability((short) 0);
		item.setAmount(1);

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (item.equals(Main.item)) {

				if (p.isSneaking()) {

					if (p.hasPermission("chestcleaner.cleaningitem.use.owninventory")) {
						if (!Timer.playerCheck(p))
							return;

						damageItem(p);

						InventorySorter.sortPlayerInv(p);
						InventorySorter.playSortingSound(p);

						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);
						
						e.setCancelled(true);
					}

				} else if (!Main.eventmode) {

					if (p.hasPermission("chestcleaner.cleaningitem.use")) {

						Block b = BlockDetector.getTargetBlock(p);
						if (!Timer.playerCheck(p)) {
							return;
						}

						if (InventorySorter.sortPlayerBlock(b, p)) {

							damageItem(p);

							MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
									StringTable.getMessage(MessageID.INVENTORY_SORTED), p);
							e.setCancelled(true);
						}

					}

				}
			}

		}
	}

	/**
	 * Damages the item in the Hand of the {@code player} (using
	 * player.getItemInHand()), if the {@code durability} (in class Main) is
	 * true. Damaging means, that stackable items (maxStackSize > 1) get reduced
	 * in amount by one, not stackable items get damaged and removed, if they
	 * reach the highest durability .
	 * 
	 * @param player
	 *            the player who is holding the item, that you want to get
	 *            damaged, in hand.
	 */
	private void damageItem(Player player) {
		if (Main.durability) {
			player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));

			if (player.getItemInHand().getDurability() >= player.getItemInHand().getType().getMaxDurability()) {
				player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
			}
		}
	}

	@EventHandler
	private void onOpenInventory(InventoryOpenEvent e) {

		if (Main.eventmode) {

			Player p = (Player) e.getPlayer();
			ItemStack item = p.getItemInHand().clone();
			item.setDurability((short) 0);
			item.setAmount(1);

			if (p.hasPermission("chestcleaner.cleaningitem.use")) {

				if (item.equals(Main.item)) {

					if (!Timer.playerCheck(p))
						return;

					InventorySorter.sortInventory(e.getInventory());
					InventorySorter.playSortingSound(p);

					damageItem(p);

					e.setCancelled(true);
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
							StringTable.getMessage(MessageID.INVENTORY_SORTED), p);
				}

			}

		}

	}

}
