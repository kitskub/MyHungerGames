package com.randude14.hungergames.listeners;

import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SessionListener implements Listener {
	private static final Map<String, Session> chestAdders = new HashMap<String, Session>();
	private static final Map<String, Session> chestRemovers = new HashMap<String, Session>();
	private static final Map<String, Session> spawnAdders = new HashMap<String, Session>();
	private static final Map<String, Session> spawnRemovers = new HashMap<String, Session>();
	private static final Map<String, Session> cuboidAdders = new HashMap<String, Session>();
	
	@EventHandler
	public void playerClickedBlock(PlayerInteractEvent event) {
	    if (event.isCancelled()) return;
	    Player player = event.getPlayer();
	    Action action = event.getAction();
	    if (!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) return;
	    if (chestAdders.containsKey(player.getName())) {
		    Session session = chestAdders.get(player.getName());
		    HungerGame game = session.getGame();
		    if (game == null) {
			    ChatUtils.error(player,"%s has been removed recently due to unknown reasons.");
			    return;
		    }
		    Block block = event.getClickedBlock();
		    if (action == Action.LEFT_CLICK_BLOCK) {
			    if (!(block.getState() instanceof Chest)) {
				    ChatUtils.error(player, "Block is not a chest.");
				    return;
			    }
		    if (game.addChest(block.getLocation())) {
			    ChatUtils.send(player, "Chest has been added to %s.", game.getName());
		    }
		    else {
			    ChatUtils.error(player, "Chest has already been added to game %s.",game.getName());
		    }
		    session.clicked(event.getClickedBlock());
		}
		else {
			ChatUtils.send(player, "You have added %d chests to the game %s.", session.getBlocks().size(), game.getName());
			chestAdders.remove(player.getName());
		}
	    }

	    else if (chestRemovers.containsKey(player.getName())) {
		Session session = chestRemovers.get(player.getName());
		HungerGame game = session.getGame();
		if (game == null) {
			ChatUtils.error(player, "%s has been removed recently due to unknown reasons.");
			return;
		}
		Block block = event.getClickedBlock();
		if (action == Action.LEFT_CLICK_BLOCK) {
		    if (!(block.getState() instanceof Chest)) {
			    ChatUtils.error(player, "Block is not a chest.");
			    return;
		    }
		    if (game.removeChest(block.getLocation())) {
			ChatUtils.send(player, "Chest has been removed from %s.", game.getName());
		    }
		    else {
			ChatUtils.error(player, "%s does not contain this chest.", game.getName());
		    }
		    session.clicked(event.getClickedBlock());
		}
		else {
		    ChatUtils.send(player, "You have removed %d chests from the game %s.", session.getBlocks().size(), game.getName());
		    chestRemovers.remove(player.getName());
		}
	    }

	    else if (spawnAdders.containsKey(player.getName())) {
		    Session session = spawnAdders.get(player.getName());
		    HungerGame game = session.getGame();
		    if (game == null) {
			    ChatUtils.error(player, "%s has been removed recently due to unknown reasons.");
			    return;
		    }
		    Location loc = event.getClickedBlock().getLocation();
		    loc.add(.5, 1, .5);
		    if (action == Action.LEFT_CLICK_BLOCK) {
			    if (game.addSpawnPoint(loc)) {
				    ChatUtils.send(player, "Spawn point has been added to %s.", game.getName());
			    }
			    else {
				    ChatUtils.error(player, "%s already has this spawn point.", game.getName());
			    }
			    session.clicked(event.getClickedBlock());
		    }
		    else {
			    ChatUtils.send(player, "You have added %d spawn points to the game %s.", session.getBlocks().size(), game.getName());
			    spawnAdders.remove(player.getName());
		    }
	    }
	    else if (spawnRemovers.containsKey(player.getName())) {
		    Session session = spawnRemovers.get(player.getName());
		    HungerGame game = session.getGame();
		    if (game == null) {
			    ChatUtils.error(player, "%s has been removed recently due to unknown reasons.");
			    return;
		    }
		    Location loc = event.getClickedBlock().getLocation();
		    loc.add(.5, 1, .5);
		    if (action == Action.LEFT_CLICK_BLOCK) {
			    if (game.removeSpawnPoint(loc)) {
				    ChatUtils.send(player, "Spawn point has been removed from %s.", game.getName());
			    }
			    else {
				    ChatUtils.error(player, "%s does not contain this spawn point.", game.getName());
			    }
			    session.clicked(event.getClickedBlock());
		    }
		    else {
			    ChatUtils.send(player, "You have removed %d spawn points from the game %s.", session.getBlocks().size(), game.getName());
			    spawnRemovers.remove(player.getName());
		    }
	    }
	    else if (cuboidAdders.containsKey(player.getName())) {
		    Session session = cuboidAdders.get(player.getName());
		    HungerGame game = session.getGame();
		    if (game == null) {
			    ChatUtils.error(player, "%s has been removed recently due to unknown reasons.");
			    return;
		    }
		    if (session.getBlocks().size() < 1) {
			    session.clicked(event.getClickedBlock());
			    ChatUtils.send(player, "First corner set.");
		    }
		    else {
			    game.addCuboid(session.getBlocks().get(0).getLocation(), event.getClickedBlock().getLocation());
			    cuboidAdders.remove(player.getName());
			    ChatUtils.send(player, "Second corner and cuboid set.");
		    }
	    }
	}
	
	public static void addChestAdder(Player player, String name) {
		chestAdders.put(player.getName(),  new Session(name));
	}

	public static void addChestRemover(Player player, String name) {
		chestRemovers.put(player.getName(), new Session(name));
	}

	public static void addSpawnAdder(Player player, String name) {
		spawnAdders.put(player.getName(), new Session(name));
	}

	public static void addSpawnRemover(Player player, String name) {
		spawnRemovers.put(player.getName(), new Session(name));
	}

	public static void addCuboidAdder(Player player, String name) {
		cuboidAdders.put(player.getName(), new Session(name));
	}
	
	public static void removePlayer(Player player) {
		spawnAdders.remove(player.getName());
		spawnRemovers.remove(player.getName());
		chestAdders.remove(player.getName());
		chestRemovers.remove(player.getName());
		cuboidAdders.remove(player.getName());
	}
	
	private static class Session {
		private List<Block> blocks;
		private String game;

		public Session(String game) {
			this.game = game;
			this.blocks = new ArrayList<Block>();
		}

		public HungerGame getGame() {
			return GameManager.getGame(game);
		}

		public void clicked(Block block) {
			blocks.add(block);
		}

		public List<Block> getBlocks() {
			return blocks;
		}

	}
}
