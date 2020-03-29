package chestcleaner.sorting.v2;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class PredicateCategorizer extends Categorizer {

    Predicate<ItemStack> predicate;

    public PredicateCategorizer(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        List<List<ItemStack>> returnItems = new ArrayList<>();
        //returnItems.add(items.stream().filter(predicate).collect(Collectors.toList()));
        //returnItems.add(items.stream().filter(predicate.negate()).collect(Collectors.toList()));
        //return returnItems;

        // more lines but i believe more efficient, cause go through items only once
        List<ItemStack> trueItems = new ArrayList<>();
        List<ItemStack> falseItems = new ArrayList<>();

        for (ItemStack item : items) {
            if (predicate.test(item)) {
                trueItems.add(item);
            } else {
                falseItems.add(item);
            }
        }
        returnItems.add(trueItems);
        returnItems.add(falseItems);

        return returnItems;
    }
}
