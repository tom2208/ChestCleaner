package chestcleaner.sorting.categorizer;

import chestcleaner.config.serializable.WordCategory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class PredicateCategorizer extends Categorizer {

    Predicate<ItemStack> predicate;

    public PredicateCategorizer(String name, Predicate<ItemStack> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    public PredicateCategorizer(WordCategory wordCategory) {
        this.name = wordCategory.getName();
        this.predicate = item -> item.getType().name().toLowerCase().contains(wordCategory.getValue().toLowerCase());
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        List<List<ItemStack>> returnItems = new ArrayList<>();
        Map<Boolean, List<ItemStack>> lists = doCategorizationGetMap(items);
        returnItems.add(lists.get(Boolean.TRUE));
        returnItems.add(lists.get(Boolean.FALSE));
        return returnItems;
    }

    public Map<Boolean, List<ItemStack>> doCategorizationGetMap(List<ItemStack> items) {
        return items.stream().collect(Collectors.partitioningBy(predicate));
    }
}
