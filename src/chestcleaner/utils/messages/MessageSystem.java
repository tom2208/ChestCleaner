package chestcleaner.utils.messages;

import org.bukkit.entity.Player;

import chestcleaner.main.ChestCleaner;

public class MessageSystem {

	public static void sendMessageToPlayer(MessageType type, String arg, Player p) {
		p.sendMessage(getMessageString(type, arg));
	}

	public static void sendMessageToPlayer(MessageType type, MessageID messageID, Player p) {
		p.sendMessage(getMessageString(type, StringTable.getMessage(messageID)));
	}

	public static void sendConsoleMessage(MessageType type, String arg) {
		ChestCleaner.main.getServer().getConsoleSender().sendMessage(getMessageString(type, arg));
	}

	public static void sendConsoleMessage(MessageType type, MessageID messageID) {
		ChestCleaner.main.getServer().getConsoleSender()
				.sendMessage(getMessageString(type, StringTable.getMessage(messageID)));
	}

	private static String getMessageString(MessageType type, String arg) {

		String out = "§6[ChestCleaner] ";

		switch (type) {
		case SYNTAX_ERROR:
			out += "§c" + StringTable.getMessage(MessageID.SYTAX_ERROR) + ": " + arg;
			break;
		case ERROR:
			out += "§c" + StringTable.getMessage(MessageID.ERROR) + ": " + arg;
			break;
		case SUCCESS:
			out += "§a" + arg;
			break;
		case MISSING_PERMISSION:
			out += "§c" + StringTable.getMessage(MessageID.PERMISSON_DENIED) + " ( " + arg + " )";
			break;
		case UNHEADED_INFORMATION:
			out = "§7" + arg;
			break;
		default:
			throw new IllegalArgumentException();
		}

		return out;

	}

}
