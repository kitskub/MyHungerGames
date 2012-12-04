package com.randude14.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.randude14.hungergames.Defaults.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

public class TeleportListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.INSTANCE.getRawPlayingSession(player);
		if (session == null) return;
		if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN) && !Config.CAN_TELEPORT.getBoolean(session.getSetup())) {
			ChatUtils.error(player, "You cannot teleport while in-game!");
			//Logging.debug("Cancelling a teleport.");
			event.setCancelled(true);
		}
		
	}

}
