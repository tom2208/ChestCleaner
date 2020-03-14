package chestcleaner.utils;

public enum Permissions {

	CMD_BLACKLIST("chestcleaner.cmd.blacklist"), CMD_CLEANING_ITEM_RENAME("chestcleaner.cmd.cleaningitem.rename"),
	CMD_CLEANING_ITEM_SET_LORE("chestcleaner.cmd.cleaningitem.setlore"),
	CMD_CLEANING_ITEM_SET_ITEM("chestcleaner.cmd.cleaningitem.setitem"),
	CMD_CLEANING_ITEM_GET("chestcleaner.cmd.cleaningitem.get"),
	CMD_CLEANING_ITEM_SET_ACTIVE("chestcleaner.cmd.cleaningitem.setactive"),
	CMD_CLEANING_ITEM_SET_DURABILITYLOSS("chestcleaner.cmd.cleaningitem.setdurabilityloss"),
	CMD_CLEANING_ITEM_GIVE("chestcleaner.cmd.cleaningitem.give"),
	CMD_CLEANING_ITEM_SET_EVENT_MODE("chestcleaner.cmd.cleaningitem.seteventdetectionmode"),
	CMD_INV_CLEAN("chestcleaner.cmd.cleaninventory"),
	CMD_SORTING_CONFIG_PATTERN("chestcleaner.cmd.sortingconfig.pattern"),
	CMD_SORTING_CONFIG_EVALUATOR("chestcleaner.cmd.sortingconfig.evaluator"),
	CMD_SORTING_CONFIG_SET_AUTOSORT("chestcleaner.cmd.sortingconfig.setautosort"),
	CMD_SORTING_CONFIG_ADMIN_CONTROL("chestcleaner.cmd.sorting.config.admincontrol"),
	CMD_TIMER("chestcleaner.cmd.timer"), REFILL_BLOCKS("chestcleaner.autorefill.blocks"),
	AUTOFILL_CONSUMABLES("chestcleaner.autorefill.consumables"),
	CLEANING_ITEM_USE_OWN_INV("chestcleaner.cleaningitem.use.owninventory"),
	CLEANING_ITEM_USE("chestcleaner.cleaningitem.use");

	private String permission;

	Permissions(String permission) {
		this.permission = permission;
	}

	public String getString() {
		return permission;
	}

}
