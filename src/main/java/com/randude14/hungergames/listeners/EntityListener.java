package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		// Games
		HungerGame game = GameManager.getSession(player);
		if (game != null) {
			if (Config.getForceDamage(game.getSetup())) {
				event.setCancelled(false);
			}
		}
		
		// Spectators
		if (event.isCancelled()) return;
		String name = GameManager.getSpectating(player);
		if (name != null) {
			event.setCancelled(true);
		}
	}
}
