package chestcleaner.listeners;

import org.bukkit.Bukkit;
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

			if (!Bukkit.getVersion().contains("1.8")) {

				Player p = e.getPlayer();
				if (p.getGameMode().equals(GameMode.SURVIVAL)) {

					ItemStack item = e.getItem();
					if (item.getAmount() == 1) {

						if (p.hasPermission(PluginPermissions.AUTOFILL_CONSUMABLES.getString())) {

							if (item.getMaxStackSize() > 1) {
								int hand = -1; // -1 = nothing, 0 = hand, 1 =
												// offhand

								if (p.getInventory().getItem(p.getInventory().getHeldItemSlot()) != null) {
									if (p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType()
											.equals(item.getType())) {
										hand = 0;
									} else if (p.getInventory().getItemInOffHand() != null) {
										if (p.getInventory().getItemInOffHand().getType().equals(item.getType())) {
											hand = 1;
										}
									}
								} else if (p.getInventory().getItemInOffHand() != null) {
									if (p.getInventory().getItemInOffHand().getType().equals(item.getType())) {
										hand = 1;
									}

								}

								if (hand > -1) {

									ItemStack[] items = InventoryDetector.getFullInventory(p.getInventory());
									for (int i = 9; i < 36; i++) {
										if (items[i] != null) {
											if (items[i].getType().equals(item.getType()) && items[i].getAmount() > 1) {
												items[i].setAmount(items[i].getAmount() + 1);
												if (hand == 0) {
													e.setItem(items[i]);
													p.getInventory().setItem(i, null);
													break;
												} else if (hand == 1) {
													e.setItem(items[i]);
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
			}
		}

	}

}
