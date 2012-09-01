package com.randude14.hungergames.reset;

import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.Cuboid;
import com.randude14.hungergames.utils.EquatableWeakReference;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
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

public class InternalResetter extends Resetter implements Listener, Runnable {
	private static final Map<EquatableWeakReference<HungerGame>, Map<Location, BlockState>> changedBlocks = Collections.synchronizedMap(new WeakHashMap<EquatableWeakReference<HungerGame>, Map<Location, BlockState>>());
	private static final Map<Block, BlockState> toCheck = new ConcurrentHashMap<Block, BlockState>();


	@Override
	public void init() {
		Bukkit.getPluginManager().registerEvents(this, HungerGames.getInstance());
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(HungerGames.getInstance(), this, 0, 20);
	}

	@Override
	public void beginGame(HungerGame game) {
	}

	@Override
	public boolean resetChanges(HungerGame game) {
		EquatableWeakReference<HungerGame> eMap = new EquatableWeakReference<HungerGame>(game);
		if(!changedBlocks.containsKey(new EquatableWeakReference<HungerGame>(game))) return true;
		for(Location l : changedBlocks.get(eMap).keySet()) {
		BlockState state = changedBlocks.get(eMap).get(l);
		if (state instanceof Chest) Logging.debug("Resetting chest");
		l.getBlock().setTypeId(state.getTypeId());
		l.getBlock().setData(state.getRawData());
		}
		changedBlocks.get(eMap).clear();
		return true;
	}

	private static HungerGame insideGame(Block block) {
		Location loc = block.getLocation();
		for (HungerGame game : GameManager.INSTANCE.getRawGames()) {
			if (game.getWorlds().size() <= 0 && game.getCuboids().size() <= 0) return null;
			if (game.getWorlds().contains(block.getWorld())) return game;
			for (Cuboid c : game.getCuboids()) {
				if (c.isLocationWithin(loc)) return game;
			}
		}
		return null;
	}

	public void run() {
	synchronized(toCheck) {
		for (Iterator<Block> it = toCheck.keySet().iterator(); it.hasNext();) {
			Block b = it.next();
			HungerGame game = insideGame(b);
			if (game != null) {
				addBlockState(game, b, toCheck.get(b));
			}
			it.remove();
		}
	}
	}

	private static void addToCheck(Block block, BlockState state) {
		synchronized(toCheck) {
			toCheck.put(block, state);
		}
	}

	private static synchronized void addBlockState(HungerGame game, Block block, BlockState state) {
		EquatableWeakReference<HungerGame> eMap = new EquatableWeakReference<HungerGame>(game);
		if (!changedBlocks.containsKey(eMap)) changedBlocks.put(eMap, new HashMap<Location, BlockState>());
		if (changedBlocks.get(eMap).containsKey(block.getLocation())) return; // Don't want to erase the original block
		changedBlocks.get(eMap).put(block.getLocation(), state);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Chest) {
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
		addToCheck(event.getBlock(), event.getBlock().getState());
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
