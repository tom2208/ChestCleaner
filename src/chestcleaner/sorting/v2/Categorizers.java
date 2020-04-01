package chestcleaner.sorting.v2;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Categorizers {

    public static List<Categorizer> availableCategorizers = new ArrayList<>();

    public static void addCategorizer(Categorizer categorizer) {
        availableCategorizers.add(categorizer);
    }

    public static Categorizer getByName(String name) {

        Categorizer result = availableCategorizers.stream().filter(
                categorizer -> categorizer.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);

        if (result == null) {
            throw new IllegalArgumentException("Category " + name + " doesn't exist.");
        }

        return result;
    }

    public static List<List<ItemStack>> executeCategorizers(List<ItemStack> items, List<String> categorizerNames) {

        List<Categorizer> categorizers = categorizerNames.stream()
                .map(Categorizers::getByName).collect(Collectors.toList());

        List<List<ItemStack>> categories = new ArrayList<>();
        categories.add(items);

        for (Categorizer categorizer : categorizers) {
            categories = categorizer.categorize(categories);
        }
        return categories;
    }

    public static List<ItemStack> mergeCategoryLists(List<List<ItemStack>> categories) {
        return categories.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public static List<ItemStack> categorizeAndMerge(List<ItemStack> items, List<String> categorizerNames) {
        return mergeCategoryLists(executeCategorizers(items, categorizerNames));
    }

}
