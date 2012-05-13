package com.randude14.hungergames.listeners;

import java.util.List;

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
		List<Integer> list = Config.getSpecialBlocksPlace(setup);
		boolean contains = list.contains(event.getBlock().getTypeId());
		boolean canPlaceBlocks = Config.getCanPlaceBlock(setup);
		if(contains && canPlaceBlocks) {
			Plugin.error(player, "Cannot place this block while in game %s.", session.getName());
			event.setCancelled(true);
			return;
		}
		
		if(!contains && !canPlaceBlocks) {
			Plugin.error(player, "Cannot place this block while in game %s.", session.getName());
			event.setCancelled(true);
			return;
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getSession(player);
		if(session == null) return;
		String setup = session.getSetup();
		List<Integer> list = Config.getSpecialBlocksBreak(setup);
		boolean contains = list.contains(event.getBlock().getTypeId());
		boolean canBreakBlocks = Config.getCanBreakBlock(setup);
		if(contains && canBreakBlocks) {
			Plugin.error(player, "Cannot break this block while in game %s.", session.getName());
			event.setCancelled(true);
			return;
		}
		
		if(!contains && !canBreakBlocks) {
			Plugin.error(player, "Cannot break this block while in game %s.", session.getName());
			event.setCancelled(true);
			return;
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getSession(player);
		if(session == null) return;
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		String setup = session.getSetup();
		List<Integer> list = Config.getSpecialBlocksInteract(setup);
		boolean contains = list.contains(event.getClickedBlock().getTypeId());
		boolean canInteractWithBlocks = Config.getCanInteractBlock(setup);
		if(contains && canInteractWithBlocks) {
			Plugin.error(player, "Cannot interact with this block while in game %s.", session.getName());
			event.setCancelled(true);
			return;
		}
		
		if(!contains && !canInteractWithBlocks) {
			Plugin.error(player, "Cannot interact with this block while in game %s.", session.getName());
			event.setCancelled(true);
			return;
		}
		
	}

}
