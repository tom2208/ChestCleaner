package chestcleaner.utils.messages;

import java.util.ArrayList;
import java.util.List;

import chestcleaner.config.Config;

public class StringTable {

	private static ArrayList<String> messages = new ArrayList<String>();
	private static ArrayList<String> defaultMessages = new ArrayList<>();
	
	public static void setUpList(List<String> list){
		
		defaultMessages.add("Syntex Error");
		defaultMessages.add("Error");
		defaultMessages.add("I'm sorry, but you do not have permission to perform this command.");
		defaultMessages.add("You can sort the next inventory in %time seconds.");
		defaultMessages.add("The block at the location %location has no inventory.");
		defaultMessages.add("Inventory sorted.");
		defaultMessages.add("There is no world with the name \"%worldname\".");
		defaultMessages.add("You have to be a player to perform this Command");
		defaultMessages.add("Timer: true");
		defaultMessages.add("Timer: false");
		defaultMessages.add("Timer: %time");
		defaultMessages.add("The name of your new cleaning item is now: \"%itemname§a\"");
		defaultMessages.add("Cleaning item lore was set.");
		defaultMessages.add("%newitem is now the new cleaning item.");
		defaultMessages.add("You have to hold an item to do this.");
		defaultMessages.add("You got an cleaning item.");
		defaultMessages.add("Cleaning item: true");
		defaultMessages.add("Cleaning item: false");
		defaultMessages.add("The player %playername got a cleaning item.");
		defaultMessages.add("Player %playername is not online.");
		defaultMessages.add("OpenInventoryEvent-DetectionMode was set to: %modeBoolean");
		defaultMessages.add("A new update is available at:§b https://www.spigotmc.org/resources/40313/updates");
		defaultMessages.add("DurabilityLoss: true");
		defaultMessages.add("DurabilityLoss: false");
		defaultMessages.add("The material of the item \"%material\" was added to the blacklist.");
		defaultMessages.add("The material \"%material\" was removed form the blacklist.");
		defaultMessages.add("The blacklist does not contain the material \"%material\".");
		defaultMessages.add("Index is out of bounds, it have to be bigger than -1 and smaller than %biggestindex.");
		defaultMessages.add("The blacklist is empty.");
		defaultMessages.add("The BlackList page %page:");
		defaultMessages.add("For the next entries: /list %nextpage");
		defaultMessages.add("Invalid input for an integer: %index");
		defaultMessages.add("Invalid page number (valid  page number range: %range)");
		defaultMessages.add("There is no Material with the name \"%material\".");
		defaultMessages.add("The blacklist was successfully cleared.");
		defaultMessages.add("The material %material is already on the blacklist.");
		defaultMessages.add("This Inventory is on the blacklist, you can't sort it.");
		defaultMessages.add("There is no pattern with this id.");
		defaultMessages.add("Pattern was set.");
		defaultMessages.add("There is no evaluator with this id.");
		defaultMessages.add("Evaluator was set.");
		
		if(list == null){
			
			messages = defaultMessages;
			Config.setStrings(defaultMessages);
			
		}else if(list.size() >= defaultMessages.size()){
			messages = (ArrayList<String>) list;
		}else{
			messages = (ArrayList<String>) list;
			for(int i = messages.size() ; i < defaultMessages.size(); i++){
				messages.add(defaultMessages.get(i));
			}
			Config.setStrings(messages);
		}
		
	}
	
	public static String getMessage(MessageID id) {
		return messages.get(id.getID());
	}

	public static String getMessage(MessageID id, String target, String replacement) {

		String out = messages.get(id.getID());
		out = out.replace(target, replacement);

		return out;
	}

}
