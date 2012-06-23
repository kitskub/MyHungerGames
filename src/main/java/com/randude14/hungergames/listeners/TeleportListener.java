package com.randude14.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

public class TeleportListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session == null) return;
		if(!Config.getCanTeleport(session.getSetup())) {
			ChatUtils.error(player, "Cannot teleport while in game %s.");
			event.setCancelled(true);
		}
		
	}

}
