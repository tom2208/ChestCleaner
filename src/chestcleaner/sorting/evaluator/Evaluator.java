package chestcleaner.sorting.evaluator;

import org.bukkit.inventory.ItemStack;

public interface Evaluator {

	public boolean isSmallerThan(ItemStack item1, ItemStack item2);

}
