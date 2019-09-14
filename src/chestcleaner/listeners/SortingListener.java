package chestcleaner.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import chestcleaner.commands.BlacklistCommand;
import chestcleaner.main.Main;
import chestcleaner.playerdata.PlayerDataManager;
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
		ItemStack itemMainHand = p.getInventory().getItemInMainHand().clone();
		itemMainHand.setDurability((short) 0);
		itemMainHand.setAmount(1);

		ItemStack itemOffHand = p.getInventory().getItemInOffHand().clone();
		itemOffHand.setDurability((short) 0);
		itemOffHand.setAmount(1);

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			boolean isMainHand = itemMainHand.equals(Main.item);
			boolean isOffHand = itemOffHand.equals(Main.item);

			// TODO RIGHTCLICK WIRD WOHL ZWEI MAL AUFGERUFEN, WENN MAN IN BIEDEN
			// SLOTS DAS ITEM HÄLT

			if ((isMainHand || isOffHand) && (isMainHand != isOffHand)) {

				if (p.isSneaking()) {

					if (p.hasPermission("chestcleaner.cleaningitem.use.owninventory")) {
						if (!Timer.playerCheck(p))
							return;

						damageItem(p, isMainHand);
						InventorySorter.sortPlayerInv(p, PlayerDataManager.getSortingPatternOfPlayer(p), PlayerDataManager.getEvaluatorTypOfPlayer(p));
						InventorySorter.playSortingSound(p);

						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.INVENTORY_SORTED, p);

						e.setCancelled(true);
					}

				} else if (!Main.eventmode) {

					if (p.hasPermission("chestcleaner.cleaningitem.use")) {

						Block b = BlockDetector.getTargetBlock(p);

						if (BlacklistCommand.inventoryBlacklist.contains(b.getType())) {
							return;
						}

						if (!Timer.playerCheck(p)) {
							return;
						}

						if (InventorySorter.sortPlayerBlock(b, p, PlayerDataManager.getSortingPatternOfPlayer(p), PlayerDataManager.getEvaluatorTypOfPlayer(p))) {

							damageItem(p, isMainHand);

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
	private void damageItem(Player player, boolean isHoldingInMainHand) {

		if (Main.durability) {

			ItemStack item;
			if (isHoldingInMainHand) {
				item = player.getInventory().getItemInMainHand();
			} else {
				item = player.getInventory().getItemInOffHand();
			}

			if (item.getMaxStackSize() == 1)
				item.setDurability((short) (item.getDurability() + 1));

			if (item.getDurability() >= item.getType().getMaxDurability()) {
				item.setAmount(item.getAmount() - 1);
			}
		}

	}

	@EventHandler
	private void onOpenInventory(InventoryOpenEvent e) {

		if (Main.eventmode) {

			Player p = (Player) e.getPlayer();

			if (p.hasPermission("chestcleaner.cleaningitem.use")) {

				ItemStack itemMainHand = p.getInventory().getItemInMainHand().clone();
				itemMainHand.setDurability((short) 0);
				itemMainHand.setAmount(1);

				ItemStack itemOffHand = p.getInventory().getItemInOffHand().clone();
				itemOffHand.setDurability((short) 0);
				itemOffHand.setAmount(1);

				boolean isMainHand = itemMainHand.equals(Main.item);
				boolean isOffHand = itemOffHand.equals(Main.item);

				if (isMainHand || isOffHand) {
					
					if (!Timer.playerCheck(p))
						return;

					InventorySorter.sortInventory(e.getInventory(), PlayerDataManager.getSortingPatternOfPlayer(p), PlayerDataManager.getEvaluatorTypOfPlayer(p));
					InventorySorter.playSortingSound(p);

					damageItem(p, isMainHand);

					e.setCancelled(true);
					MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
							StringTable.getMessage(MessageID.INVENTORY_SORTED), p);
				}

			}

		}

	}

}
