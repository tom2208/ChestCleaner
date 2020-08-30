package chestcleaner.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.main.ChestCleaner;
import chestcleaner.utils.InventoryDetector;
import chestcleaner.utils.PluginPermissions;

public class RefillListener implements org.bukkit.event.Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onPlacingBlock(BlockPlaceEvent e) {
		Player player = e.getPlayer();

		if (isPlayerAllowedToRefillBlocks(player)) {
			boolean config = PluginConfigManager.isDefaultBlockRefill();

			if (PlayerDataManager.containsRefillBlocks(player)) {
				config = PlayerDataManager.isRefillBlocks(player);
			}

			if (config) {

				ItemStack item = e.getItemInHand();

				if (item.getAmount() == 1) {

					if (!isOnBlackList(item)) {
						/*
						 * stripping a log counts as placing a block, at least some times (cause the axe
						 * to get replaced)
						 */
						if (e.getBlockPlaced().getType().toString().contains("STRIPPED")) {
							if (player.getInventory().getItemInMainHand().getType().toString().contains("_AXE")
									|| player.getInventory().getItemInOffHand().getType().toString().contains("_AXE")) {
								return;
							}
						}

						refillBlockInSlot(e);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onConsuming(PlayerItemConsumeEvent e) {

		Player player = e.getPlayer();

		if (isPlayerAllowedToRefillConsumables(player)) {

			boolean config = PluginConfigManager.isDefaultConsumablesRefill();

			if (PlayerDataManager.containsRefillConumables(player)) {
				config = PlayerDataManager.isRefillConumables(player);
			}

			if (config) {
				ItemStack item = e.getItem();
				if (item.getAmount() == 1) {

					if (!isOnBlackList(item)) {
						if (item.getMaxStackSize() > 1) {

							boolean mainhand = false;
							boolean offhand = false;

							if (isPlayerHoldingAItemInMainHand(player)) {

								if (playerMainHandHeldItemMaterialEquals(item, player)) {
									mainhand = true;

								} else if (isPlayerHoldingAItemInOffHand(player)) {

									if (playerOffHandHeldItemMaterialEquals(item, player)) {
										offhand = true;
									}

								}

							} else if (isPlayerHoldingAItemInOffHand(player)) {

								if (playerOffHandHeldItemMaterialEquals(item, player)) {
									offhand = true;
								}

							}
							if (mainhand || offhand) {
								int hand = e.getPlayer().getInventory().getHeldItemSlot();
								if (!mainhand)
									hand = -999;
								refillConsumableInSlot(hand, player, e.getItem());

							}
						}
					}
				}
			}
		}

	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onPlayerItemBreaks(PlayerItemBreakEvent e) {

		Player player = e.getPlayer();

		if (isPlayerAllowedToRefillBrokenItems(player)) {

			boolean config = PluginConfigManager.isDefaultBreakableRefill();

			if (PlayerDataManager.containsRefillBreakables(player)) {
				config = PlayerDataManager.isRefillBreakables(player);
			}

			if (config) {
				ItemStack item = e.getBrokenItem();
				if (!isOnBlackList(item)) {
					int refillSlot = getRefillStack(item.getType(), player);

					if (refillSlot > 8) {
						if (player.getInventory().getItemInMainHand().equals(item)) {

							ItemStack refillItem = player.getInventory().getItem(refillSlot);

							ChestCleaner.main.getServer().getScheduler().scheduleSyncDelayedTask(ChestCleaner.main,
									new Runnable() {
										@Override
										public void run() {
											player.getInventory().setItem(player.getInventory().getHeldItemSlot(),
													refillItem);
											player.getInventory().setItem(refillSlot, null);
										}
									}, 1l);

						} else {

							ItemStack refillItem = player.getInventory().getItem(refillSlot);
							player.getInventory().setItem(40, refillItem);
							player.getInventory().setItem(refillSlot, null);

						}
					}
				}
			}
		}

	}

	private boolean isOnBlackList(ItemStack item) {
		return PluginConfigManager.getBlacklistAutoRefill().contains(item.getType());
	}

	/**
	 * Returns the index of a refill stack if it gets found, if not it returns -1.
	 * 
	 * @param material the material of the stack you want to find.
	 * @param p        the owner of the inventory.
	 * @return the index of the item in the inventory if it got found otherwise -1.
	 */
	private int getRefillStack(Material material, Player player) {

		ItemStack[] items = InventoryDetector.getFullInventory(player.getInventory());
		for (int i = 9; i < 36; i++) {

			if (items[i] != null) {

				if (items[i].getType().equals(material)) {
					return i;
				}

			}

		}
		return -1;

	}

	/**
	 * If the player is in survival mode and has the permission
	 * PluginPermissions.AUTOFILL_BROKEN_ITEMS it returns true otherwise false.
	 * 
	 * @param player the player you want to check.
	 * @return true if the player is allowed to auto refill broken items otherwise
	 *         false.
	 */
	private boolean isPlayerAllowedToRefillBrokenItems(Player player) {
		return player.getGameMode().equals(GameMode.SURVIVAL)
				&& player.hasPermission(PluginPermissions.AUTOFILL_BROKEN_ITEMS.getString());
	}

	/**
	 * If the player is in survival mode and has the permission
	 * PluginPermissions.AUTOFILL_CONSUMABLES it returns true otherwise false.
	 * 
	 * @param player the player you want to check.
	 * @return true if the player is allowed to auto refill otherwise false.
	 */
	private boolean isPlayerAllowedToRefillConsumables(Player player) {
		return player.getGameMode().equals(GameMode.SURVIVAL)
				&& player.hasPermission(PluginPermissions.AUTOFILL_CONSUMABLES.getString());
	}

	/**
	 * If the player is in survival mode and has the permission
	 * PluginPermissions.AUTOFILL_CONSUMABLES it returns true otherwise false.
	 * 
	 * @param player the player you want to check.
	 * @return true if the player is allowed to auto refill otherwise false.
	 */
	private boolean isPlayerAllowedToRefillBlocks(Player player) {
		return player.getGameMode().equals(GameMode.SURVIVAL)
				&& player.hasPermission(PluginPermissions.AUTOFILL_BLOCKS.getString());
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
		return !player.getInventory().getItemInOffHand().getType().isAir();
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
	 * with the same type, an amount bigger than 1 (bigger than 0 would work but
	 * causes a rendering-bug in the client, so the item is invisible) and puts it
	 * into the slot (+ one amount) while consuming.
	 * 
	 * @param inMainHand true if the refill slot is the main hand otherwise false.
	 * @param e          the consuming event the refill should happen.
	 */
	private void refillConsumableInSlot(int hand, Player player, ItemStack conItem) {

		ChestCleaner.main.getServer().getScheduler().scheduleSyncDelayedTask(ChestCleaner.main, new Runnable() {
			@Override
			public void run() {

				ItemStack[] items = InventoryDetector.getFullInventory(player.getInventory());
				for (int i = 9; i < 36; i++) {
					if (items[i] != null) {
						if (items[i].getType().equals(conItem.getType())) {
							if (hand > -999) {
								player.getInventory().setItem(hand, items[i]);
								player.getInventory().setItem(i, null);
								break;
							} else {
								player.getInventory().setItemInOffHand(items[i]);
								player.getInventory().setItem(i, null);
								break;
							}

						}

					}

				}

			}
		}, 1l);

	}

	/**
	 * Searches through the main inventory (slots 0 - 35) taking the first ItemStack
	 * with the same type of materials the placed block has and puts it into the
	 * slot after placing.
	 * 
	 * @param e the block placing event the refill should happen.
	 */
	private void refillBlockInSlot(BlockPlaceEvent e) {
		ItemStack[] items = InventoryDetector.getFullInventory(e.getPlayer().getInventory());
		int currentItemSlot = e.getPlayer().getInventory().getHeldItemSlot();

		for (int i = 0; i < 36; i++) {
			if (i == currentItemSlot) continue;
			if (items[i] != null) {
				if (items[i].isSimilar(e.getItemInHand())) {
					if (e.getHand().equals(EquipmentSlot.HAND)) {
						e.getPlayer().getInventory().setItemInMainHand(items[i]);
						e.getPlayer().getInventory().setItem(i, null);
						break;
					} else if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
						e.getPlayer().getInventory().setItemInOffHand(items[i]);
						e.getPlayer().getInventory().setItem(i, null);
						break;
					}

				}

			}

		}
	}
}
