package chestcleaner.sorting.categorizer;

import chestcleaner.config.serializable.MasterCategory;
import chestcleaner.sorting.CategorizerManager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MasterCategorizer extends Categorizer{

    private List<String> subCategorizers;

    public MasterCategorizer(MasterCategory masterCategory) {
        this.name = masterCategory.getName();
        this.subCategorizers = masterCategory.getValue();
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        List<List<ItemStack>> returnItems = new ArrayList<>();
        returnItems.add(CategorizerManager.sort(items, subCategorizers));
        return returnItems;
    }
}
