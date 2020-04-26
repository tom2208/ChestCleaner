package chestcleaner.utils;

import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class Quicksort {

	public static List<ItemStack> sort(List<ItemStack> items, Comparator<ItemStack> comparator, int l, int r) {
		int q;
		if (l < r) {
			q = partition(items, comparator, l, r);
			sort(items, comparator, l, q);
			sort(items, comparator, q + 1, r);
		}
		return items;
	}

	private static int partition(List<ItemStack> items, Comparator<ItemStack> comparator, int l, int r) {

		int i = l - 1;
		int j = r + 1;
		ItemStack item = items.get((l + r) / 2);
		while (true) {
			do {
				i++;
			} while (comparator.compare(items.get(i), item) < 0);

			do {
				j--;
			} while (comparator.compare(item, items.get(j)) < 0);

			if (i < j) {
				ItemStack k = items.get(i);
				items.set(i, items.get(j));
				items.set(j, k);
			} else {
				return j;
			}
		}
	}

}