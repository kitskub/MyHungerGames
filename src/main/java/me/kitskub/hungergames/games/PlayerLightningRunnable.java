package me.kitskub.hungergames.games;

import me.kitskub.hungergames.Defaults.Config;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PlayerLightningRunnable implements Runnable{
	private final HungerGame game;
	private BukkitTask task;
	private long lastLightningTime;
	private int nextLightningIndex;
	
	public PlayerLightningRunnable(HungerGame game) {
		this.game = game;
		this.lastLightningTime = 0;
		this.nextLightningIndex = 0;
	}
	
	public void run() {
		if(Config.LIGHTNING_ON_PLAYER_COUNT.getInt(game.getSetup()) > 0 && game.getRemainingPlayers().size() <= Config.LIGHTNING_ON_PLAYER_COUNT.getInt(game.getSetup())){
			if(lastLightningTime == 0 || System.currentTimeMillis() > lastLightningTime + Config.LIGHTNING_ON_PLAYER_DELAY.getInt(game.getSetup()) * 1000){
				if (game.getRemainingPlayers().isEmpty()) {
					cancel();
					return;
				}
				if(nextLightningIndex >= game.getRemainingPlayers().size()) nextLightningIndex = 0;
				
				Player target = game.getRemainingPlayers().get(nextLightningIndex).getPlayer();
				Location location = target.getLocation();
				location.setY(1);
				target.getWorld().strikeLightningEffect(location);
								
				nextLightningIndex++;
				lastLightningTime = System.currentTimeMillis();				
			}
		}
		
	}
	
	public void cancel() {
		task.cancel();
		task = null;
		this.lastLightningTime = 0;
		this.nextLightningIndex = 0;
	}
	
	public void setTask(BukkitTask task) {
		this.task = task;
	}
}
