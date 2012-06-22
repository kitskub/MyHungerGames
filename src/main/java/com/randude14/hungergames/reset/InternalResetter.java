package com.randude14.hungergames.reset;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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
public class InternalResetter extends Resetter implements Listener{
    private static Map<HungerGame, Map<Block, BlockState>> changedBlocks = new HashMap<HungerGame, Map<Block, BlockState>>();
    
    private static void addBlockState(HungerGame game, Block block, BlockState state) {
	if(!changedBlocks.containsKey(game)) changedBlocks.put(game, new HashMap<Block, BlockState>());
	changedBlocks.get(game).put(block, state);
    }
    
    @Override
    public void init() {
	    Bukkit.getPluginManager().registerEvents(this, HungerGames.getInstance());
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
        
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
	Player player = event.getPlayer();
	HungerGame session = GameManager.getSession(player);
	if(session == null) return;
	BlockState blockReplacedState = event.getBlockReplacedState();
	addBlockState(session, event.getBlock(), blockReplacedState);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
	Player player = event.getPlayer();
	HungerGame session = GameManager.getSession(player);
	if(session == null) return;
	BlockState blockReplacedState = event.getBlock().getState();
	addBlockState(session, event.getBlock(), blockReplacedState);
    }
}
