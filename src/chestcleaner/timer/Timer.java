package chestcleaner.timer;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import chestcleaner.main.Main;
import chestcleaner.utils.messages.MessageID;
import chestcleaner.utils.messages.MessageSystem;
import chestcleaner.utils.messages.MessageType;
import chestcleaner.utils.messages.StringTable;

public class Timer {

	private Player p;
	private int time;

	public Timer(Player p, int time) {
		this.p = p;
		this.time = time;
	}

	public Player getPlayer() {
		return p;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	static ArrayList<Timer> times = new ArrayList<>();

	public static void update() {
		if (Main.timer) {
			for (Timer t : times)
				t.setTime(t.getTime() - 1);

			ArrayList<Integer> remove = new ArrayList<>();

			for (int i = 0; i < times.size(); i++)
				if (times.get(i).getTime() <= 0)
					remove.add(i);
			for (int i : remove)
				times.remove(i);
		}
	}

	public static boolean isPlayerOnList(Player p) {
		for (Timer t : times)
			if (t.getPlayer().equals(p))
				return true;
		return false;
	}

	public static int getPlayerTime(Player p) {
		for (Timer t : times)
			if (p.equals(p))
				return t.getTime();
		return 0;
	}

	public static void setPlayerOnList(Player p) {
		times.add(new Timer(p, Main.time));
	}

	public static boolean playerCheck(Player p) {
		if (Main.timer && !p.hasPermission("chestcleaner.timer.noeffect")) {
			if (isPlayerOnList(p)) {
				MessageSystem.sendMessageToPlayer(MessageType.ERROR, StringTable
						.getMessage(MessageID.SORTING_ON_COOLDOWN, "%time", String.valueOf(getPlayerTime(p))), p);
				return false;
			}
			setPlayerOnList(p);
			return true;
		}
		return true;
	}

}
