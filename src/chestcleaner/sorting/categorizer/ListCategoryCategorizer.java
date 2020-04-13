package chestcleaner.sorting.categorizer;

import chestcleaner.config.serializable.ListCategory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class ListCategoryCategorizer extends Categorizer {

    List<String> list;
    PredicateCategorizer predicateCategorizer;
    ComparatorCategorizer comparatorCategorizer;

    public ListCategoryCategorizer(ListCategory listCategory) {
        this.name = listCategory.getName();
        this.list = listCategory.getValue();

        this.predicateCategorizer = new PredicateCategorizer("",
                item -> list.indexOf(getMaterialName(item)) >= 0);

        this.comparatorCategorizer = new ComparatorCategorizer("",
                Comparator.comparing(item -> list.indexOf(getMaterialName(item))));
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        List<List<ItemStack>> returnItems = new ArrayList<>();
        Map<Boolean, List<ItemStack>> map = predicateCategorizer.doCategorizationGetMap(items);
        List<ItemStack> sortedList = comparatorCategorizer.doCategorizationGetList(map.get(Boolean.TRUE));
        returnItems.add(sortedList);
        returnItems.add(map.get(Boolean.FALSE));
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
