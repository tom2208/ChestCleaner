package chestcleaner.sorting.v2;

import chestcleaner.sorting.evaluator.Evaluator;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuicksortV2 {

	public static List<ItemStack> sort(List<ItemStack> items, Evaluator evaluator, int l, int r) {
		int q;
		if (l < r) {
			q = partition(items, evaluator, l, r);
			sort(items, evaluator, l, q);
			sort(items, evaluator, q + 1, r);
		}
		return items;
	}

	private static int partition(List<ItemStack> items, Evaluator evaluator, int l, int r) {

		int i = l - 1;
		int j = r + 1;
		ItemStack item = items.get((l + r) / 2);
		while (true) {
			do {
				i++;
			} while (evaluator.isSmallerThan(items.get(i), item));

			do {
				j--;
			} while (evaluator.isSmallerThan(item, items.get(j)));

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