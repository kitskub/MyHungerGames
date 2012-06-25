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
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

public class BlockListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			String setup = session.getSetup();
			List<Integer> list = Config.getSpecialBlocksPlace(setup);
			List<Integer> listglobal = Config.getSpecialBlocksPlaceGlobal();
			boolean canPlaceBlocksGlobal = Config.getCanPlaceBlockGlobal();
			boolean canPlaceBlocksSetup = Config.getCanPlaceBlock(setup);
			boolean containsGlobal = listglobal.contains(event.getBlock().getTypeId());
			boolean containsSetup = list.contains(event.getBlock().getTypeId());
			boolean canGlobal = containsGlobal ^ canPlaceBlocksGlobal;
			boolean canSetup = containsSetup ^ canPlaceBlocksSetup;
			if(canGlobal || canSetup) {
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
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			String setup = session.getSetup();
			List<Integer> list = Config.getSpecialBlocksBreak(setup);
			List<Integer> listglobal = Config.getSpecialBlocksBreakGlobal();
			boolean canBreakBlocksGlobal = Config.getCanBreakBlockGlobal();
			boolean canBreakBlocksSetup = Config.getCanBreakBlock(setup);
			boolean containsGlobal = listglobal.contains(event.getBlock().getTypeId());
			boolean containsSetup = list.contains(event.getBlock().getTypeId());
			boolean canGlobal = containsGlobal ^ canBreakBlocksGlobal;
			boolean canSetup = containsSetup ^ canBreakBlocksSetup;
			if(canGlobal || canSetup) {
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
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			String setup = session.getSetup();
			List<Integer> list = Config.getSpecialBlocksInteract(setup);
			List<Integer> listglobal = Config.getSpecialBlocksInteractGlobal();
			boolean canInteractBlocksGlobal = Config.getCanInteractBlockGlobal();
			boolean canInteractBlocksSetup = Config.getCanInteractBlock(setup);
			boolean containsGlobal = listglobal.contains(event.getClickedBlock().getTypeId());
			boolean containsSetup = list.contains(event.getClickedBlock().getTypeId());
			boolean canGlobal = containsGlobal ^ canInteractBlocksGlobal;
			boolean canSetup = containsSetup ^ canInteractBlocksSetup;
			if(canGlobal || canSetup) {
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
