package chestcleaner.config;

import chestcleaner.config.serializable.ListCategory;
import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.config.serializable.WordCategory;
import chestcleaner.sorting.CategorizerManager;
import chestcleaner.sorting.categorizer.ComparatorCategorizer;
import chestcleaner.sorting.categorizer.ListCategoryCategorizer;
import chestcleaner.sorting.categorizer.MasterCategorizer;
import chestcleaner.sorting.categorizer.PredicateCategorizer;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class CategoryLoader {

    private static Predicate<ItemStack> is_block = item -> item.getType().isBlock();
    private static Predicate<ItemStack> is_item = item -> item.getType().isItem();
    private static Predicate<ItemStack> is_edible = item -> item.getType().isEdible();
    private static Predicate<ItemStack> is_fuel = item -> item.getType().isFuel();
    private static Predicate<ItemStack> is_flammable = item -> item.getType().isFlammable();

    private static Comparator<ItemStack> alphabet_asc = Comparator.comparing(item -> item.getType().name());
    private static Comparator<ItemStack> alphabet_desc = alphabet_asc.reversed();

    private static Comparator<ItemStack> alphabet_back_asc = Comparator.comparing(
            item -> new StringBuilder(item.getType().name()).reverse().toString());
    private static Comparator<ItemStack> alphabet_back_desc = alphabet_back_asc.reversed();


    public static void loadCategorizers(List<WordCategory> wordCategories, List<ListCategory> listCategories,
                                        List<MasterCategory> masterCategories) {
        loadStaticCategorizers();
        loadWordCategorizers(wordCategories);
        loadListCategorizers(listCategories);
        loadMasterCategorizers(masterCategories);
    }

    private static void loadStaticCategorizers() {
        CategorizerManager.addCategorizer(new PredicateCategorizer("is_block", is_block));
        CategorizerManager.addCategorizer(new PredicateCategorizer("is_item", is_item));
        CategorizerManager.addCategorizer(new PredicateCategorizer("is_edible", is_edible));
        CategorizerManager.addCategorizer(new PredicateCategorizer("is_fuel", is_fuel));
        CategorizerManager.addCategorizer(new PredicateCategorizer("is_flammable", is_flammable));

        CategorizerManager.addCategorizer(new ComparatorCategorizer("alpha_asc", alphabet_asc));
        CategorizerManager.addCategorizer(new ComparatorCategorizer("alpha_desc", alphabet_desc));
        CategorizerManager.addCategorizer(new ComparatorCategorizer("alpha_back_asc", alphabet_back_asc));
        CategorizerManager.addCategorizer(new ComparatorCategorizer("alpha_back_desc", alphabet_back_desc));
    }

    private static void loadWordCategorizers(List<WordCategory> categories) {
        if (categories == null) {
            return;
        }
        categories.stream().map(PredicateCategorizer::new).forEach(CategorizerManager::addCategorizer);
    }

    private static void loadListCategorizers(List<ListCategory> categories) {
        if (categories == null) {
            return;
        }
        categories.stream().map(ListCategoryCategorizer::new).forEach(CategorizerManager::addCategorizer);
    }

    private static void loadMasterCategorizers(List<MasterCategory> categories) {
        if (categories == null) {
            return;
        }
        categories.stream().map(MasterCategorizer::new).forEach(CategorizerManager::addCategorizer);
    }
}
