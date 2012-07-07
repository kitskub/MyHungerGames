package com.randude14.hungergames.listeners;

import com.randude14.hungergames.GameManager;
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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class InternalListener implements Runnable, Listener{

	private static final Map<ListenerType, Map<HungerGame, List<Location>>> listeners = new EnumMap<ListenerType, Map<HungerGame, List<Location>>>(ListenerType.class);
	private static List<OffQueue> queues = new ArrayList<OffQueue>();
	private static int taskId = 0;
	
	public InternalListener() {
		taskId = HungerGames.scheduleTask(this, 0, 1);
	}
	
	/**
	 * Add a sign. Does not check first line.
	 * @param sign
	 * @return  
	 */
	public static boolean addSign(Sign sign) {
		String id = sign.getLine(1);
		ListenerType type = ListenerType.byId(id);
		if (type == null) return false;
		HungerGame game = GameManager.getGame(sign.getLine(2));
		if (game == null) {
			sign.setLine(1, "");
			sign.setLine(2, "BAD GAME NAME!");
			sign.setLine(3, "");
			return false;
		}
		Map<HungerGame, List<Location>> gameMap = listeners.get(type);
		if (gameMap == null) gameMap = listeners.put(type, new HashMap<HungerGame, List<Location>>());
		List<Location> locs = gameMap.get(game);
		if (locs == null) locs = gameMap.put(game, new ArrayList<Location>());
		locs.add(sign.getLocation());
		return true;
	}

	public void run() {
		for (OffQueue queue : queues) {
			if (queue.removeTick()) {
				for (Block b : queue.blocks.keySet()) {
					b.setType(Material.SIGN);
					Sign sign = (Sign) b;
					sign.setData(queue.blocks.get(b).getData());
					
				}
			}
		}
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
		List<Sign> signs = new ArrayList<Sign>();
		for (Location loc : locs) {
			if (!(loc.getBlock() instanceof Sign)) {
				locs.remove(loc);
				continue;
			}
			signs.add((Sign) loc.getBlock());
			loc.getBlock().setType(Material.REDSTONE_TORCH_ON);
		}
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
	
	class OffQueue {
		private int ticksLeft;
		private Map<Block, Sign> blocks = new HashMap<Block, Sign>();

		public OffQueue(List<Sign> signs) {
			for (Sign s : signs) {
				blocks.put(s.getBlock(), s);
			}
			ticksLeft = 20;
		}
		
		public boolean removeTick() {
			ticksLeft--;
			return ticksLeft <= 0;
		}
		
	}

}
