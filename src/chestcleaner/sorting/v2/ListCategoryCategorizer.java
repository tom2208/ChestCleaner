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
        // todo: implement
        // all items represented in the list should be sorted by listIndex
        // and put into one list
        // all items not in the list, should be put into another list (no sorting)
        //
        List<List<ItemStack>> returnItems = new ArrayList<>();
        returnItems.add(items);
        return returnItems;
    }

    private static String getMaterialName(ItemStack item) {
        try {
            return item.getType().getKey().toString();
        } catch (IllegalArgumentException e) { // cannot getKey of Legacy Material
            return "minecraft:" + item.getType().name().toLowerCase();
        }
    }
}
