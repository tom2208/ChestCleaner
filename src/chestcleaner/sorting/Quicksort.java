package chestcleaner.sorting;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import chestcleaner.sorting.evaluator.Evaluator;
import chestcleaner.sorting.evaluator.EvaluatorType;

public class Quicksort {

	private ArrayList<ItemStack> items;
	// Default Evaluator
	private Evaluator evaluator = EvaluatorType.getEvaluator(EvaluatorType.DEFAULT);

	public Quicksort(ArrayList<ItemStack> items, Evaluator evaluator) {
		this.items = items;
		if (evaluator != null) {
			this.evaluator = evaluator;
		}
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