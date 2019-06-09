package chestcleaner.sorting;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class Quicksort {

	private ArrayList<ItemStack> items;

	public Quicksort(ArrayList<ItemStack> items) {
		this.items = items;
	}

	public ArrayList<ItemStack> sort(int l, int r) {
		int q;
		if (l < r) {
			q = partition(l, r);
			sort(l, q);
			sort(q + 1, r);
		}
		return items;
	}

	private int partition(int l, int r) {

		int i = l - 1;
		int j = r + 1;
		ItemStack item = items.get((l+r)/2);
		while (true) {
			do {
				i++;
			} while (Evaluator.isSmallerThan(items.get(i), item));

			do {
				j--;
			} while (Evaluator.isGreaterThan(items.get(j), item));

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