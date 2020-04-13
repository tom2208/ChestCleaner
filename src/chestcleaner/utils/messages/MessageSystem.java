package chestcleaner.utils.messages;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import chestcleaner.main.ChestCleaner;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

import java.util.List;

public class MessageSystem {

	public static void sendMessageToCS(MessageType type, String arg, CommandSender cs) {
		cs.sendMessage(getMessageString(type, arg));
	}

	public static void sendMessageToCS(MessageType type, MessageID messageID, CommandSender cs) {
		sendMessageToCS(type, ChestCleaner.main.getRB().getString(messageID.getID()), cs);
	}

	public static void sendConsoleMessage(MessageType type, MessageID messageID) {
		sendMessageToCS(type, messageID, ChestCleaner.main.getServer().getConsoleSender());
	}

	/**
	 * Sends a message with the MessageID {@code messageID} and the MessageType
	 * {@code messageType} to the CommandSender {@code cs} (player or console)
	 * replacing placeholders using java's String.format(str, args)
	 * see https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax for details
	 *
	 * @param type        the MessageType of the message.
	 * @param messageID   the MessageID of the Message.
	 * @param cs      the player who should receive the message.
	 * @param replacement the replacement variables
	 */
	public static void sendMessageToCSWithReplacement(MessageType type, MessageID messageID, CommandSender cs,
			Object... replacement) {
		String message = ChestCleaner.main.getRB().getString(messageID.getID());
		sendMessageToCS(type, String.format(message, replacement), cs);
	}

	public static void sendListPageToCS(List<String> list, CommandSender sender, String pageNrAsString, int maxPageLines) {

		int pages = (int) Math.ceil(list.size() / (double) maxPageLines);
		int page;

		try {
			page = Integer.parseInt(pageNrAsString);
		} catch (NumberFormatException ex) {
			sendMessageToCSWithReplacement(MessageType.ERROR, MessageID.NOT_AN_INTEGER, sender, pageNrAsString);
			return;
		}

		if (page < 0 || page > pages) {
			sendMessageToCSWithReplacement(MessageType.ERROR,
					MessageID.INVALID_PAGE_NUMBER, sender, "1 - " + pages);
			return;
		}

		sendMessageToCSWithReplacement(MessageType.SUCCESS,
				MessageID.BLACKLIST_PAGE, sender, page + " / " + pages);

		for (int i = (page - 1) * maxPageLines; i < page * maxPageLines; i++) {
			if (list.size() == i) {
				break;
			} else {
				sendMessageToCS(MessageType.UNHEADED_INFORMATION, (i + 1) + ". " + list.get(i), sender);
			}
		}

		if (pages > page) {
			sendMessageToCSWithReplacement(MessageType.SUCCESS,
					MessageID.BLACKLIST_NEXT_PAGE, sender, String.valueOf(page + 1));
		}
	}

	private static String getMessageString(MessageType type, String arg) {

		String out = ChestCleaner.main.getRB().getString(MessageID.PREFIX.getID()) + " ";

		switch (type) {
		case SYNTAX_ERROR:
			out += ChatColor.RED + ChestCleaner.main.getRB().getString(MessageID.SYNTAX_ERROR.getID()) + ": " + arg;
			break;
		case ERROR:
			out += ChatColor.RED + ChestCleaner.main.getRB().getString(MessageID.ERROR.getID()) + ": " + arg;
			break;
		case SUCCESS:
			out += ChatColor.GREEN + arg;
			break;
		case MISSING_PERMISSION:
			out += ChatColor.RED + ChestCleaner.main.getRB().getString(MessageID.NO_PERMISSION_FOR_COMMAND.getID())
					+ " ( " + arg + " )";
			break;
		case UNHEADED_INFORMATION:
			out = ChatColor.GRAY + arg;
			break;
		default:
			throw new IllegalArgumentException();
		}

		return out;

	}

}
