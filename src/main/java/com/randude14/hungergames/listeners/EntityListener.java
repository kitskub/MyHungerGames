package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.api.Game.GameState;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.stats.PlayerStat.PlayerState;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		// Games
		HungerGame killedGame = GameManager.INSTANCE.getSession(player);
		if (killedGame != null) {
			if (Config.getForceDamage(killedGame.getSetup())) {
				event.setCancelled(false);
				return;
			}
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent) event;
				double period = Config.getGracePeriod(killedGame.getSetup());
				long startTime = killedGame.getInitialStartTime();
				if (((System.currentTimeMillis() - startTime) / 1000) < period) {
					event.setCancelled(true);
					if (newEvent.getDamager() instanceof Player) {
						ChatUtils.error((Player) newEvent.getDamager(), "You can't hurt that player during the grace-period!");
					}
				}
			}
		}

		// Spectators
		if (event.isCancelled()) return;
		String name = GameManager.INSTANCE.getSpectating(player);
		if (name != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled=true)
	public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {
		if (!(event.getTarget() instanceof Player)) return;
		Player player = (Player) event.getTarget();
		// Games
		HungerGame game = GameManager.INSTANCE.getSession(player);
		if (game != null) {
			if (game.getState() == GameState.STOPPED) {
				if (!Config.getStopTargetting(game.getSetup())) return;
				PlayerStat stat = game.getPlayerStat(player);
				if (stat != null && stat.getState().equals(PlayerState.WAITING)) event.setCancelled(true); 
			}
		}
	}
}
