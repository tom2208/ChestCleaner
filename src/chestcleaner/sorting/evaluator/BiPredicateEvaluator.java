package chestcleaner.sorting.evaluator;

import org.bukkit.inventory.ItemStack;

import java.util.function.BiPredicate;

public class BiPredicateEvaluator implements Evaluator {

    private BiPredicate<ItemStack, ItemStack> biPredicate;

    public BiPredicateEvaluator(BiPredicate<ItemStack, ItemStack> biPredicate) {
        this.biPredicate = biPredicate;
    }

    @Override
    public boolean isSmallerThan(ItemStack item1, ItemStack item2) {
        return biPredicate.test(item1, item2);
    }
}
