package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.logging.Level;

import org.bukkit.block.Chest;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			String setup = session.getSetup();
			if(Config.getCanPlaceBlock(setup, event.getBlock())) {
				ChatUtils.error(player, "You cannot place this block while in game %s.", session.getName());
				event.setCancelled(true);
				return;
			}
		}
		else if (GameManager.getGame(GameManager.getSpectating(player)) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot place this block while spectating %s.", GameManager.getSpectating(player));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerBreakBlockMonitor(BlockBreakEvent event) {
		for (HungerGame game : GameManager.getGames()) {
			game.removeChest(event.getBlock().getLocation());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			String setup = session.getSetup();
			if(Config.getCanBreakBlock(setup, event.getBlock())) {
				ChatUtils.error(player, "You cannot break this block while in game %s.", session.getName());
				event.setCancelled(true);
				return;
			}
		}
		else if (GameManager.getGame(GameManager.getSpectating(player)) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot break this block while spectating %s.", GameManager.getSpectating(player));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractMonitor(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!(event.getClickedBlock() instanceof Chest)) return;

                Player player = event.getPlayer();
                HungerGame game = GameManager.getPlayingSession(player);
                if(game == null) return;
		if(!Config.getAutoAdd(game.getSetup())) return;
		Logging.log(Level.FINEST, "Inventory opened and checking for fill. Player: {0}", player.getName());
                game.addAndFillChest((Chest) event.getClickedBlock());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			String setup = session.getSetup();
			if(Config.getCanInteractBlock(setup, event.getClickedBlock())) {
				ChatUtils.error(player, "You cannot interact with this block while in game %s.", session.getName());
				event.setCancelled(true);
				return;
			}
		}
		else if (GameManager.getGame(GameManager.getSpectating(player)) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot interact with this block while spectating %s.", GameManager.getSpectating(player));
		}
	}

}
