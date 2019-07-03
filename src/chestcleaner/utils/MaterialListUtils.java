package chestcleaner.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import chestcleaner.sorting.InventorySorter;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

public class MaterialListUtils {
	 	
	public static void sendListPageToPlayer(List<Material> list, Player p, int page, int maxPageLines, int pages){
		
		MessageSystem.sendMessageToPlayer(MessageType.SUCCESS,
				StringTable.getMessage(MessageID.BLACKLIST_TITLE, "%page", page + " / " + pages), p);
		
		for (int i = (page - 1) * maxPageLines; i < page * maxPageLines; i++) {
			if (list.size() == i) {
				break;
			} else {
				MessageSystem.sendMessageToPlayer(MessageType.UNHEADED_INFORMATION,
						(i + 1) + ". " + list.get(i).name(), p);
			}
		}
		
		if (pages > page) {
			MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, StringTable
					.getMessage(MessageID.NEXT_ENTRIES, "%nextpage", String.valueOf(page + 1)), p);
		}
		
	}
	
}
