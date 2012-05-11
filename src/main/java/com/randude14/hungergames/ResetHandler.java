package com.randude14.hungergames;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
/**
 *
 *
 */
public class ResetHandler implements Listener{
    private static Map<HungerGame, Map<Block, BlockState>> changedBlocks = new HashMap<HungerGame, Map<Block, BlockState>>();
    
    private static void addBlockState(HungerGame game, Block block, BlockState state) {
	if(!changedBlocks.containsKey(game)) changedBlocks.put(game, new HashMap<Block, BlockState>());
	changedBlocks.get(game).put(block, state);
    }
    
    public static boolean reloadWorld(String world) {
	// Unload
	Plugin.getInstance().getServer().unloadWorld(world, true); // FIXME no safety for players
	File serverWorlds = Plugin.getInstance().getServer().getWorldContainer();
	File templateLoc = Plugin.getInstance().getDataFolder();
	File worldFolder = new File(serverWorlds, world);
	File template = new File(templateLoc, world + "_template");
	if(!template.exists()) {
	    Logging.log(Level.WARNING, "There is no template world, so cancelling delete and reload.");
	    return false;
	}
	// Delete
	if (!FileUtils.deleteFolder(worldFolder) || worldFolder.exists()) {
	    // Couldn't delete it???
	    Logging.log(Level.SEVERE, "Couldn't delete a world!");
	    return false; // failed...
	}

	// Copy 
	if (!FileUtils.copyFolder(template, worldFolder)) {
	    // Dang
	    Logging.log(Level.SEVERE, "Couldn't copy a world!");
	    return false; // failed...
	}
	return true;
    }
    
    public static boolean resetBlockChanges(HungerGame game) {
	if(!changedBlocks.containsKey(game)) return true;
	for(Block b : changedBlocks.get(game).keySet()) {
	    BlockState state = changedBlocks.get(game).get(b);
	    b.setTypeId(state.getTypeId());
	    b.setData(state.getRawData());
	}
	changedBlocks.get(game).clear();
	return true;
    }
    
    public static boolean resetChanges(HungerGame game) {
	if(Config.getReloadWorld(game.getSetup())) {
	    return reloadWorld(Config.getReloadWorldName(game.getSetup()));
	}
	else if(Config.getResetChanges(game.getSetup())) {
	    return resetBlockChanges(game);
	}
	return true;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
	Player player = event.getPlayer();
	HungerGame session = GameManager.getSession(player);
	if(session == null) return;
	String setup = session.getSetup();
	if(Config.getResetChanges(setup)){
	    BlockState blockReplacedState = event.getBlockReplacedState();
	    addBlockState(session, event.getBlock(), blockReplacedState);
	}
	    
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
	Player player = event.getPlayer();
	HungerGame session = GameManager.getSession(player);
	if(session == null) return;
	String setup = session.getSetup();
	if(Config.getResetChanges(setup)){
	    BlockState blockReplacedState = event.getBlock().getState();
	    addBlockState(session, event.getBlock(), blockReplacedState);
	}
    }
	
}
