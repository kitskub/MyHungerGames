package me.kitskub.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.utils.ChatUtils;

import java.util.HashSet;
import java.util.Set;

public class TeleportListener implements Listener {
	private static Set<String> playerWhiteList = new HashSet<String>();
	
	public static void allowTeleport(Player player) {
		playerWhiteList.add(player.getName());
	}

	@EventHandler(ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		boolean isWhiteListed = playerWhiteList.remove(player.getName());
		HungerGame session = ((GameManager) HungerGames.getInstance().getGameManager()).getRawPlayingSession(player);
		if (session == null) return;
		if (Config.CAN_TELEPORT.getBoolean(session.getSetup()) && !isWhiteListed && (event.getCause().equals(PlayerTeleportEvent.TeleportCause.UNKNOWN))) {
			ChatUtils.error(player, "You cannot teleport while in-game!");
			//Logging.debug("Cancelling a teleport.");
			event.setCancelled(true);
		}
		
	}

}
