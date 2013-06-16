package me.kitskub.hungergames.listeners;

import java.util.HashSet;
import java.util.Set;
import me.kitskub.hungergames.Defaults;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.api.Game.GameState;
import me.kitskub.hungergames.games.Arena;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.GeneralUtils;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ArenaListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreakNormal(BlockBreakEvent event) {
		for (Arena a : GeneralUtils.getArenasIn(event.getBlock().getLocation())) {
			if (!HungerGames.hasPermission(event.getPlayer(), Defaults.Perm.ADMIN_EDIT_ARENA) || (a.getActiveGame() != null && a.getActiveGame().getState() == GameState.RUNNING)) {
				ChatUtils.error(event.getPlayer(), "You cannot break this block while in this arena.");
				event.setCancelled(true);
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlaceNormal(BlockPlaceEvent event) {
		for (Arena a : GeneralUtils.getArenasIn(event.getBlock().getLocation())) {
			if (!HungerGames.hasPermission(event.getPlayer(), Defaults.Perm.ADMIN_EDIT_ARENA) || (a.getActiveGame() != null && a.getActiveGame().getState() == GameState.RUNNING)) {
				ChatUtils.error(event.getPlayer(), "You cannot place this block while in this arena.");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractNormal(PlayerInteractEvent event) {
		Set<Arena> toCall = new HashSet<Arena>();
		toCall.addAll(GeneralUtils.getArenasIn(event.getClickedBlock().getLocation()));
		toCall.addAll(GeneralUtils.getArenasIn(event.getPlayer().getLocation()));
		for (Arena a : toCall) {
			boolean cancel = false;
			//if (!event.isCancelled() && event.getClickedBlock() != null && !HungerGames.hasPermission(event.getPlayer(), Defaults.Perm.ADMIN_EDIT_ARENA) || (a.getActiveGame() != null && (a.getActiveGame().getState() == GameState.RUNNING || a.getActiveGame().getState() == GameState.WAITING || a.getActiveGame().getState() == GameState.COUNTING))) {
				//ChatUtils.error(event.getPlayer(), "You cannot interact with this while in this arena.");
			//	cancel = true;
			//}
			if (cancel) {
				event.setUseItemInHand(Result.DENY);
				event.getPlayer().updateInventory();
			}
		}
	}
}
