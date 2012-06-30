package com.randude14.hungergames.reset;

import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.Cuboid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

/**
 *
 *
 */
public class InternalResetter extends Resetter implements Listener, Runnable{
    private static final Map<HungerGame, Map<Block, BlockState>> changedBlocks = new HashMap<HungerGame, Map<Block, BlockState>>();
    private static final Map<Block, BlockState> toCheck = Collections.synchronizedMap(new HashMap<Block, BlockState>());

    
    @Override
    public void init() {
	    Bukkit.getPluginManager().registerEvents(this, HungerGames.getInstance());
	    HungerGames.scheduleTask(this, 0, 5);
    }
    
    @Override
    public void beginGame(HungerGame game) {
    }
    
    @Override
    public boolean resetChanges(HungerGame game) {
	if(!changedBlocks.containsKey(game)) return true;
	for(Block b : changedBlocks.get(game).keySet()) {
		BlockState state = changedBlocks.get(game).get(b);
		b.setTypeId(state.getTypeId());
		b.setData(state.getRawData());
	}
	changedBlocks.get(game).clear();
	return true;
    }
    
    private static HungerGame insideGame(Block block) {
	    Location loc = block.getLocation();
	    for (HungerGame game : GameManager.getGames()) {
		    if (game.getWorlds().contains(block.getWorld())) return game;
	    }
	    for (HungerGame game : GameManager.getGames()) {
		    for (Cuboid c : game.getCuboids()) {
			    if (c.isLocationWithin(loc)) return game;
		    }
	    }
	    return null;
    }
    
    public void run() {
	synchronized(toCheck) {
		for (Block b : toCheck.keySet()) {
			HungerGame game = insideGame(b);
			if (game != null) {
				addBlockState(game, b, toCheck.get(b));
				toCheck.remove(b);
			}
		    }
	}
    }

    private static synchronized void addToCheck(Block block, BlockState state) {
	    toCheck.put(block, state);
    }
    
    private static synchronized void addBlockState(HungerGame game, Block block, BlockState state) {
	    if (!changedBlocks.containsKey(game)) changedBlocks.put(game, new HashMap<Block, BlockState>());
	    if (changedBlocks.get(game).containsKey(block)) return; // Don't want to erase the original block
	    changedBlocks.get(game).put(block, state);
    }
        
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
	    for (Block b : event.blockList()) {
		    addToCheck(b, b.getState());
	    }
    }
        
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
	    HungerGame session = GameManager.getPlayingSession(event.getPlayer());
	    if(session == null) return;
	    addBlockState(session, event.getBlock(), event.getBlockReplacedState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
	    HungerGame session = GameManager.getPlayingSession(event.getPlayer());
	    if(session == null) return;
	    addBlockState(session, event.getBlock(), event.getBlock().getState());
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
