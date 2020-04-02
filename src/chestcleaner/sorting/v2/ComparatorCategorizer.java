package chestcleaner.sorting.v2;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ComparatorCategorizer extends Categorizer {

    Comparator<ItemStack> comparator;

    public ComparatorCategorizer(String name, Comparator<ItemStack> comparator) {
        this.name = name;
        this.comparator = comparator;
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        return new ArrayList<>(Collections.singletonList(doCategorizationGetList(items)));
    }

    public List<ItemStack> doCategorizationGetList(List<ItemStack> items) {
        //items.sort(comparator);
        //return items;
        return QuicksortV2.sort(items, comparator, 0, items.size() - 1);
    }

}
