package me.kitskub.hungergames.listeners;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.games.PlayerQueueHandler;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;


public class PlayerListener implements Listener {

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void playerKilled(PlayerDeathEvent event) {
		Player killed = event.getEntity();
		HungerGame gameOfKilled = (HungerGame) HungerGames.getInstance().getGameManager().getRawPlayingSession(killed);
		if (gameOfKilled == null) return;
		Player killer = killed.getKiller();
		if (killer != null) {
			HungerGame gameOfKiller = (HungerGame) HungerGames.getInstance().getGameManager().getRawPlayingSession(killer);
			if (gameOfKiller == null) return;
			if (gameOfKilled.compareTo(gameOfKiller) == 0) {
				gameOfKiller.killed(killer, killed, event);
			}
		}
		else {
			gameOfKilled.killed(null, killed, event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public static void playerQuit(PlayerQuitEvent event) {
		((GameManager) HungerGames.getInstance().getGameManager()).playerLeftServer(event.getPlayer());
		HungerGames.playerLeftServer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public static void playerKick(PlayerKickEvent event) {
		((GameManager) HungerGames.getInstance().getGameManager()).playerLeftServer(event.getPlayer());
		HungerGames.playerLeftServer(event.getPlayer());
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		Location frozenLoc = HungerGames.getInstance().getGameManager().getFrozenLocation(player);
		if (frozenLoc == null) {
			return;
		}
		int px = player.getLocation().getBlockX();
		int pz = player.getLocation().getBlockZ();
		int fx = frozenLoc.getBlockX();
		int fz = frozenLoc.getBlockZ();
		if ((px != fx) || (pz != fz)) {
			TeleportListener.allowTeleport(player);
			Location loc = frozenLoc.clone();
			loc.setPitch(player.getLocation().getPitch());
			loc.setYaw(player.getLocation().getYaw());
			player.teleport(loc, PlayerTeleportEvent.TeleportCause.UNKNOWN);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event) {
		PlayerQueueHandler.addPlayer(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerSneak(PlayerToggleSneakEvent event) {
		HungerGame game;
		if ((game = ((GameManager) HungerGames.getInstance().getGameManager()).getRawPlayingSession(event.getPlayer())) == null) return;
		if (!Config.HIDE_PLAYERS.getBoolean(game.getSetup())) return;
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)	
	public void onItemPickup(PlayerPickupItemEvent event) {
		if (HungerGames.getInstance().getGameManager().getSpectating(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}
}
