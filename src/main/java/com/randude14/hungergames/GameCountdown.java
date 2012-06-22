package com.randude14.hungergames;

import org.bukkit.ChatColor;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

public class GameCountdown implements Runnable {
	private final HungerGame game;
	private int countdown;
	private int taskId;

	public GameCountdown(final HungerGame game, int num, boolean isResuming) {
		this.game = game;
		countdown = num;
		taskId = Plugin.scheduleTask(this, 20L, 20L);
		if(isResuming) {
			ChatUtils.broadcast("Resuming %s in %s...",
					game.getName(), Plugin.formatTime(countdown));
		}
		else {
			ChatUtils.broadcast("Starting %s in %s...",
					game.getName(), Plugin.formatTime(countdown));
		}

	}

	public GameCountdown(final HungerGame game, int num) {
		this(game, num, false);
	}
	
	public GameCountdown(final HungerGame game, boolean isResuming) {
		this(game, Config.getDefaultTime(game.getSetup()), isResuming);
	}
	
	public GameCountdown(final HungerGame game) {
		this(game, Config.getDefaultTime(game.getSetup()), false);
	}
	
	public void cancel() {
		Plugin.cancelTask(taskId);
	}

	public void run() {
		if (countdown <= 1) {
			Plugin.cancelTask(taskId);
			game.startGame();
			ChatUtils.broadcastRaw("Start!!");
			return;
		}
		countdown--;
		ChatColor color = ChatColor.GREEN;
		if(countdown <= 5)
			color = ChatColor.GOLD;
		if(countdown <= 3)
			color = ChatColor.RED;
		ChatUtils.broadcastRaw(color, "%s...", Plugin.formatTime(countdown));
	}

}
