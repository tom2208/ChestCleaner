package chestcleaner.utils.messages.enums;

public enum MessageID {

	COMMON_ERROR("common.error"),
	COMMON_ERROR_SYNTAX("common.error.syntax"),
	COMMON_PAGE("common.page"),
	COMMON_PAGE_NEXT("common.page.next"),
	COMMON_PREFIX("common.prefix"),

	ERROR_BLACKLIST_EMPTY("error.blacklist.empty"),
	ERROR_BLACKLIST_EXISTS("error.blacklist.exists"),
	ERROR_BLACKLIST_INVENTORY("error.blacklist.inventory"),
	ERROR_BLACKLIST_NOT_EXISTS("error.blacklist.not.exists"),
	ERROR_BLACKLIST_LIST_NOT_EXIST("error.blacklist.list.not.exist"),
	ERROR_BLOCK_NO_INVENTORY("error.block.no.inventory"),
	ERROR_CATEGORY_BOOK("error.category.book"),
	ERROR_CATEGORY_NAME("error.category.name"),
	ERROR_CATEGORY_NOT_IN_CONFIG("error.category.notinconfig"),
	ERROR_CATEGORY_INVALID("error.category.invalid"),
	ERROR_MATERIAL_NAME("error.material.name"),
	ERROR_PAGE_NUMBER("error.page.number"),
	ERROR_PATTERN_ID("error.pattern.id"),
	ERROR_PERMISSION("error.permission"),
	ERROR_PLAYER_NOT_ONLINE("error.player.not.online"),
	ERROR_SOUND_NOT_FOUND("error.sound.notfound"),
	ERROR_VALIDATION_BOOLEAN("error.validation.bool"),
	ERROR_VALIDATION_INDEX_BOUNDS("error.validation.index.bounds"),
	ERROR_VALIDATION_INTEGER("error.validation.integer"),
	ERROR_WORLD_NAME("error.world.name"),
	ERROR_YOU_COOLDOWN_SORTING("error.you.cooldown.sorting"),
	ERROR_YOU_COOLDOWN_GENERIC("error.you.cooldown.generic"),
	ERROR_YOU_HOLD_ITEM("error.you.hold.item"),
	ERROR_YOU_NOT_PLAYER("error.you.not.player"),

	INFO_BLACKLIST_ADD("info.blacklist.add"),
	INFO_BLACKLIST_CLEARED("info.blacklist.cleared"),
	INFO_BLACKLIST_DEL("info.blacklist.del"),
	INFO_CATEGORY_NEW("info.category.new"),
	INFO_CATEGORY_REMOVED("info.category.removed"),
	INFO_CATEGORY_RESETED("info.category.reseted"),
	INFO_CLEANITEM_PLAYER_GET("info.cleaningitem.player.get"),
	INFO_CLEANITEM_YOU_GET("info.cleaningitem.you.get"),
	INFO_CURRENT_VALUE("info.value.current"),
	INFO_INVENTORY_SORTED("info.inventory.sorted"),
	INFO_SORTING_SOUND_DEFAULT_SET("info.sorting.sound.default.set"),
	INFO_UPDATE_AVAILABLE("info.update.available"),
	INFO_VALUE_CHANGED("info.value.changed"),
	INFO_RESET_CONFIG("info.config.reset");

	String id;

	MessageID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

}
