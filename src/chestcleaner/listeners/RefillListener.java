package chestcleaner.listeners;

import java.util.HashMap;
import java.util.Objects;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockEvent;
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

	private HashMap<Material, Material> specialBlockRefills;

	public RefillListener() {
		specialBlockRefills = new HashMap<>();
		try {
			specialBlockRefills.put(Material.WHEAT, Material.WHEAT_SEEDS);
			specialBlockRefills.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
			specialBlockRefills.put(Material.MELON_STEM, Material.MELON_SEEDS);
			specialBlockRefills.put(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
			specialBlockRefills.put(Material.TRIPWIRE, Material.STRING);
			specialBlockRefills.put(Material.COCOA, Material.COCOA_BEANS);
			specialBlockRefills.put(Material.POTATOES, Material.POTATO);
			specialBlockRefills.put(Material.CARROTS, Material.CARROT);
			specialBlockRefills.put(Material.REDSTONE_WIRE, Material.REDSTONE);
			specialBlockRefills.put(Material.REDSTONE_WALL_TORCH, Material.REDSTONE_TORCH);
			specialBlockRefills.put(Material.WALL_TORCH, Material.TORCH);
			specialBlockRefills.put(Material.OAK_WALL_SIGN, Material.OAK_SIGN);
			specialBlockRefills.put(Material.SPRUCE_WALL_SIGN, Material.SPRUCE_SIGN);
			specialBlockRefills.put(Material.BIRCH_WALL_SIGN, Material.BIRCH_SIGN);
			specialBlockRefills.put(Material.JUNGLE_WALL_SIGN, Material.JUNGLE_SIGN);
			specialBlockRefills.put(Material.ACACIA_WALL_SIGN, Material.ACACIA_SIGN);
			specialBlockRefills.put(Material.DARK_OAK_WALL_SIGN, Material.DARK_OAK_SIGN);
			specialBlockRefills.put(Material.WARPED_WALL_SIGN, Material.WARPED_SIGN);
			specialBlockRefills.put(Material.BAMBOO_SAPLING, Material.BAMBOO);
			specialBlockRefills.put(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES);
			specialBlockRefills.put(Material.SOUL_WALL_TORCH, Material.SOUL_TORCH);
			specialBlockRefills.put(Material.CRIMSON_WALL_SIGN, Material.CRIMSON_SIGN);
		}catch (NoSuchFieldError ignore){
		}

	}

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

				Material material = e.getBlockPlaced().getType();

				if (specialBlockRefills.containsKey(material)) {
					material = specialBlockRefills.get(material);
				}

				if (item.getAmount() == 1) {
					if (e.getPlayer().getInventory().getItem(e.getHand()).getType().equals(material)) {
						if (isNotOnBlackList(item)) {
							refillBlockInSlot(e.getPlayer(), material, e.getHand());
						}
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

					if (isNotOnBlackList(item)) {
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
				if (isNotOnBlackList(item)) {
					int refillSlot = getRefillStack(item.getType(), player);
					if (refillSlot >= 0) {
						ItemStack refillItem = player.getInventory().getItem(refillSlot);

						if (player.getInventory().getItemInMainHand().equals(item)) {

							ChestCleaner.main.getServer().getScheduler().scheduleSyncDelayedTask(ChestCleaner.main,
									() -> {
										player.getInventory().setItem(player.getInventory().getHeldItemSlot(),
												refillItem);
										player.getInventory().setItem(refillSlot, null);
									}, 1L);

						} else if (player.getInventory().getItemInOffHand().equals(item)) {
							ChestCleaner.main.getServer().getScheduler().scheduleSyncDelayedTask(ChestCleaner.main,
									() -> {
										player.getInventory().setItem(40, refillItem);
										player.getInventory().setItem(refillSlot, null);
									}, 1L);
						}
					}
				}
			}
		}

	}

	private boolean isNotOnBlackList(ItemStack item) {
		return !PluginConfigManager.getBlacklistAutoRefill().contains(item.getType());
	}

	/**
	 * Returns the index of a refill stack if it gets found, if not it returns -1.
	 * 
	 * @param material the material of the stack you want to find.
	 * @param player        the owner of the inventory.
	 * @return the index of the item in the inventory if it got found otherwise -1.
	 */
	private int getRefillStack(Material material, Player player) {

		ItemStack[] items = InventoryDetector.getFullInventory(player.getInventory());
		for (int i = 0; i < 36; i++) {
			if (i != player.getInventory().getHeldItemSlot()) {
				if (items[i] != null) {

					if (items[i].getType().equals(material)) {
						return i;
					}

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
		return Objects.requireNonNull(player.getInventory().getItem(player.getInventory().getHeldItemSlot())).getType().equals(item.getType());
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

	private boolean isViableSlot(int i, Player player) {
		return i != player.getInventory().getHeldItemSlot() && i != 40;
	}

	/**
	 * Searches through the main inventory (slots 9 - 35) taking the first ItemStack
	 * with the same type, an amount bigger than 1 (bigger than 0 would work but
	 * causes a rendering-bug in the client, so the item is invisible) and puts it
	 * into the slot (+ one amount) while consuming.
	 */
	private void refillConsumableInSlot(int hand, Player player, ItemStack conItem) {

		ChestCleaner.main.getServer().getScheduler().scheduleSyncDelayedTask(ChestCleaner.main, () -> {

			ItemStack[] items = InventoryDetector.getFullInventory(player.getInventory());
			for (int i = 0; i < 36; i++) {
				if (items[i] != null) {
					if (isViableSlot(i, player)) {
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

		}, 1L);

	}

	/**
	 * Searches through the main inventory (slots 9 - 35) taking the first ItemStack
	 * with the same type of materials the placed block has and puts it into the
	 * slot after placing.
	 */
	private void refillBlockInSlot(Player player, Material material, EquipmentSlot hand) {
		ItemStack[] items = InventoryDetector.getFullInventory(player.getInventory());

		for (int i = 0; i < 36; i++) {

			if (items[i] != null) {
				if (isViableSlot(i, player)) {
					if (items[i].getType().equals(material)) {

						if (hand.equals(EquipmentSlot.HAND)) {
							player.getInventory().setItemInMainHand(items[i]);
							player.getInventory().setItem(i, null);
							break;
						} else if (hand.equals(EquipmentSlot.OFF_HAND)) {
							player.getInventory().setItemInOffHand(items[i]);
							player.getInventory().setItem(i, null);
							break;
						}

					}
				}
			}

		}
	}
}
