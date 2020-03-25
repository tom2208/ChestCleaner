package chestcleaner.config;

import java.util.ArrayList;
import java.util.List;

public class PluginConfigManager {

	private static PluginConfigManager instance = null;

	private boolean durabilityLossActive = true;
	private boolean cleaningItemActive = true;
	private boolean eventModeActive = false;
	private boolean blockRefillActive = true;
	private boolean consumablesRefillActive = true;
	private boolean updateCheckerActive = true;
	private boolean cleanInvPermission = true;
	private final String falseString = "false";
	private final String trueString = "true";

	protected PluginConfigManager() {

	}

	public static PluginConfigManager getInstance() {
		if (instance == null) {
			instance = new PluginConfigManager();
		}

		return instance;
	}

	public boolean isDurabilityLossActive() {
		return durabilityLossActive;
	}

	public void setDurabilityLossActive(boolean durabilityLossActive) {
		this.durabilityLossActive = durabilityLossActive;
	}

	public boolean isCleaningItemActive() {
		return cleaningItemActive;
	}

	public void setCleaningItemActive(boolean cleaningItemActive) {
		this.cleaningItemActive = cleaningItemActive;
	}

	public boolean isEventModeActive() {
		return eventModeActive;
	}

	public void setEventModeActive(boolean eventModeActive) {
		this.eventModeActive = eventModeActive;
	}

	public boolean isBlockRefillActive() {
		return blockRefillActive;
	}

	public void setBlockRefillActive(boolean blockRefillActive) {
		this.blockRefillActive = blockRefillActive;
	}

	public boolean isConsumablesRefillActive() {
		return consumablesRefillActive;
	}

	public void setConsumablesRefillActive(boolean consumablesRefillActive) {
		this.consumablesRefillActive = consumablesRefillActive;
	}

	public boolean isUpdateCheckerActive() {
		return updateCheckerActive;
	}

	public void setUpdateCheckerActive(boolean updateCheckerActive) {
		this.updateCheckerActive = updateCheckerActive;
	}

	public boolean isCleanInvPermission() {
		return cleanInvPermission;
	}

	public void setCleanInvPermission(boolean cleanInvPermission) {
		this.cleanInvPermission = cleanInvPermission;
	}

	public String getFalseString() {
		return falseString;
	}

	public String getTrueString() {
		return trueString;
	}

	public static boolean isStringTrueOrFalse(String str) {
		return isStringTrue(str) || isStringFalse(str);
	}

	public static boolean isStringTrue(String str) {
		return str.equalsIgnoreCase(getInstance().getTrueString());
	}

	public static boolean isStringFalse(String str) {
		return str.equalsIgnoreCase(getInstance().getFalseString());
	}
	
	public static List<String> getBooleanValueStringList(){
		List<String> list = new ArrayList<String>();
		list.add(getInstance().getTrueString());
		list.add(getInstance().getFalseString());
		return list;
	}

}
