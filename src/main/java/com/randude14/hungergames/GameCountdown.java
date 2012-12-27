package com.randude14.hungergames;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;
import com.randude14.hungergames.utils.GeneralUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCountdown implements Runnable {
	private final HungerGame game;
	private int countdown;
	private int task;
	private CommandSender starter;
	private boolean isResuming;
	
	public GameCountdown(final HungerGame game, int num, boolean isResuming) {
		this.game = game;
		countdown = num;
		task = Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), this, 20L, 20L).getTaskId();
		this.isResuming = isResuming;
		if(isResuming) {
			ChatUtils.broadcast(game, "Resuming %s in %s...",
					game.getName(), GeneralUtils.formatTime(countdown));
		}
		else {
			ChatUtils.broadcast(game, "Starting %s in %s...",
					game.getName(), GeneralUtils.formatTime(countdown));
		}

	}
	
	public GameCountdown(final HungerGame game, int num, boolean isResuming, Player starter) {
		this(game, num, isResuming);
		this.starter = starter;
	}

	public GameCountdown(final HungerGame game, int num) {
		this(game, num, false);
	}

	public GameCountdown(final HungerGame game, int num, Player starter) {
		this(game, num, false, starter);
	}
	
	public GameCountdown(final HungerGame game, boolean isResuming) {
		this(game, Defaults.Config.DEFAULT_TIME.getInt(game.getSetup()), isResuming);
	}
	
	public GameCountdown(final HungerGame game) {
		this(game, Defaults.Config.DEFAULT_TIME.getInt(game.getSetup()), false);
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
			if (isResuming) {
				game.resumeGame(starter, 0);
			}
			else {
				game.startGame(starter, 0);
			}
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
