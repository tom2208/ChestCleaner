package chestcleaner.sorting.v2;

import java.util.ArrayList;
import java.util.List;

public class Categorizers {

    public static List<Categorizer> availableCategorizers = new ArrayList<>();

    public static void addCategorizer(Categorizer categorizer) {
        availableCategorizers.add(categorizer);
    }

    public static Categorizer getByName(String name) {

        Categorizer result = availableCategorizers.stream().filter(
                categorizer -> categorizer.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);

        if (result == null) {
            throw new IllegalArgumentException("Category " + name + " doesn't exist.");
        }

        return result;
    }

}
