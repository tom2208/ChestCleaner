package chestcleaner.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import chestcleaner.playerdata.PlayerData;
import chestcleaner.playerdata.PlayerDataManager;
import chestcleaner.sorting.SortingPattern;
import chestcleaner.sorting.evaluator.EvaluatorType;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;

public class SortingConfigCommand implements CommandExecutor, TabCompleter{
	
	private final List<String> commandList = new ArrayList<>();
	
	public SortingConfigCommand() {
		
		commandList.add("pattern");
		commandList.add("evaluator");
		
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		
		if(!(cs instanceof Player)){
			return true;
		}
		
		Player p = (Player)cs;
		
		if(p.hasPermission("chestcleaner.cmd.sortingconfig")){
			
			if(args.length == 2){
				
				/* PATTERN */
				if(args[0].equalsIgnoreCase(commandList.get(0))){
					
					SortingPattern pattern = SortingPattern.getSortingPatternByName(args[1]);
					
					if(pattern == null){
						
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.NO_PATTERN_ID, p);
						return true;
						
					}else{
						
						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.NEW_PATTERN_SET, p);
						PlayerData.setSortingPattern(pattern, p);
						PlayerDataManager.loadPlayerData(p);
						return true;
						
					}
					
				}else if(args[0].equalsIgnoreCase(commandList.get(1))){
					
					EvaluatorType evaluator = EvaluatorType.getEvaluatorTypByName(args[1]);
					
					if(evaluator == null){
						
						MessageSystem.sendMessageToPlayer(MessageType.ERROR, MessageID.NO_EVALUATOR_ID, p);
						return true;
						
					}else{
						
						MessageSystem.sendMessageToPlayer(MessageType.SUCCESS, MessageID.NEW_EVALUATOR_SET, p);
						PlayerData.setEvaluatorTyp(evaluator, p);
						PlayerDataManager.loadPlayerData(p);
						return true;
						
					}
					
				}else{
					MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, "/sortingconfig <pattern/evaluator>", p);
					return true;
				}
				
			}else{
				MessageSystem.sendMessageToPlayer(MessageType.SYNTAX_ERROR, "/sortingconfig <pattern/evaluator>", p);
				return true;
			}
			
		}else{
			MessageSystem.sendMessageToPlayer(MessageType.MISSING_PERMISSION, "chestcleaner.cmd.sortingconfig", p);
			return true;
		}
		
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
		
		final List<String> completions = new ArrayList<>();

		if (args.length <= 1) {

			StringUtil.copyPartialMatches(args[0], commandList, completions);

		} else if (args.length == 2) {

			if(args[0].equalsIgnoreCase(commandList.get(0))) StringUtil.copyPartialMatches(args[1], SortingPattern.getIDList(), completions);
			else if(args[0].equalsIgnoreCase(commandList.get(1))) StringUtil.copyPartialMatches(args[1], EvaluatorType.getIDList(), completions);
			
		}

		return completions;

		
	}
	
}
