package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.games.PlayerQueueHandler;


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
		HungerGame gameOfKilled = GameManager.INSTANCE.getRawPlayingSession(killed);
		if (gameOfKilled == null) return;
		Player killer = killed.getKiller();
		if (killer != null) {
			HungerGame gameOfKiller = GameManager.INSTANCE.getRawPlayingSession(killer);
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
		GameManager.INSTANCE.playerLeftServer(event.getPlayer());
		HungerGames.playerLeftServer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public static void playerKick(PlayerKickEvent event) {
		GameManager.INSTANCE.playerLeftServer(event.getPlayer());
		HungerGames.playerLeftServer(event.getPlayer());
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		Location frozenLoc = GameManager.INSTANCE.getFrozenLocation(player);
		if (frozenLoc == null) {
			return;
		}
		int px = player.getLocation().getBlockX();
		int pz = player.getLocation().getBlockZ();
		int fx = frozenLoc.getBlockX();
		int fz = frozenLoc.getBlockZ();
		if ((px != fx) || (pz != fz)) {
			player.teleport(frozenLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event) {
		PlayerQueueHandler.addPlayer(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerSneak(PlayerToggleSneakEvent event) {
		HungerGame game;
		if ((game = GameManager.INSTANCE.getRawPlayingSession(event.getPlayer())) == null) return;
		if (!Config.getHidePlayers(game.getSetup())) return;
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)	
	public void onItemPickup(PlayerPickupItemEvent event) {
		if (GameManager.INSTANCE.getSpectating(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}
}
