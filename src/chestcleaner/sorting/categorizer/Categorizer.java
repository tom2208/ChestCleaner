package chestcleaner.sorting.categorizer;

import chestcleaner.sorting.CategorizerManager;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Categorizer {

    /**
     * The unique name of the categorizer
     */
    String name;

    public String getName() {
        return name;
    }

    /**
     * categorizes every existing categories into finer grained categories
     * by calling doCategorization on each one and merging the results
     *
     * @param categories categories to categorize further
     * @return List of Lists of ItemStacks (categories)
     */
    public List<List<ItemStack>> categorize(List<List<ItemStack>> categories) {
        return categories.stream()
                .map(this::doCategorization)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * categorizes a single list into multiple others
     *
     * @param items ItemStack to categorize
     * @return List of Lists of ItemStacks (categories)
     */
    public abstract List<List<ItemStack>> doCategorization(List<ItemStack> items);
}
