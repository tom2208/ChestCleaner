package chestcleaner.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

public class MaterialListUtils {

	public static void sendListPageToCS(List<Material> list, CommandSender sender, int page, int maxPageLines,
			int pages) {

		MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.BLACKLIST_PAGE, sender,
				page + " / " + pages);

		for (int i = (page - 1) * maxPageLines; i < page * maxPageLines; i++) {
			if (list.size() == i) {
				break;
			} else {
				MessageSystem.sendMessageToCS(MessageType.UNHEADED_INFORMATION, (i + 1) + ". " + list.get(i).name(),
						sender);
			}
		}

		if (pages > page) {
			MessageSystem.sendMessageToCSWithReplacement(MessageType.SUCCESS, MessageID.BLACKLIST_NEXT_PAGE, sender,
					String.valueOf(page + 1));

		}

	}

}
