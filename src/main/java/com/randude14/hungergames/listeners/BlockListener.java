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
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
                        String setup = session.getSetup();
			List<Integer> list = Config.getSpecialBlocksBreak(setup);
                        List<Integer> listglobal = Config.getSpecialBlocksBreakGlobal();
			boolean canPlaceBlockssetup = Config.getCanPlaceBlockGlobal();
                        boolean canPlaceBlocks = Config.getCanPlaceBlock(setup);
                        boolean containsglobal = listglobal.contains(event.getBlock().getTypeId());
                        boolean containssetup = list.contains(event.getBlock().getTypeId());
			if(!containsglobal && !containssetup && !canPlaceBlocks && !canPlaceBlockssetup) {
                            ChatUtils.error(player, "You cannot place this block while in game %s.", session.getName());
                            event.setCancelled(true);
			}
		}
		if (GameManager.getGame(GameManager.getSpectating(player)) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot place this block while spectating %s.", GameManager.getSpectating(player));
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			String setup = session.getSetup();
			List<Integer> list = Config.getSpecialBlocksBreak(setup);
                        List<Integer> listglobal = Config.getSpecialBlocksBreakGlobal();
			boolean canBreakBlockssetup = Config.getCanBreakBlock(setup);
                        boolean canBreakBlocks = Config.getCanBreakBlockGlobal();
                        boolean containsglobal = listglobal.contains(event.getBlock().getTypeId());
                        boolean containssetup = list.contains(event.getBlock().getTypeId());
			if(!containsglobal && !containssetup && !canBreakBlocks && !canBreakBlockssetup) {
                            ChatUtils.error(player, "You cannot break this block while in game %s.", session.getName());
                            event.setCancelled(true);
			}
		}
		if (GameManager.getGame(GameManager.getSpectating(player)) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot break this block while spectating %s.", GameManager.getSpectating(player));
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		HungerGame session = GameManager.getPlayingSession(player);
		if(session != null) {
			if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			String setup = session.getSetup();
			List<Integer> list = Config.getSpecialBlocksInteract(setup);
                        List<Integer> listglobal = Config.getSpecialBlocksBreakGlobal();
                        boolean containsglobal = listglobal.contains(event.getClickedBlock().getTypeId());
			boolean containssetup = list.contains(event.getClickedBlock().getTypeId());
			boolean canInteractSetup = Config.getCanInteractBlock(setup);
                        boolean canInteractGlobal = Config.getCanInteractBlockGlobal();
			if(!containssetup && !containsglobal && !canInteractSetup &&!canInteractGlobal) {
                            ChatUtils.error(player, "You cannot interact with this block while in game %s.", session.getName());
                            event.setCancelled(true);
                            return;
			}
		}
		if (GameManager.getGame(GameManager.getSpectating(player)) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot interact with this block while spectating %s.", GameManager.getSpectating(player));
		}
	}

}
