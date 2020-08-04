package chestcleaner.utils;

public enum PluginPermissions {

    AUTOFILL_CONSUMABLES("chestcleaner.autorefill.consumables"),
    AUTOFILL_BLOCKS("chestcleaner.autorefill.blocks"),
    CLEANING_ITEM_USE("chestcleaner.cleaningitem.use"),
    CLEANING_ITEM_USE_OWN_INV("chestcleaner.cleaningitem.use.owninventory"),
    COOLDOWN_IMMUNE("chestcleaner.cooldown.immune"),
    UPDATE_PLUGIN("chestcleaner.update"),

    CMD_SORTING_CONFIG_CATEGORIES("chestcleaner.cmd.sortingconfig.categories"),
    CMD_SORTING_CONFIG_PATTERN("chestcleaner.cmd.sortingconfig.pattern"),
    CMD_SORTING_CONFIG_SET_AUTOSORT("chestcleaner.cmd.sortingconfig.setautosort"),
    CMD_SORTING_CONFIG_SET_NOTIFICATION_BOOL("chestcleaner.cmd.sortingconfig.setchatnotification"),
    CMD_SORTING_CONFIG_SET_SOUND_BOOL("chestcleaner.cmd.sortingconfig.setsortingsound"),
    CMD_CLEANING_ITEM_GET("chestcleaner.cmd.cleaningitem.get"),
    CMD_CLEANING_ITEM_GIVE("chestcleaner.cmd.cleaningitem.give"),
    CMD_INV_CLEAN("chestcleaner.cmd.cleaninventory"),

    CMD_ADMIN_CONFIG("chestcleaner.cmd.admin.config"),
    CMD_ADMIN_COOLDOWN("chestcleaner.cmd.admin.cooldown"),
    CMD_ADMIN_BLACKLIST("chestcleaner.cmd.admin.blacklist"),

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
