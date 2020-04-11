package chestcleaner.utils;

import org.apache.commons.lang.Validate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StringUtils {

    /**
     * Same basic functionality as org.bukkit.util.StringUtil copyPartialMatches
     * but also works to complete comma seperated lists
     * @param token what was already input
     * @param originals all possibilities
     * @param collection matching possibilities
     * @param <T> Collection of Strings
     */
    public static <T extends Collection<? super String>> void copyPartialMatchesCommas(
            String token, Iterable<String> originals, T collection) {

        Validate.notNull(token, "Search token cannot be null");
        Validate.notNull(collection, "Collection cannot be null");
        Validate.notNull(originals, "Originals cannot be null");

        List<String> list = Arrays.asList(token.split(",", -1));
        token = list.get(list.size() - 1);
        String prefix = list.size() > 1 ? String.join(",", list.subList(0, list.size() - 1)) : "";

        for (String string : originals) {
            if(string.toLowerCase().startsWith(token.toLowerCase())) {
                collection.add(prefix + string);
            }
        }
    }

    public static <T extends Collection<? super String>> void copyPartialMatchesCommasNoDuplicates(
            String token, Iterable<String> originals, T collection) {

        Validate.notNull(token, "Search token cannot be null");
        Validate.notNull(collection, "Collection cannot be null");
        Validate.notNull(originals, "Originals cannot be null");

        List<String> list = Arrays.asList(token.split(",", -1));
        token = list.get(list.size() - 1);
        String prefix = list.size() > 1 ? String.join(",", list.subList(0, list.size() - 1)) + "," : "";

        for (String string : originals) {
            if(string.toLowerCase().startsWith(token.toLowerCase()) && !list.contains(string)) {
                collection.add(prefix + string);
            }
        }
    }

    public static boolean isStringTrueOrFalse(String str) {
        return Boolean.parseBoolean(str) || str.equalsIgnoreCase(Boolean.FALSE.toString());
    }

    public static List<String> getBooleanValueStringList(){
        return Arrays.asList(Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }
}
