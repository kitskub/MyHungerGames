package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		// Games
		HungerGame game = GameManager.getSession(player);
		if (game != null) {
			if (Config.getForceDamage(game.getSetup())) {
				event.setCancelled(false);
				return;
			}
		}
		
		// Spectators
		if (event.isCancelled()) return;
		String name = GameManager.getSpectating(player);
		if (name != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority= EventPriority.HIGHEST)
	public void onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		// Games
		HungerGame game = GameManager.getSession(player);
		if (game != null) {
			double period = Config.getGracePeriod(game.getSetup());
			long startTime = game.getInitialStartTime();
			if (((System.currentTimeMillis() - startTime) / 1000) < period) {
				event.setCancelled(true);
				if (event.getDamager() instanceof Player) {
					ChatUtils.error((Player) event.getDamager(), "You can't hurt that player during the grace-period!");
				}
			}
		}
	}
}
