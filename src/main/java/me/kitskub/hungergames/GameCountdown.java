package me.kitskub.hungergames;

import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.GeneralUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCountdown implements Runnable {
	private final HungerGame game;
	private int countdown;
	private int task;
	private CommandSender starter;
	
	public GameCountdown(final HungerGame game, int num) {
		this.game = game;
		countdown = num;
		task = Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), this, 20L, 20L).getTaskId();
		starter = Bukkit.getConsoleSender();
		ChatUtils.broadcast(game, "Starting %s in %s...", game.getName(), GeneralUtils.formatTime(countdown));
	}
	
	public GameCountdown(final HungerGame game, int num, Player starter) {
		this(game, num);
		this.starter = starter;
	}


	public GameCountdown(final HungerGame game) {
		this(game, Defaults.Config.DEFAULT_TIME.getInt(game.getSetup()));
	}

	public void cancel() {
		Bukkit.getScheduler().cancelTask(task);
	}

	public int getTimeLeft() {
		return countdown;
	}

	public void run() {
		if (countdown <= 1) {
			cancel();
			game.setDoneCounting();
			game.startGame(starter, 0);
			return;
		}
		countdown--;
		ChatColor color = ChatColor.GREEN;
		if(countdown <= 5) color = ChatColor.GOLD;
		if(countdown <= 3) color = ChatColor.RED;
		ChatUtils.broadcastRaw(game, color, "%s...", GeneralUtils.formatTime(countdown));
	}
	
	public void setStarter(CommandSender starter) {
		this.starter = starter;
	}

}
