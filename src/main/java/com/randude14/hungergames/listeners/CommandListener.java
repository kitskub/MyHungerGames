package com.randude14.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Plugin;
import com.randude14.hungergames.games.HungerGame;

public class CommandListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		if(message.startsWith("/" + Plugin.CMD_ADMIN) || message.startsWith("/" + Plugin.CMD_USER)) return;
		HungerGame session = GameManager.getSession(player);
		if(session == null) return;
		if(!Config.getUseCommand(session.getSetup())) {
			Plugin.error(player, "Cannot use commands while in game %s.");
			event.setCancelled(true);
		}
		
	}

}
