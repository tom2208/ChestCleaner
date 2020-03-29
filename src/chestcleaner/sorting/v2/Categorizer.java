package chestcleaner.sorting.v2;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


// sortItems method, todo: extract to InventorySorter(?)
class SortingImpl {
    public List<ItemStack> sortItems(List<ItemStack> items, Categorizer[] categorizers) {
        // create Starting point
        List<List<ItemStack>> categories = new ArrayList<>();
        categories.add(items);

        // categorize
        for (Categorizer categorizer : categorizers) {
            categories = categorizer.categorize(categories);
        }

        // iterate over category Lists and merge them into one
        List<ItemStack> returnItems = new ArrayList<>();

        for (List<ItemStack> category : categories) {
            returnItems.addAll(category);
        }
        return returnItems;
    }
}

public abstract class Categorizer {

    /**
     * categorizes every existing categories into finer grained categories
     * by calling doCategorization on each one and merging the results
     * @param categories categories to categorize further
     * @return List of Lists of ItemStacks (categories)
     */
    public List<List<ItemStack>> categorize(List<List<ItemStack>> categories) {
        List<List<ItemStack>> returnCategories = new ArrayList<>();

        for (List<ItemStack> category : categories) {
            returnCategories.addAll(doCategorization(category));
        }
        return returnCategories;
    }

    /**
     * categorizes a single list into multiple others
     * @param items ItemStack to categorize
     * @return List of Lists of ItemStacks (categories)
     */
    public abstract List<List<ItemStack>> doCategorization(List<ItemStack> items);
}
