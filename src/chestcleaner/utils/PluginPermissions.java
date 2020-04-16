package chestcleaner.utils;

public enum PluginPermissions {

    AUTOFILL_CONSUMABLES("chestcleaner.autorefill.consumables"),
    CLEANING_ITEM_USE("chestcleaner.cleaningitem.use"),
    COOLDOWN_IMMUNE("chestcleaner.cooldown.immune"),
    CLEANING_ITEM_USE_OWN_INV("chestcleaner.cleaningitem.use.owninventory"),
    CMD_BLACKLIST("chestcleaner.cmd.blacklist"),
    CMD_CLEANING_ITEM_RENAME("chestcleaner.cmd.cleaningitem.rename"),
    CMD_CLEANING_ITEM_GET("chestcleaner.cmd.cleaningitem.get"),
    CMD_CLEANING_ITEM_GIVE("chestcleaner.cmd.cleaningitem.give"),
    CMD_CLEANING_ITEM_SET_ACTIVE("chestcleaner.cmd.cleaningitem.setactive"),
    CMD_CLEANING_ITEM_SET_DURABILITYLOSS("chestcleaner.cmd.cleaningitem.setdurabilityloss"),
    CMD_CLEANING_ITEM_SET_EVENT_MODE("chestcleaner.cmd.cleaningitem.seteventdetectionmode"),
    CMD_CLEANING_ITEM_SET_ITEM("chestcleaner.cmd.cleaningitem.setitem"),
    CMD_CLEANING_ITEM_SET_LORE("chestcleaner.cmd.cleaningitem.setlore"),
    CMD_COOLDOWN("chestcleaner.cmd.cooldown"),
    REFILL_BLOCKS("chestcleaner.autorefill.blocks"),
    CMD_INV_CLEAN("chestcleaner.cmd.cleaninventory"),
    CMD_SORTING_CONFIG_ADMIN_CONTROL("chestcleaner.cmd.sorting.config.admincontrol"),
    CMD_SORTING_CONFIG_EVALUATOR("chestcleaner.cmd.sortingconfig.evaluator"),
    CMD_SORTING_CONFIG_PATTERN("chestcleaner.cmd.sortingconfig.pattern"),
    CMD_SORTING_CONFIG_SET_AUTOSORT("chestcleaner.cmd.sortingconfig.setautosort"),
    ;

    private String permission;

    PluginPermissions(String permission) {
        this.permission = permission;
    }

    public String getString() {
        return permission;
    }

}
