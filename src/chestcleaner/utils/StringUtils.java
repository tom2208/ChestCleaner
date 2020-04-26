package chestcleaner.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
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
        String prefix = list.size() > 1 ? String.join(",", list.subList(0, list.size() - 1)) + "," : "";

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


    public static ItemStack getAsBook(String string) {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setPages(seperateIntoPages(string));
        book.setItemMeta(bm);
        return book;
    }

    public static List<String> seperateIntoPages(String string) {
        List<String> pages = new ArrayList<>();
        String curPage = "";
        int maxLines = 14;
        int curPageLines = 0;
        // pages
        for (String line : string.split("\n")) {
            int lineCount = countLinesNeeded(line);
            if (curPageLines + lineCount <= maxLines) {
                curPageLines += lineCount;
                curPage = curPage.concat(line).concat("\n");
            } else {
                pages.add(curPage);
                curPage = line.concat("\n");
                curPageLines = lineCount;
            }
        }
        pages.add(curPage);
        return pages;
    }

    private static int countLinesNeeded(String string) {
        double maxCharsPerLine = 19;
        int countLines = 1;
        int curLineChars = 0;

        for (String part : string.split(" ")) {
            if (part.length() + curLineChars < maxCharsPerLine) {
                curLineChars += part.length() + 1; // +1 for the space that belongs between the parts
            } else if (part.length() <= maxCharsPerLine) {
                countLines++;
                curLineChars = part.length();
            } else {
                double lineRatio = part.length() / maxCharsPerLine;
                countLines += Math.ceil(lineRatio);
                curLineChars = (int) (part.length() - ((int)lineRatio * maxCharsPerLine));
            }
        }
        return countLines;
    }
}
