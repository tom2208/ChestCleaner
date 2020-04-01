package chestcleaner.utils;

import java.util.List;

import chestcleaner.config.PluginConfigManager;
import chestcleaner.sorting.v2.CategorizerManager;
import chestcleaner.sorting.v2.ItemSorter;

public class SortingUtils {
	
	public static ItemSorter getSorter() {
		return PluginConfigManager.getInstance().getSorter();
	}
	
	public static CategorizerManager getManager() {
		return getSorter().getManager();
	}
	
	public static List<String> getCategoryNames(){
		return getManager().getNameList();
	}
}
