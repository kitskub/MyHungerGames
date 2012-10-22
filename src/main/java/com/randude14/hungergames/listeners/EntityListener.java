package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.api.Game.GameState;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.stats.PlayerStat.PlayerState;
import com.randude14.hungergames.stats.PlayerStat.Team;
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
		HungerGame hurtGame = GameManager.INSTANCE.getRawSession(player);
		if (hurtGame != null) {
			if (Config.getForceDamage(hurtGame.getSetup())) {
				event.setCancelled(false);
			}
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent) event;
				double period = Config.getGracePeriod(hurtGame.getSetup());
				long startTime = hurtGame.getInitialStartTime();
				if (((System.currentTimeMillis() - startTime) / 1000) < period) {
					event.setCancelled(true);
					if (newEvent.getDamager() instanceof Player) {
						ChatUtils.error((Player) newEvent.getDamager(), "You can't hurt that player during the grace-period!");
					}
				}
				if (!Config.getAllowTeamFriendlyDamage(hurtGame.getSetup())) {
					Team hurtTeam = hurtGame.getPlayerStat(player).getTeam();
					if (newEvent.getDamager() instanceof Player && hurtTeam != null && hurtGame.contains((Player) newEvent.getDamager())){
						Team hurterTeam = hurtGame.getPlayerStat((Player) newEvent.getDamager()).getTeam();
						if (hurterTeam != null) {
							if (hurtTeam.getName().equals(hurterTeam.getName())) {
								event.setCancelled(true);
								ChatUtils.error((Player) newEvent.getDamager(), "You can't hurt a player on your team!");
							}
						}
					}
				}
			}
		}

		// Spectators
		if (event.isCancelled()) return;
		if (GameManager.INSTANCE.getSpectating(player) != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {
		if (!(event.getTarget() instanceof Player)) return;
		Player player = (Player) event.getTarget();
		// Games
		HungerGame game = GameManager.INSTANCE.getRawSession(player);
		if (game != null) {
			if (game.getState() == GameState.STOPPED) {
				if (!Config.getStopTargetting(game.getSetup())) return;
				PlayerStat stat = game.getPlayerStat(player);
				if (stat != null && stat.getState().equals(PlayerState.WAITING)) event.setCancelled(true); 
			}
		}
	}
}
