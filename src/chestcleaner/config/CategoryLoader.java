package chestcleaner.config;

import chestcleaner.sorting.evaluator.BiPredicateEvaluator;
import chestcleaner.sorting.v2.Categorizers;
import chestcleaner.sorting.v2.EvaluatorCategorizer;
import chestcleaner.sorting.v2.ListCategoryCategorizer;
import chestcleaner.sorting.v2.MasterCategorizer;
import chestcleaner.sorting.v2.PredicateCategorizer;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class CategoryLoader {

    private static Predicate<ItemStack> is_block = item -> item.getType().isBlock();
    private static Predicate<ItemStack> is_item = item -> item.getType().isItem();
    private static Predicate<ItemStack> is_edible = item -> item.getType().isEdible();
    private static Predicate<ItemStack> is_fuel = item -> item.getType().isFuel();
    private static Predicate<ItemStack> is_flammable = item -> item.getType().isFlammable();

    private static BiPredicateEvaluator alphabet_asc =
            new BiPredicateEvaluator((item1, item2) -> item1.getType().name().compareTo(item2.getType().name()) < 0);

    private static BiPredicateEvaluator alphabet_desc =
            new BiPredicateEvaluator((item1, item2) -> item1.getType().name().compareTo(item2.getType().name()) > 0);

    private static BiPredicateEvaluator alphabet_back_asc =
            new BiPredicateEvaluator((item1, item2) -> new StringBuilder(item1.getType().name()).reverse().toString()
                    .compareTo(new StringBuilder(item2.getType().name()).reverse().toString()) < 0);

    private static BiPredicateEvaluator alphabet_back_desc =
            new BiPredicateEvaluator((item1, item2) -> new StringBuilder(item1.getType().name()).reverse().toString()
                    .compareTo(new StringBuilder(item2.getType().name()).reverse().toString()) > 0);

    public static void loadCategorizers(List<WordCategory> wordCategories, List<ListCategory> listCategories,
                                        List<MasterCategory> masterCategories) {
        loadStaticCategorizers();
        loadWordCategorizers(wordCategories);
        loadListCategorizers(listCategories);
        loadMasterCategorizers(masterCategories);
    }

    private static void loadStaticCategorizers() {
        Categorizers.addCategorizer(new PredicateCategorizer("is_block", is_block));
        Categorizers.addCategorizer(new PredicateCategorizer("is_item", is_item));
        Categorizers.addCategorizer(new PredicateCategorizer("is_edible", is_edible));
        Categorizers.addCategorizer(new PredicateCategorizer("is_fuel", is_fuel));
        Categorizers.addCategorizer(new PredicateCategorizer("is_flammable", is_flammable));

        Categorizers.addCategorizer(new EvaluatorCategorizer("alpha_asc", alphabet_asc));
        Categorizers.addCategorizer(new EvaluatorCategorizer("alpha_desc", alphabet_desc));
        Categorizers.addCategorizer(new EvaluatorCategorizer("alpha_back_asc", alphabet_back_asc));
        Categorizers.addCategorizer(new EvaluatorCategorizer("alpha_back_desc", alphabet_back_desc));
    }

    private static void loadWordCategorizers(List<WordCategory> categories) {
        if (categories == null) {
            return;
        }
        categories.forEach(category -> Categorizers.addCategorizer(new PredicateCategorizer(category)));
    }

    private static void loadListCategorizers(List<ListCategory> categories) {
        if (categories == null) {
            return;
        }
        categories.forEach(category -> Categorizers.addCategorizer(new ListCategoryCategorizer(category)));
    }

    private static void loadMasterCategorizers(List<MasterCategory> categories) {
        if (categories == null) {
            return;
        }
        categories.forEach(category -> Categorizers.addCategorizer(new MasterCategorizer(category)));
    }
}
