package com.randude14.hungergames;

import org.bukkit.ChatColor;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.entity.Player;

public class GameCountdown implements Runnable {
	private final HungerGame game;
	private int countdown;
	private int taskId;
	private Player starter;
	private boolean isResuming;
	
	public GameCountdown(final HungerGame game, int num, boolean isResuming) {
		this.game = game;
		countdown = num;
		taskId = HungerGames.scheduleTask(this, 20L, 20L);
		this.isResuming = isResuming;
		if(isResuming) {
			ChatUtils.broadcast(true, "Resuming %s in %s...",
					game.getName(), HungerGames.formatTime(countdown));
		}
		else {
			ChatUtils.broadcast(true, "Starting %s in %s...",
					game.getName(), HungerGames.formatTime(countdown));
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
		this(game, Config.getDefaultTime(game.getSetup()), isResuming);
	}
	
	public GameCountdown(final HungerGame game) {
		this(game, Config.getDefaultTime(game.getSetup()), false);
	}
	
	public void cancel() {
		HungerGames.cancelTask(taskId);
	}

	public void run() {
		if (countdown <= 1) {
			HungerGames.cancelTask(taskId);
			game.setCounting(false);
			if (isResuming) {
				game.resumeGame(starter, 0);
			}
			else {
				game.startGame(starter, 0, false);
			}
			return;
		}
		countdown--;
		ChatColor color = ChatColor.GREEN;
		if(countdown <= 5) color = ChatColor.GOLD;
		if(countdown <= 3) color = ChatColor.RED;
		ChatUtils.broadcastRaw(true, color, "%s...", HungerGames.formatTime(countdown));
	}

}
