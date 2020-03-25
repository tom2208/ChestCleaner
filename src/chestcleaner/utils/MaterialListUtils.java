package chestcleaner.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.enums.MessageID;
import chestcleaner.utils.messages.enums.MessageType;

public class MaterialListUtils {

	public static void sendListPageToPlayer(List<Material> list, Player p, int page, int maxPageLines, int pages) {

		MessageSystem.sendMessageToPlayerWithReplacement(MessageType.SUCCESS , MessageID.BLACKLIST_PAGE, p, page + " / " + pages);

		for (int i = (page - 1) * maxPageLines; i < page * maxPageLines; i++) {
			if (list.size() == i) {
				break;
			} else {
				MessageSystem.sendMessageToPlayer(MessageType.UNHEADED_INFORMATION, (i + 1) + ". " + list.get(i).name(),
						p);
			}
		}

		if (pages > page) {
			MessageSystem.sendMessageToPlayerWithReplacement(MessageType.SUCCESS , MessageID.BLACKLIST_NEXT_PAGE, p, String.valueOf(page + 1));

		}

	}

}
