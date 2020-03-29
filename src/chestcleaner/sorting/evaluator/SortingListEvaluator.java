package chestcleaner.sorting.evaluator;

import chestcleaner.sorting.DefaultSortingList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SortingListEvaluator implements Evaluator {

    // Default Sorting List
    public static List<String> DEFAULT = DefaultSortingList.DEFAULT_SORTING_LIST;

    /***
     * Compares items against predefined sorting list
     * @param item1 item in question
     * @param item2 item compared against
     * @return true if item1 appears earlier in the sorting list than item2
     *         items in the list are smaller than items not in the list
     *         items not in the list are sorted by first character.
     */
    public boolean isSmallerThan(ItemStack item1, ItemStack item2) {
        String name1 = getMaterialName(item1);
        String name2 = getMaterialName(item2);

        int index1 = DEFAULT.indexOf(name1);
        int index2 = DEFAULT.indexOf(name2);

        if (index1 >= 0 && index2 >= 0) {
            return index1 < index2;
        } else if (index1 < 0 && index2 < 0) {
            return name1.compareTo(name2) < 0;
        } else {
            return index2 < 0;
        }
    }

    private String getMaterialName(ItemStack item) {
        try {
            return item.getType().getKey().toString();
        } catch (IllegalArgumentException e) { // cannot getKey of Legacy Material
            return "minecraft:" + item.getType().name().toLowerCase();
        }
    }
}
