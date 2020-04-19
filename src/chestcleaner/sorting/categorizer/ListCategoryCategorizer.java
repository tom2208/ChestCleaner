package chestcleaner.sorting.categorizer;

import chestcleaner.config.serializable.ListCategory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ListCategoryCategorizer extends Categorizer {

    List<String> list;
    PredicateCategorizer predicateCategorizer;
    ComparatorCategorizer comparatorCategorizer;

    public ListCategoryCategorizer(ListCategory listCategory) {
        this.name = listCategory.getName();
        this.list = listCategory.getValue().stream().map(String::toLowerCase).collect(Collectors.toList());

        this.predicateCategorizer = new PredicateCategorizer("",
                item -> list.indexOf(item.getType().name().toLowerCase()) >= 0);

        this.comparatorCategorizer = new ComparatorCategorizer("",
                Comparator.comparing(item -> list.indexOf(item.getType().name().toLowerCase())));
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
}
