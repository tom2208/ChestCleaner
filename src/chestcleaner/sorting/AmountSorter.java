package chestcleaner.sorting;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AmountSorter {

	private ArrayList<ItemStack> items;

	/**
	 * You can use it to sort ArrayLists of ItemStacks by amount in sub-arrays of
	 * the same material.
	 * 
	 * @param list the list you want to get sorted.
	 * @throws IllegalArgumentException if {@code list} is null;
	 */
	public AmountSorter(ArrayList<ItemStack> list) {
		if (list == null)
			throw new IllegalArgumentException();
		items = list;
	}

	/**
	 * Sorts the array and returns the result;
	 * 
	 * @return Returns the sorted array.
	 */
	public ArrayList<ItemStack> sortArray() {

		if (items.size() < 2)
			return items;

		int beginn = 0;
		Material material = items.get(0).getType();

		for (int i = 1; i < items.size(); i++) {

			if (!items.get(i).getType().equals(material)) {

				if (i - beginn > 1)
					sortSubArray(beginn, i - 1);
				beginn = i;
				material = items.get(i).getType();

			}

		}

		if (beginn != items.size() - 1) {
			sortSubArray(beginn, items.size() - 1);
		}

		return items;
	}

	private void sortSubArray(int from, int to) {

		for (int i = from; i <= to - 1; i++) {
			for (int j = i + 1; j <= to; j++) {
				if (items.get(j).getAmount() > items.get(i).getAmount()) {
					ItemStack k = items.get(j);
					items.set(j, items.get(i));
					items.set(i, k);
				}
			}
		}

	}

}
