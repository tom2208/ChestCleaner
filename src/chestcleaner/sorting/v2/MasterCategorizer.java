package chestcleaner.sorting.v2;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MasterCategorizer extends Categorizer{

    private List<String> subCategorizers;

    public MasterCategorizer(String name, List<String> subCategorizers) {
        this.name = name;
        this.subCategorizers = subCategorizers;
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        // todo: only execute or executeAndMerge?
        // eg. make one category out of all subCategories, or leave all subCategories as their own category?
        return Categorizers.executeCategorizers(items, subCategorizers);
    }
}
