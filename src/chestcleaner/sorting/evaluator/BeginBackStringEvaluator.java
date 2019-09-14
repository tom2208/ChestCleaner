package chestcleaner.sorting.evaluator;

import org.bukkit.inventory.ItemStack;

public class BeginBackStringEvaluator implements Evaluator{

	@Override
	public boolean isGreaterThan(ItemStack item1, ItemStack item2) {
		if (item1.getType().equals(item2.getType()))
			return false;

		String name1 = item1.getType().name();
		String name2 = item2.getType().name();

		for (int i = 0; i < Math.min(name1.length(), name2.length()); i++) {

			if (name1.charAt(i) > name2.charAt(i)) {
				return true;
			} else if (name1.charAt(i) != name2.charAt(i)) {
				return false;
			}

		}
		
		if(name1.length() > name2.length()) return true;
		return false;
	}

	@Override
	public boolean isSmallerThan(ItemStack item1, ItemStack item2) {

		if (item1.getType().equals(item2.getType()))
			return false;

		String name1 = item1.getType().name();
		String name2 = item2.getType().name();

		for (int i = 0; i < Math.min(name1.length(), name2.length()); i++) {

			if (name1.charAt(i) < name2.charAt(i)) {
				return true;
			} else if (name1.charAt(i) != name2.charAt(i)) {
				return false;
			}

		}
		
		if(name1.length() < name2.length()) return true;
		return false;

	}

}
