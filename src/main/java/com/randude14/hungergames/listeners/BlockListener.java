package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat.PlayerState;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Material;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		if(!event.getLine(0).equalsIgnoreCase("[MyHungerGames]")) return;
		String[] lines = event.getLines();
		SignListener.ListenerType type = SignListener.ListenerType.byId(lines[1]);
		if (type == null) return;
		HungerGame game = null;
		if (lines[2] != null && !lines[2].equals("")) {
			game = (HungerGame) HungerGames.getInstance().getGameManager().getRawGame(lines[2]);
			if (game == null) {
				event.setLine(1, "");
				event.setLine(2, "BAD GAME NAME!");
				event.setLine(3, "");
				return;
			}
		}
		Sign sign = (Sign) event.getBlock().getState();
		if (!HungerGames.checkPermission(event.getPlayer(), type.getPerm())) {
			event.setLine(1, "");
			event.setLine(2, "NO PERMISSION!");
			event.setLine(3, "");
			return;
		}

		if (SignListener.addSign(type, game, sign)) {
			ChatUtils.send(event.getPlayer(), "Sign was created successfully.");
		}
		else {
			ChatUtils.error(event.getPlayer(), "Sign was not created.");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onchestBreak(BlockBreakEvent event) {
		if (!(event.getBlock().getState() instanceof Chest)) return;
		for (HungerGame game : ((GameManager) HungerGames.getInstance().getGameManager()).getRawGames()) {
			game.chestBroken(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		HungerGame session = ((GameManager) HungerGames.getInstance().getGameManager()).getRawPlayingSession(player);
		if(session != null) {
			if (session.getPlayerStat(player).getState().equals(PlayerState.WAITING)) {
				event.setCancelled(true);
				return;
			}
			String setup = session.getSetup();
			if(!Config.getCanPlaceBlock(setup, event.getBlock())) {
				ChatUtils.error(player, "You cannot place this block while in game %s.", session.getName());
				event.setCancelled(true);
				return;
			}
		}
		else if (HungerGames.getInstance().getGameManager().getSpectating(player) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot place this block while spectating %s.", HungerGames.getInstance().getGameManager().getSpectating(player));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		HungerGame session = ((GameManager) HungerGames.getInstance().getGameManager()).getRawPlayingSession(player);
		if(session != null) {
			if (session.getPlayerStat(player).getState().equals(PlayerState.WAITING)) {
				event.setCancelled(true);
				return;
			}
			String setup = session.getSetup();
			if(!Config.getCanBreakBlock(setup, event.getBlock())) {
				ChatUtils.error(player, "You cannot break this block while in game %s.", session.getName());
				event.setCancelled(true);
				return;
			}
		}
		else if (HungerGames.getInstance().getGameManager().getSpectating(player) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot break this block while spectating %s.", HungerGames.getInstance().getGameManager().getSpectating(player));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractMonitor(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!(event.getClickedBlock().getState() instanceof Chest)) return;

                Player player = event.getPlayer();
                HungerGame game = ((GameManager) HungerGames.getInstance().getGameManager()).getRawPlayingSession(player);
                if(game == null) return;
		if(!Defaults.Config.AUTO_ADD.getBoolean(game.getSetup())) return;
		
		// Logging.log(Level.FINEST, "Inventory opened and checking for fill. Player: {0}", player.getName());
                game.addAndFillChest((Chest) event.getClickedBlock().getState());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		Player player = event.getPlayer();
		HungerGame session = ((GameManager) HungerGames.getInstance().getGameManager()).getRawPlayingSession(player);
		if(session != null) {
			if (session.getPlayerStat(player).getState().equals(PlayerState.WAITING)) {
				event.setCancelled(true);
				return;
			}
			if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			String setup = session.getSetup();
			if(!Config.getCanInteractBlock(setup, event.getClickedBlock()) && !event.getClickedBlock().getType().equals(Material.CHEST)) {
				ChatUtils.error(player, "You cannot interact with this block while in game %s.", session.getName());
				event.setCancelled(true);
				return;
			}
		}
		else if (HungerGames.getInstance().getGameManager().getSpectating(player) != null) { // TODO configurable
			event.setCancelled(true);
			ChatUtils.error(player, "You cannot interact with this block while spectating %s.", HungerGames.getInstance().getGameManager().getSpectating(player));
		}
	}
}