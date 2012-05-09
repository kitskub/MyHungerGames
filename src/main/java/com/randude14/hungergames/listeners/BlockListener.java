package com.randude14.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Plugin;
import com.randude14.hungergames.games.HungerGame;

public class BlockListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getSession(player);
		if(session == null) return;
		String setup = session.getSetup();
		if(!Config.getCanPlaceBlock(setup)) {
			Plugin.error(player, "Cannot place blocks while in game %s", session.getName());
			event.setCancelled(true);
		}

	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getSession(player);
		if(session == null) return;
		String setup = session.getSetup();
		if(!Config.getCanBreakBlock(setup)) {
			Plugin.error(player, "Cannot break blocks while in game %s", session.getName());
			event.setCancelled(true);
		}

	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getSession(player);
		if(session == null) return;
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(event.getClickedBlock().getState() == null) return;
		String setup = session.getSetup();
		if(!Config.getCanInteractBlock(setup)) {
			Plugin.error(player, "Cannot interact with blocks while in game %s", session.getName());
			event.setCancelled(true);
		}

	}

}
