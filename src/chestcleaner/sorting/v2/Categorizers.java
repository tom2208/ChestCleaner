package chestcleaner.sorting.v2;

import chestcleaner.sorting.evaluator.BackBeginStringEvaluator;
import chestcleaner.sorting.evaluator.BeginBackStringEvaluator;
import chestcleaner.sorting.evaluator.SortingListEvaluator;
import org.bukkit.inventory.ItemStack;

public enum Categorizers {
    BLOCKS,
    ITEMS,
    EDIBLES,
    FLAMMABLES,
    FUEL,
    SLABS,
    STAIRS,
    STONE,
    ALPHANUMERIC_ASC,
    ALPHANUMERIC_DESC,
    ALPHANUMERIC_BACK_ASC,
    ALPHANUMERIC_BACK_DESC,
    SORTING_LIST;

    public Categorizer getCategorizer(Categorizers type) {
        switch (type) {
            case BLOCKS:
                return new PredicateCategorizer(item -> item.getType().isBlock());
            case ITEMS:
                return new PredicateCategorizer(item -> item.getType().isItem());
            case EDIBLES:
                return new PredicateCategorizer(item -> item.getType().isEdible());
            case FLAMMABLES:
                return new PredicateCategorizer(item -> item.getType().isFlammable());
            case FUEL:
                return new PredicateCategorizer(item -> item.getType().isFuel());
            case SLABS:
                return new PredicateCategorizer(item -> item.getType().name().toLowerCase().contains("slab"));
            case STAIRS:
                return new PredicateCategorizer(item -> item.getType().name().toLowerCase().contains("stair"));
            case STONE:
                return new PredicateCategorizer(item -> item.getType().name().toLowerCase().contains("stone"));
            case SORTING_LIST:
                return new EvaluatorCategorizer(new SortingListEvaluator());
            case ALPHANUMERIC_ASC:
                return new EvaluatorCategorizer(new BeginBackStringEvaluator());
            case ALPHANUMERIC_BACK_ASC:
                return new EvaluatorCategorizer(new BackBeginStringEvaluator());
            default:
                return new PredicateCategorizer(item -> true); // no sorting
        }
    }

}
