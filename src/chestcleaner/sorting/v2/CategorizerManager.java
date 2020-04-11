package chestcleaner.sorting.v2;

import chestcleaner.config.serializable.Category;
import chestcleaner.config.serializable.ListCategory;
import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.config.serializable.WordCategory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategorizerManager {

    private static List<Categorizer> masterCategorizers = new ArrayList<>();
    private static List<Categorizer> listCategorizers = new ArrayList<>();
    private static List<Categorizer> wordCategorizers = new ArrayList<>();
    private static List<Categorizer> internalCategorizers = new ArrayList<>();

    public static void addInternalCategorizer(Categorizer categorizer) {
        if (getByName(categorizer.getName()) == null) {
            internalCategorizers.add(categorizer);
        } else {
            throw new IllegalArgumentException(categorizer.getName());
        }
    }

    public static void addCategory(Category<?> category) {
        if (getByName(category.getName()) == null) {
            if (category instanceof MasterCategory) {
                masterCategorizers.add(new MasterCategorizer((MasterCategory) category));
            } else if (category instanceof ListCategory) {
                listCategorizers.add(new ListCategoryCategorizer((ListCategory) category));
            } else if (category instanceof WordCategory) {
                wordCategorizers.add(new PredicateCategorizer((WordCategory) category));
            }
        } else {
            throw new IllegalArgumentException(category.getName());
        }
    }

    public static boolean validateExists(List<String> strings) {
        return strings.stream()
                .map(CategorizerManager::getByName)
                .filter(Objects::nonNull)
                .count() == strings.size();
    }

    public static List<Categorizer> getAllCategorizers() {
        ArrayList<Categorizer> list = new ArrayList<>(masterCategorizers);
        list.addAll(listCategorizers);
        list.addAll(wordCategorizers);
        list.addAll(internalCategorizers);
        return list;
    }

    public static Categorizer getByName(String name) {
        return getAllCategorizers().stream().filter(
                categorizer -> categorizer.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public static List<String> getAllNames() {
        return getAllCategorizers().stream().map(Categorizer::getName).collect(Collectors.toList());
    }

    public static List<ItemStack> sort(List<ItemStack> items, List<String> categorizerNames) {

        List<Categorizer> categorizers = categorizerNames.stream()
                .map(CategorizerManager::getByName).collect(Collectors.toList());

        List<List<ItemStack>> categories = new ArrayList<>();
        categories.add(items);

        for (Categorizer categorizer : categorizers) {
            categories = categorizer.categorize(categories);
        }

        return categories.stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
