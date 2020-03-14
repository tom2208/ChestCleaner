package chestcleaner.sorting.evaluator;

import org.bukkit.inventory.ItemStack;

public class BackBeginStringEvaluator implements Evaluator {

	/**
	 * <b>Checks if the characters of {@code item1} are greater than the characters
	 * of {@code item2}</b>, beginning at the last character of both items,
	 * iterating to the beginning of the item with shorter name. Breaks if the
	 * characters with the same indices are not equal. </br>
	 * </br>
	 * For example if you have the names "diamond" and "gold", then it compares the
	 * last characters:</br>
	 * 'd' == 'd' - they are equal so its the next characters turn:</br>
	 * 'n' > 'l'- n is greater than l, so the method returns true.
	 * 
	 * @param item1 the item that gets checked whether its name is greater than the
	 *              name of {@code item2}, using the algorithm described above.
	 * @param item2 the item that gets checked whether its name is smaller or equal
	 *              to the name of {@code item2}, using the algorithm described
	 *              above.
	 * @return Returns {@code true} if {@code item1} is greater then {@code item2}.
	 */
	public boolean isGreaterThan(ItemStack item1, ItemStack item2) {

		if (item1.getType().equals(item2.getType()))
			return false;

		String name1 = item1.getType().name();
		String name2 = item2.getType().name();

		for (int i = 0; i < Math.min(name1.length(), name2.length()); i++) {

			if (name1.charAt(name1.length() - i - 1) > name2.charAt(name2.length() - i - 1)) {
				return true;
			} else if (name1.charAt(name1.length() - i - 1) != name2.charAt(name2.length() - i - 1)) {
				return false;
			}

		}

		if (name1.length() > name2.length())
			return true;
		return false;

	}

	/**
	 * <b>Checks if the characters of {@code item1} are smaller than the characters
	 * of {@code item2}</b>, beginning at the last character of both items,
	 * iterating to the beginning of the item with shorter name. Breaks if the
	 * characters with the same indices are not equal. </br>
	 * </br>
	 * For example if you have the names "gold" and "diamond", then it compares the
	 * last characters:</br>
	 * 'd' == 'd' - they are equal so its the next characters turn:</br>
	 * 'l' < 'n'- n is greater than l, so the method returns true.
	 * 
	 * @param item1 the item that gets checked whether its name is smaller than the
	 *              name of {@code item2}, using the algorithm described above.
	 * @param item2 the item that gets checked whether its name is greater or equal
	 *              to the name of {@code item2}, using the algorithm described
	 *              above.
	 * @return Returns {@code true} if {@code item1} is smaller then {@code item2}.
	 */
	public boolean isSmallerThan(ItemStack item1, ItemStack item2) {

		if (item1.getType().equals(item2.getType()))
			return false;

		String name1 = item1.getType().name();
		String name2 = item2.getType().name();

		for (int i = 0; i < Math.min(name1.length(), name2.length()); i++) {

			if (name1.charAt(name1.length() - i - 1) < name2.charAt(name2.length() - i - 1)) {
				return true;
			} else if (name1.charAt(name1.length() - i - 1) != name2.charAt(name2.length() - i - 1)) {
				return false;
			}

		}

		if (name1.length() < name2.length())
			return true;
		return false;

	}

}
