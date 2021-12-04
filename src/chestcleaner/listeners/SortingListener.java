package chestcleaner.listeners;

import chestcleaner.cooldown.CMRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import chestcleaner.config.PlayerDataManager;
import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.BlockDetector;
import chestcleaner.utils.InventoryDetector;
import chestcleaner.utils.PluginPermissions;
import chestcleaner.utils.SortingUtils;
import chestcleaner.utils.messages.MessageSystem;

/**
 * @author Tom2208
 */
public class SortingListener implements org.bukkit.event.Listener {

    private String[] usableAnimalInventories = {"CraftMule", "CraftLlama", "CraftHorse"};
    private InventoryType[] invTypeWhiteList = {InventoryType.CHEST, InventoryType.ENDER_CHEST, InventoryType.BARREL, InventoryType.SHULKER_BOX, InventoryType.PLAYER, InventoryType.CREATIVE,
            InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.HOPPER};

    @EventHandler
    private void onRightClick(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (PluginConfigManager.isCleaningItemActive() && isPlayerHoldingACleaningItem(player)) {

                if (isPlayerAllowedToCleanOwnInv(player) && SortingUtils.sortPlayerInvWithEffects(player)) {
                    damageItem(player);
                    e.setCancelled(true);

                } else if (!PluginConfigManager.isOpenEvent()
                        && !CMRegistry.isOnCooldown(CMRegistry.CMIdentifier.SORTING, player)
                        && player.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())) {

                    Block b = BlockDetector.getTargetBlock(player);

                    if ((!InventoryDetector.hasInventoryHolder(b))
                            || PluginConfigManager.getBlacklistInventory().contains(b.getType()))
                    return;

                    if (InventorySorter.sortPlayerBlock(b, player)) {
                        damageItem(player);
                        InventorySorter.playSortingSound(player);
                        MessageSystem.sendSortedMessage(player);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onOpenInventory(InventoryOpenEvent e) {

        if (PluginConfigManager.isCleaningItemActive() && PluginConfigManager.isOpenEvent()) {

            Player player = (Player) e.getPlayer();

            if (player.hasPermission(PluginPermissions.CLEANING_ITEM_USE.getString())
                    && isPlayerHoldingACleaningItem(player) && SortingUtils.sortInventoryWithEffects(e.getInventory(), player)) {
                damageItem(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onCloseInventory(InventoryCloseEvent e) {
        // Doing the auto sorting here
        if (e.getInventory().getType().equals(InventoryType.ENDER_CHEST)
                || e.getInventory().getType().equals(InventoryType.CHEST)) {

            Player player = (Player) e.getPlayer();

            if (PlayerDataManager.isAutoSort(player)) {
                SortingUtils.sortInventoryWithEffects(e.getInventory(), player);
            }
        }
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if (e.getClick().equals(ClickType.MIDDLE) || isNewClick(e)) {
            if (e.getWhoClicked().hasPermission(PluginPermissions.CLICK_SORT.getString())) {
                if (e.getSlot() == -999) {
                    if (e.getCurrentItem() == null) {
                        Player player = Bukkit.getServer().getPlayer(e.getWhoClicked().getName());
                        if (PlayerDataManager.isClickSort(player)) {
                            sortInventoryOnClick(player, e);
                        }
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean isNewClick(InventoryClickEvent e){
        return e.getClick().equals(ClickType.RIGHT) && e.getCurrentItem() == null;
    }

    private void sortInventoryOnClick(Player player, InventoryClickEvent e) {

        boolean flag = false;
        for (InventoryType type : invTypeWhiteList) {
            if (e.getInventory().getType().equals(type)) {
                flag = true;
                break;
            }
        }

        boolean animalInv = false;

        if (e.getInventory().getType().equals(InventoryType.CHEST)) {
            for (String holder : usableAnimalInventories) {
                if (e.getInventory().getHolder() != null && e.getInventory().getHolder().toString().contains(holder)) {
                    animalInv = true;
                    break;
                }
            }
        }
        if (!flag || animalInv) {
            SortingUtils.sortPlayerInvWithEffects(player);
        } else {
            SortingUtils.sortInventoryWithEffects(e.getInventory(), player);
        }

    }

    private boolean isPlayerHoldingACleaningItem(Player player) {
        return isPlayerHoldingCleaningItemInMainHand(player) || isPlayerHoldingCleaningItemInOffHand(player);
    }

    private ItemStack getComparableItem(ItemStack item) {
        ItemStack compItem = item.clone();
        ItemMeta itemMeta = compItem.getItemMeta();
        Damageable damageable = ((Damageable) itemMeta);
        if (damageable != null) {
            damageable.setDamage(0);
            compItem.setItemMeta(itemMeta);
        }
        return compItem;
    }

    private boolean isPlayerHoldingCleaningItemInMainHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            return false;
        }
        return getComparableItem(item).isSimilar(PluginConfigManager.getCleaningItem());
    }

    private boolean isPlayerHoldingCleaningItemInOffHand(Player player) {
        ItemStack item = player.getInventory().getItemInOffHand();
        if (item.getType().equals(Material.AIR)) {
            return false;
        }
        return getComparableItem(item).isSimilar(PluginConfigManager.getCleaningItem());
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
            if (damageable != null) {
                if (!(damageable.getDamage() + 1 >= item.getType().getMaxDurability())) {
                    damageable.setDamage(damageable.getDamage() + 1);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
                item.setItemMeta(itemMeta);
            }
        }

    }

}
