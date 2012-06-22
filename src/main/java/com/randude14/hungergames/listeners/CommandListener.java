package com.randude14.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

public class CommandListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		if(message.startsWith("/" + HungerGames.CMD_ADMIN) || message.startsWith("/" + HungerGames.CMD_USER)) return;
		HungerGame session = GameManager.getSession(player);
		if(session == null) return;
		if(!Config.getUseCommand(session.getSetup())) {
			ChatUtils.error(player, "Cannot use commands while in game %s.");
			event.setCancelled(true);
		}
		
	}

}
