package chestcleaner.sorting.v2;

import chestcleaner.sorting.evaluator.Evaluator;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class EvaluatorCategorizer extends Categorizer {

    Evaluator evaluator;

    public EvaluatorCategorizer(String name, Evaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
    }

    @Override
    public List<List<ItemStack>> doCategorization(List<ItemStack> items) {
        items = doCategorizationGetList(items);
        List<List<ItemStack>> returnItems = new ArrayList<>();
        returnItems.add(items);
        return returnItems;
    }

    public List<ItemStack> doCategorizationGetList(List<ItemStack> items) {
        return QuicksortV2.sort(items, evaluator, 0, items.size() - 1);
    }

}
