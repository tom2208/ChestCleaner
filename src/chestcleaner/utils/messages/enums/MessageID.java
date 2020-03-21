package chestcleaner.utils.messages.enums;

public enum MessageID {

	SORTING_ON_COOLDOWN("sortingOnCooldown"), BLOCK_HAS_NO_INVENTORY("blockHasNoInventory"),
	INVENTORY_SORTED("inventorySorted"), NO_WORLD_WITH_THIS_NAME("noWorldWithThisName"),
	COOLDOWN_TOGGLE("cooldownTogggled"), TIMER_TIME("cooldownTime"), SET_CLEANING_ITEM_NAME("cleaningItemNameSet"),
	SET_CLEANING_ITEM_LORE("cleaningItemLoreSet"), SET_CLEANING_ITEM("cleaningItemSet"),
	YOU_HAVE_TO_HOLD_AN_ITEM("youHaveToHoldAnItem"), YOU_GOT_CLEANING_ITEM("youGotACleaningItem"),
	CLEANING_ITEM_TOGGLED("cleaningItemToggled"), PLAYER_GOT_CLEANING_ITEM("playerGotCleaningItem"),
	PLAYER_NOT_ONLINE("playerIsNotOnline"), OPEN_INV_MODE_TOGGLED("openInvModeToggled"),
	DURABILITYLOSS_AVTIVATED("durabilityLossActivated"), DURABILITYLOSS_DEACTIVATED("durabilityLossDeactivated"),
	MATERIAL_ADDED_TO_BLACKLIST("materialAddedToBlacklist"),
	MATERIAL_REMOVED_FROM_BLACKLIST("materialRemovedFormBlacklist"),
	BLACKLIST_DOESNT_CONTAIN_MATERIAL("blacklistDoesNotContainMatieral"), INDEX_OUT_OF_BOUNDS("indexOutOfBounds"),
	BLACKLIST_EMPTY("blacklistEmpty"), BLACKLIST_PAGE("blacklistPage"), BLACKLIST_NEXT_PAGE("blacklistNextPage"),
	NOT_AN_INTEGER("notAnInteger"), INVALID_PAGE_NUMBER("invalidPageNumber"),
	MATERIAL_NAME_NOT_EXISTING("materialNameNotExisting"), BLACKLIST_CLEARED("blacklistCleared"),
	MATERIAL_ALREADY_ON_BLACKLIST("materialAlreadyOnBlacklist"), INVENTORY_ON_BLACKLIST("inventoryOnBlacklist"),
	INVALID_PATTERN_ID("invalidPatternID"), INAVLID_EVALUATOR_ID("invalidEvaluatorID"),
	DEFAULT_PATTER_SET("defaultPatternSet"), DEFAULT_EVALUATOR_SET("defaultEvaluatorSet"), PATTERN_SET("patternSet"),
	EVALUATOR_SET("evaluatorSet"), AUTOSORTING_TOGGLED("autoSortingToggled"),
	DEFAULT_AUTOSORTING_TOGGLED("defaultAutoSortingToggled"), NEW_UPDATE_AVAILABLE("newUpdateAvailable"),
	SYNTAX_ERROR("sytaxError"), ERROR("error"), NO_PERMISSION_FOR_COMMAND("noPermissionForCommand"),
	YOU_HAVE_TO_BE_PLAYER("youHaveToBePlayer"), PREFIX("prefix");

	String id;

	MessageID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

}
