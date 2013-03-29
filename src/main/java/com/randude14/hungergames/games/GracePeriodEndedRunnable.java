package com.randude14.hungergames.games;

import com.randude14.hungergames.Lang;
import com.randude14.hungergames.Defaults.Config;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

public class GracePeriodEndedRunnable implements Runnable{
	private final HungerGame game;
	private BukkitTask task;
	
	public GracePeriodEndedRunnable(HungerGame game) {
		this.game = game;
	}
	
	public void run() {
		double period = Config.GRACE_PERIOD.getDouble(game.getSetup());
		if (((System.currentTimeMillis() - game.getInitialStartTime()) / 1000) >= period) {
			ChatUtils.broadcast(game, ChatColor.DARK_PURPLE, Lang.getGracePeriodEnded(game.getSetup()));
			this.cancel();
		}		
	}
	
	public void cancel() {
		if(task != null)
			task.cancel();
		task = null;
	}
	
	public void setTask(BukkitTask task) {
		this.task = task;
	}
}
