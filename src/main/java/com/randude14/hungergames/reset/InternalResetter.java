package com.randude14.hungergames.reset;

import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.api.Game;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.Cuboid;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.InventoryHolder;

public class InternalResetter extends Resetter implements Listener, Runnable {
	private static final Map<HungerGame, Map<Location, BlockState>> changedBlocks = new ConcurrentHashMap<HungerGame, Map<Location, BlockState>>();
	//private static final Map<HungerGame, Map<Location, ItemStack[]>> changedInvs = new ConcurrentHashMap<HungerGame, Map<Location, ItemStack[]>>();
	private static final Map<Location, BlockState> toCheck = new ConcurrentHashMap<Location, BlockState>();
	//private static final Map<Location, ItemStack[]> toCheckInvs = new ConcurrentHashMap<Location, ItemStack[]>();


	@Override
	public void init() {
		Bukkit.getPluginManager().registerEvents(this, HungerGames.getInstance());
		Bukkit.getScheduler().runTaskTimerAsynchronously(HungerGames.getInstance(), this, 0, 20);
	}

	@Override
	public void beginGame(HungerGame game) {
	}

	@Override
	public boolean resetChanges(HungerGame game) {
		synchronized (toCheck) {
			toCheck.clear();
		}
		if(!changedBlocks.containsKey(game)) return true;
		Map<Location, BlockState> map = changedBlocks.get(game);
		int chests = 0;
		for(Location l : map.keySet()) {
			BlockState state = map.get(l);
			state.update(true);
			if (!(state instanceof InventoryHolder)) continue;
			//if (!changedInvs.get(eMap).containsKey(l)) continue;
			//((InventoryHolder) state).getInventory().setContents(changedInvs.get(eMap).get(l));
			chests++;

		}
		Logging.debug("Reset " + chests + " chests");
		changedBlocks.get(game).clear();
		return true;
	}

	private static HungerGame insideGame(Location loc) {
		for (HungerGame game : ((GameManager) HungerGames.getInstance().getGameManager()).getRawGames()) {
			if (game.getState() != Game.GameState.RUNNING) continue;
			if (game.getWorlds().size() <= 0 && game.getCuboids().size() <= 0) return null;
			if (game.getWorlds().contains(loc.getWorld())) return game;
			for (Cuboid c : game.getCuboids()) {
				if (c.isLocationWithin(loc)) return game;
			}
		}
		return null;
	}

	public void run() {
	synchronized(toCheck) {
		for (Iterator<Location> it = toCheck.keySet().iterator(); it.hasNext();) {
			Location loc = it.next();
			HungerGame game = insideGame(loc);
			if (game != null) {
				addBlockState(game, loc, toCheck.get(loc));
				//addInv(game, loc, toCheckInvs.get(loc));
			}
			it.remove();
		}
	}
	}

	private static void addToCheck(Block block, BlockState state) {
		synchronized(toCheck) {
			toCheck.put(block.getLocation(), state);
			//if (state instanceof InventoryHolder) {
			//	toCheckInvs.put(block.getLocation(), ((InventoryHolder) state).getInventory().getContents());
			//}
		}
	}

	private static synchronized void addBlockState(HungerGame game, Location loc, BlockState state) {
		if (!changedBlocks.containsKey(game)) changedBlocks.put(game, new HashMap<Location, BlockState>());
		if (changedBlocks.get(game).containsKey(loc)) return; // Don't want to erase the original block
		changedBlocks.get(game).put(loc, state);
	}

	//private static synchronized void addInv(HungerGame game, Location loc, ItemStack[] inv) {
	//	EquatableWeakReference<HungerGame> eMap = new EquatableWeakReference<HungerGame>(game);
	//	if (!changedInvs.containsKey(eMap)) changedInvs.put(eMap, new HashMap<Location, ItemStack[]>());
	//	if (changedInvs.get(eMap).containsKey(loc)) return; // Don't want to erase the original block
	//	changedInvs.get(eMap).put(loc, inv);
	//}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof InventoryHolder) {
			addToCheck(event.getClickedBlock(), event.getClickedBlock().getState());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExplosion(EntityExplodeEvent event) {
		for (Block b : event.blockList()) {
			addToCheck(b, b.getState());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		addToCheck(event.getBlock(), event.getBlockReplacedState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());	    
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeavesDecay(LeavesDecayEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockFromTo(BlockFromToEvent event) {
		addToCheck(event.getBlock(), event.getBlock().getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVehicleDestroy(VehicleDestroyEvent event) {
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVehicleMove(VehicleMoveEvent event) {
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityTeleport(EntityTeleportEvent event) {
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
	}
 
}
