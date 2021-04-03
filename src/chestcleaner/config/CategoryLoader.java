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

    private static final Predicate<ItemStack> isBlock = item -> item.getType().isBlock();
    private static final Predicate<ItemStack> isItem = item -> item.getType().isItem();
    private static final Predicate<ItemStack> isSolid = item -> item.getType().isSolid();
    private static final Predicate<ItemStack> isInteractive = item -> item.getType().isInteractable();
    private static final Predicate<ItemStack> isOccluding = item -> item.getType().isOccluding();
    private static final Predicate<ItemStack> isEdible = item -> item.getType().isEdible();
    private static final Predicate<ItemStack> isFuel = item -> item.getType().isFuel();
    private static final Predicate<ItemStack> isFlammable = item -> item.getType().isFlammable();
    private static final Predicate<ItemStack> isBurnable = item -> item.getType().isBurnable();
    private static final Predicate<ItemStack> isRecord = item -> item.getType().isRecord();
    private static final Predicate<ItemStack> hasGravity = item -> item.getType().hasGravity();

    private static final Comparator<ItemStack> alphabetAsc = Comparator.comparing(item -> item.getType().name());
    private static final Comparator<ItemStack> alphabetDesc = alphabetAsc.reversed();

    private static final Comparator<ItemStack> alphabetBackAsc = Comparator.comparing(
            item -> new StringBuilder(item.getType().name()).reverse().toString());
    private static final Comparator<ItemStack> alphabetBackDesc = alphabetBackAsc.reversed();


    public static void loadCategorizers(List<WordCategory> wordCategories, List<ListCategory> listCategories,
                                        List<MasterCategory> masterCategories) {
        loadStaticCategorizers();
        loadWordCategorizers(wordCategories);
        loadListCategorizers(listCategories);
        loadMasterCategorizers(masterCategories);
    }

    private static void loadStaticCategorizers() {
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsBlock", isBlock));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsItem", isItem));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsSolid", isSolid));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsInteractable", isInteractive));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsOccluding", isOccluding));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsEdible", isEdible));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsFuel", isFuel));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsFlammable", isFlammable));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsBurnable", isBurnable));
        CategorizerManager.addCategorizer(new PredicateCategorizer("IsRecord", isRecord));
        CategorizerManager.addCategorizer(new PredicateCategorizer("HasGravity", hasGravity));

        CategorizerManager.addCategorizer(new ComparatorCategorizer("AlphaAsc", alphabetAsc));
        CategorizerManager.addCategorizer(new ComparatorCategorizer("AlphaDesc", alphabetDesc));
        CategorizerManager.addCategorizer(new ComparatorCategorizer("AlphaBackAsc", alphabetBackAsc));
        CategorizerManager.addCategorizer(new ComparatorCategorizer("AlphaBackDesc", alphabetBackDesc));
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
