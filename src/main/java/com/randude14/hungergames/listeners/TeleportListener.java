package com.randude14.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;

public class TeleportListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if (session == null) return;
		if (!Config.getCanTeleport(session.getSetup())) {
			Logging.debug("Cancelling a teleport.");
			event.setCancelled(true);
		}
		
	}

}
