package chestcleaner.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

	public static boolean isStringBoolean(CommandSender sender, String bool) {
		if (StringUtils.isStringNotTrueOrFalse(bool)) {
			MessageSystem.sendMessageToCS(MessageType.ERROR, MessageID.ERROR_VALIDATION_BOOLEAN, sender);
			return false;
		}
		return true;
	}
    
    public static boolean isStringNotTrueOrFalse(String str) {
        return !str.equalsIgnoreCase(Boolean.TRUE.toString()) && !str.equalsIgnoreCase(Boolean.FALSE.toString());
    }

    public static ItemStack getAsBook(String string) {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        assert bm != null;
        bm.setPages(separateIntoPages(string));
        book.setItemMeta(bm);
        return book;
    }

    public static List<String> separateIntoPages(String string) {
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
