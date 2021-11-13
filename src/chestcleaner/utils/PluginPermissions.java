package chestcleaner.utils;

public enum PluginPermissions {

    AUTOFILL_CONSUMABLES("chestcleaner.autorefill.consumables"),
    AUTOFILL_BLOCKS("chestcleaner.autorefill.blocks"),
    AUTOFILL_BROKEN_ITEMS("chestcleaner.autorefill.brokenitems"),
    CLEANING_ITEM_USE("chestcleaner.cleaningitem.use"),
    CLEANING_ITEM_USE_OWN_INV("chestcleaner.cleaningitem.use.owninventory"),
    COOLDOWN_IMMUNE("chestcleaner.cooldown.immune"),
    UPDATE_PLUGIN("chestcleaner.update"),
    CLICK_SORT("chestcleaner.clicksort"),
    
    CMD_SORTING_CONFIG_CATEGORIES("chestcleaner.cmd.sortingconfig.categories"),
    CMD_SORTING_CONFIG_CATEGORIES_RESET("chestcleaner.cmd.sortingconfig.categories.reset"),
    CMD_SORTING_CONFIG_PATTERN("chestcleaner.cmd.sortingconfig.pattern"),
    CMD_SORTING_CONFIG_SET_AUTOSORT("chestcleaner.cmd.sortingconfig.setautosort"),
    CMD_SORTING_CONFIG_SET_NOTIFICATION_BOOL("chestcleaner.cmd.sortingconfig.setchatnotification"),
    CMD_SORTING_CONFIG_SET_SOUND_BOOL("chestcleaner.cmd.sortingconfig.setsortingsound"),
    CMD_SORTING_CONFIG_RESET("chestcleaner.cmd.sortingconfig.reset"),
    CMD_SORTING_CONFIG_REFILL_CONSUMABLES("chestcleaner.cmd.sortingconfig.refill.consumables"),
    CMD_SORTING_CONFIG_REFILL_BLOCKS("chestcleaner.cmd.sortingconfig.refill.blocks"),
    CMD_SORTING_CONFIG_REFILL_BREAKABLES("chestcleaner.cmd.sortingconfig.refill.breakables"),
    CMD_SORTING_CONFIG_REFILL_GENERIC("chestcleaner.cmd.sortingconfig.refill.*"),
    CMD_SORTING_CONFIG_CLICKSORT("chestcleaner.cmd.sortingconfig.clicksort"),
    CMD_CLEANING_ITEM_GET("chestcleaner.cmd.cleaningitem.get"),
    CMD_CLEANING_ITEM_GIVE("chestcleaner.cmd.cleaningitem.give"),
    CMD_INV_CLEAN("chestcleaner.cmd.cleaninventory.sort"),
    CMD_INV_CLEAN_OWN("chestcleaner.cmd.cleaninventory.own"),
    CMD_INV_CLEAN_OTHERS("chestcleaner.cmd.cleaninventory.others"),
    
    CMD_ADMIN_COOLDOWN("chestcleaner.cmd.admin.cooldown"),
    CMD_ADMIN_ITEM_SET("chestcleaner.cmd.admin.cleaningitem.setitem"),
    CMD_ADMIN_ITEM_RENAME("chestcleaner.cmd.admin.cleaningitem.rename"),
    CMD_ADMIN_ITEM_SET_LORE("chestcleaner.cmd.admin.cleaningitem.setlore"),
    CMD_ADMIN_ITEM_SET_ACTIVE("chestcleaner.cmd.admin.cleaningitem.setactive"),
    CMD_ADMIN_ITEM_SET_DURABILITYLOSS("chestcleaner.cmd.admin.cleaningitem.setdurabilityloss"),
    CMD_ADMIN_ITEM_SET_EVENT_MODE("chestcleaner.cmd.admin.cleaningitem.setopenevent");

    private String permission;

    PluginPermissions(String permission) {
        this.permission = permission;
    }

    public String getString() {
        return permission;
    }

}
