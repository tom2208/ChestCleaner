package chestcleaner.sorting.v2;

import chestcleaner.config.ListCategory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class ListCategoryCategorizer extends Categorizer {

    List<String> list;

    public ListCategoryCategorizer(ListCategory listCategory) {
        this.name = listCategory.getName();
        this.list = listCategory.getValue();
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        List<List<ItemStack>> returnItems = new ArrayList<>();
        // todo: implement
        returnItems.add(items);
        return returnItems;
    }
}
