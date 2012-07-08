package com.randude14.hungergames.listeners;

import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.api.event.GameCreateEvent;
import com.randude14.hungergames.api.event.GameEndEvent;
import com.randude14.hungergames.api.event.GameLoadEvent;
import com.randude14.hungergames.api.event.GamePauseEvent;
import com.randude14.hungergames.api.event.GameRemoveEvent;
import com.randude14.hungergames.api.event.GameStartEvent;
import com.randude14.hungergames.api.event.GameStopEvent;
import com.randude14.hungergames.api.event.PlayerJoinGameEvent;
import com.randude14.hungergames.api.event.PlayerKickGameEvent;
import com.randude14.hungergames.api.event.PlayerKillEvent;
import com.randude14.hungergames.api.event.PlayerLeaveGameEvent;
import com.randude14.hungergames.api.event.PlayerQuitGameEvent;
import com.randude14.hungergames.games.HungerGame;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class InternalListener implements Runnable, Listener{

	private static final Map<ListenerType, Map<HungerGame, List<Location>>> listeners = Collections.synchronizedMap(new EnumMap<ListenerType, Map<HungerGame, List<Location>>>(ListenerType.class));
	private static List<OffQueue> queues = new ArrayList<OffQueue>();
	private static int taskId = 0;
	
	public InternalListener() {
		taskId = HungerGames.scheduleTask(this, 0, 1);
	}
	
	private class SignData {
		private Location loc;
		private Material type;
		private byte data;
		private String[] lines;

		public SignData(Location loc, Material type, byte data, String[] lines) {
			this.loc = loc;
			this.type = type;
			this.data = data;
			this.lines = lines;
		}
	}
	
	/**
	 * Add a sign. Does not check
	 * @param type 
	 * @param game 
	 * @param sign
	 * @return  
	 */
	public static boolean addSign(ListenerType type, HungerGame game, Sign sign) {
		Map<HungerGame, List<Location>> gameMap = listeners.get(type);
		if (gameMap == null) {
			listeners.put(type, new HashMap<HungerGame, List<Location>>());
			gameMap = listeners.get(type);
		}
		List<Location> locs = gameMap.get(game);
		if (locs == null) {
			gameMap.put(game, new ArrayList<Location>());
			locs = gameMap.get(game);
		}
		locs.add(sign.getLocation());
		return true;
	}

	public void run() {
		List<OffQueue> toRemove = new ArrayList<OffQueue>();
		for (OffQueue queue : queues) {
			if (queue.removeTick()) {
				for (SignData sign : queue.signs) {
					Block b = sign.loc.getBlock();
					b.setType(Material.AIR);
					b.setType(sign.type);
					b.setTypeIdAndData(sign.type.getId(), sign.data, true);
					if (sign.lines != null) {
						if (b.getState() instanceof Sign) {
							Sign signBlock = (Sign) b.getState();
							for (int i = 0; i < sign.lines.length; i++) {
								signBlock.setLine(i, sign.lines[i]);
							}
							signBlock.update();
						}
					}
				}
				toRemove.add(queue);
			}
		}
		queues.removeAll(toRemove);
	}
	
	private void callListeners(ListenerType type, HungerGame game) {
		Logging.debug("Calling listener: " + type.name());
		Map<HungerGame, List<Location>> gameMap = listeners.get(type);
		if (gameMap == null) {
			listeners.put(type, new HashMap<HungerGame, List<Location>>());
			gameMap = listeners.get(type);
		}
		List<Location> locs = gameMap.get(game);
		if (locs == null) {
			gameMap.put(game, new ArrayList<Location>());
			locs = gameMap.get(game);
		}
		List<Location> toRemove = new ArrayList<Location>();
		List<SignData> signs = new ArrayList<SignData>();
		for (Location loc : locs) {
			String[] lines = null;
			Block block = loc.getBlock();
			if (block.getState() instanceof Sign) {
				Sign sign = (Sign) block.getState();
				lines = sign.getLines();
			}
			if (loc.getBlock().getType() == Material.SIGN_POST) {
				loc.getBlock().setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) 0x5,true);
				signs.add(new SignData(loc, Material.SIGN_POST, (byte) 0x1, lines));
			}
			else if (loc.getBlock().getType() == Material.WALL_SIGN) {
				byte data = loc.getBlock().getData(); // Correspond to the direction of the wall sign
				if (data == 0x2) { // South
					loc.getBlock().setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x4, true);
					signs.add(new SignData(loc, Material.WALL_SIGN, (byte) 0x2, lines));
				}
				else if (data == 0x3) { // North
					loc.getBlock().setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x3, true);
					signs.add(new SignData(loc, Material.WALL_SIGN, (byte) 0x3, lines));
				}
				else if (data == 0x4) { // East
					loc.getBlock().setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x2, true);
					signs.add(new SignData(loc, Material.WALL_SIGN, (byte) 0x4, lines));
				}
				else if (data == 0x5) { // West
					loc.getBlock().setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x1, true);
					signs.add(new SignData(loc, Material.WALL_SIGN, (byte) 0x5, lines));
				}
			}
			else {
				System.out.println("Location is no longer a sign");
				toRemove.add(loc);
				continue;
			}
		}
		locs.removeAll(toRemove);
		queues.add(new OffQueue(signs));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameCreate(GameCreateEvent event) {
		callListeners(ListenerType.GAME_CREATE, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		callListeners(ListenerType.GAME_END, event.getGame());
	} 
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameLoad(GameLoadEvent event) {
		callListeners(ListenerType.GAME_LOAD, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamePause(GamePauseEvent event) {
		callListeners(ListenerType.GAME_PAUSE, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameRemove(GameRemoveEvent event) {
		callListeners(ListenerType.GAME_REMOVE, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		callListeners(ListenerType.GAME_START, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStop(GameStopEvent event) {
		callListeners(ListenerType.GAME_STOP, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinGameEvent event) {
		callListeners(ListenerType.PLAYER_JOIN, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickGameEvent event) {
		callListeners(ListenerType.PLAYER_KICK, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKillEvent event) {
		callListeners(ListenerType.PLAYER_KILL, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		callListeners(ListenerType.PLAYER_LEAVE, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitGameEvent event) {
		callListeners(ListenerType.PLAYER_QUIT, event.getGame());
	}
	
	public enum ListenerType {
		GAME_CREATE("gamecreate"),
		GAME_END("gameend"),
		GAME_LOAD("gameload"),
		GAME_PAUSE("gamepause"),
		GAME_REMOVE("gameremove"),
		GAME_START("gamestart"),
		GAME_STOP("gamestop"),
		PLAYER_JOIN("playerjoin"),
		PLAYER_KICK("playerkick"),
		PLAYER_KILL("playerkill"),
		PLAYER_LEAVE("playerleave"),
		PLAYER_QUIT("playerquit");
		
		private String id;
		private static final Map<String, ListenerType> map = new HashMap<String, ListenerType>();
		
		private ListenerType(String id) {
			this.id = id;
		}
		
		public static ListenerType byId(String string) {
			if (string == null) return null;
			return map.get(string);
		}
		
		static {
			for (ListenerType value : values()) {
				map.put(value.id, value);
			}
		}
	}
	
	private class OffQueue {
		private int ticksLeft;
		private List<SignData> signs = new ArrayList<SignData>();

		public OffQueue(List<SignData> signs) {
			this.signs.addAll(signs);
			ticksLeft = 20;
		}
		
		public boolean removeTick() {
			ticksLeft--;
			return ticksLeft <= 0;
		}
		
	}

}
