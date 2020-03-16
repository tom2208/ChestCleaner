package chestcleaner.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import chestcleaner.main.ChestCleaner;
import chestcleaner.utils.InventoryDetector;
import chestcleaner.utils.PluginPermissions;

public class RefillListener implements org.bukkit.event.Listener {

	@EventHandler
	private void onPlacingBlock(BlockPlaceEvent e) {

		if (ChestCleaner.blockRefill && !e.isCancelled()) {
			Player p = e.getPlayer();

			if (p.getGameMode().equals(GameMode.SURVIVAL)) {

				ItemStack item = e.getItemInHand();
				if (item.getAmount() == 1) {

					if (p.hasPermission(PluginPermissions.REFILL_BLOCKS.getString())) {

						Material material = e.getBlockPlaced().getType();

						if (material.toString().contains("STRIPPED")) {
							if (p.getInventory().getItemInMainHand().getType().toString().contains("_AXE")
									|| p.getInventory().getItemInOffHand().getType().toString().contains("_AXE")) {
								return;
							}
						}

						ItemStack[] items = InventoryDetector.getFullInventory(p.getInventory());

						for (int i = 9; i < 36; i++) {

							if (items[i] != null) {

								if (items[i].getType().equals(material)) {

									if (e.getHand().equals(EquipmentSlot.HAND)) {
										p.getInventory().setItemInMainHand(items[i]);
										p.getInventory().setItem(i, null);
										break;
									} else if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
										p.getInventory().setItemInOffHand(items[i]);
										p.getInventory().setItem(i, null);
										break;
									}

								}

							}

						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onConsuming(PlayerItemConsumeEvent e) {

		if (ChestCleaner.consumablesRefill && !e.isCancelled()) {

			Player player = e.getPlayer();

			if (isPlayerAllowedToRefill(player)) {

				ItemStack item = e.getItem();
				if (item.getAmount() == 1) {

					if (item.getMaxStackSize() > 1) {

						if (isPlayerHoldingAItemInMainHand(player)) {

							if (playerMainHandHeldItemMaterialEquals(item, player)) {
								refillSlot(true, e);

							} else if (isPlayerHoldingAItemInOffHand(player)) {

								if (playerOffHandHeldItemMaterialEquals(item, player)) {
									refillSlot(false, e);
								}

							}

						} else if (isPlayerHoldingAItemInOffHand(player)) {

							if (playerOffHandHeldItemMaterialEquals(item, player)) {
								refillSlot(false, e);
							}

						}

					}

				}

			}

		}

	}

	/**
	 * If the player is in survival mode and has the permission
	 * PluginPermissions.AUTOFILL_CONSUMABLES it returns true otherwise flase.
	 * 
	 * @param player the player you want to check.
	 * @return true if the player is allowed to auto refill otherwise false.
	 */
	private boolean isPlayerAllowedToRefill(Player player) {
		return player.getGameMode().equals(GameMode.SURVIVAL)
				&& player.hasPermission(PluginPermissions.AUTOFILL_CONSUMABLES.getString());
	}

	/**
	 * Returns true if the player has a item in his main hand.
	 * 
	 * @param player the player you want to check.
	 * @return true if the player holds an item in his main hand otherwise false.
	 */
	private boolean isPlayerHoldingAItemInMainHand(Player player) {
		return player.getInventory().getItem(player.getInventory().getHeldItemSlot()) != null;
	}

	/**
	 * Returns true if the player has a item in his off hand.
	 * 
	 * @param player the player you want to check.
	 * @return true if the player holds an item in his off hand otherwise false.
	 */
	private boolean isPlayerHoldingAItemInOffHand(Player player) {
		return player.getInventory().getItemInOffHand() != null;
	}

	/**
	 * If the Material of the {@code item} is equal to the material in the held item
	 * slot (main hand) it returns true otherwise false.
	 * 
	 * @param item   the item which material you want to compare.
	 * @param player the player owning the held item slot (main hand).
	 * @return true if the materials are equal otherwise false.
	 */
	private boolean playerMainHandHeldItemMaterialEquals(ItemStack item, Player player) {
		return player.getInventory().getItem(player.getInventory().getHeldItemSlot()).getType().equals(item.getType());
	}

	/**
	 * If the Material of the {@code item} is equal to the material in the off hand
	 * it returns true otherwise false.
	 * 
	 * @param item   the item which material you want to compare.
	 * @param player the player owning the off hand.
	 * @return true if the materials are equal otherwise false.
	 */
	private boolean playerOffHandHeldItemMaterialEquals(ItemStack item, Player player) {
		return player.getInventory().getItemInOffHand().getType().equals(item.getType());
	}

	/**
	 * Searches through the main inventory (slots 9 - 35) taking the first ItemStack
	 * with the same type, an amount bigger than 1 and puts it into the slot (+ one
	 * amount) while consuming.
	 * 
	 * @param inMainHand true if the refill slot is the main hand otherwise false.
	 * @param e          the consuming event the refill should happen.
	 */
	private void refillSlot(boolean inMainHand, PlayerItemConsumeEvent e) {
		ItemStack[] items = InventoryDetector.getFullInventory(e.getPlayer().getInventory());
		for (int i = 9; i < 36; i++) {
			if (items[i] != null) {
				if (items[i].getType().equals(e.getItem().getType()) && items[i].getAmount() > 1) {
					items[i].setAmount(items[i].getAmount() + 1);
					if (inMainHand) {
						e.setItem(items[i]);
						e.getPlayer().getInventory().setItem(i, null);
						break;
					} else {
						e.setItem(items[i]);
						e.getPlayer().getInventory().setItem(i, null);
						break;
					}

				}

			}

		}
	}

}
