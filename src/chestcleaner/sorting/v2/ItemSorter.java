package chestcleaner.sorting.v2;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ItemSorter {
	
	private CategorizerManager manager;
	
	public ItemSorter(CategorizerManager manager) {
		this.manager = manager;
	}
	
	public ItemSorter() {
		manager = new CategorizerManager();
	}
	
	public CategorizerManager getManager() {
		return manager;
	}
	
	public List<ItemStack> sort(List<ItemStack> items, String[] categoryNames){
		// TODO sorting / categorization
		return null;
	}
		
}
