package com.randude14.hungergames.listeners;

import com.randude14.hungergames.*;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.api.Game;
import com.randude14.hungergames.api.event.GameEndEvent;
import com.randude14.hungergames.api.event.GamePauseEvent;
import com.randude14.hungergames.api.event.GameStartEvent;
import com.randude14.hungergames.api.event.PlayerJoinGameEvent;
import com.randude14.hungergames.api.event.PlayerKillEvent;
import com.randude14.hungergames.api.event.PlayerLeaveGameEvent;
import com.randude14.hungergames.utils.GeneralUtils;

import java.util.*;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class SignListener implements Runnable, Listener {

	private static final Map<ListenerType, Map<String, List<Location>>> listeners = Collections.synchronizedMap(new EnumMap<ListenerType, Map<String, List<Location>>>(ListenerType.class));
	private static final Map<ListenerType, List<Location>> allGameListeners = Collections.synchronizedMap(new EnumMap<ListenerType, List<Location>>(ListenerType.class));
	private static List<OffQueue> queues = new ArrayList<OffQueue>();
	private static BukkitTask task;
	
	public SignListener() {
		task = Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), this, 0, 1);
		loadSigns();
	}
	
	public static void loadSigns() {
		YamlConfiguration config = Files.SIGNS.getConfig();
		for (ListenerType type : ListenerType.values()) {
			ConfigurationSection section = config.getConfigurationSection(type.name());
			if (!allGameListeners.containsKey(type)) allGameListeners.put(type, new ArrayList<Location>());
			if (!listeners.containsKey(type)) listeners.put(type, new HashMap<String, List<Location>>());
			if (section == null) continue;

			List<String> sList = section.getStringList("gameLocs");
			if (sList != null) {
				for (String s : sList) {
					try {
						allGameListeners.get(type).add(GeneralUtils.parseToLoc(s));
					} catch (NumberFormatException ex) {
						Logging.debug(ex.getMessage());
						continue;
					} catch (WorldNotFoundException ex) {
						Logging.warning(ex.getMessage());
						continue;
					}
				}
			}
			if (section == null) continue;
			for (String game : section.getKeys(false)) {
				if (!listeners.get(type).containsKey(game)) listeners.get(type).put(game, new ArrayList<Location>());
				ConfigurationSection gameSection = section.getConfigurationSection(game);
				if (gameSection == null) continue;
				List<Location> list = new ArrayList<Location>();
				List<String> stringList = gameSection.getStringList("gameLocs");
				for (String s : stringList) {
					try {
						list.add(GeneralUtils.parseToLoc(s));
					} catch (NumberFormatException ex) {
						Logging.debug(ex.getMessage());
						continue;
					} catch (WorldNotFoundException ex) {
						Logging.warning(ex.getMessage());
						continue;
					}
				}
				listeners.get(type).get(game).addAll(list);
			}
		}
	}
	
	public static void saveSigns() {
		YamlConfiguration config = Files.SIGNS.getConfig();
		for (ListenerType type : ListenerType.values()) {
			ConfigurationSection section = config.createSection(type.name());
			List<String> allGamesList = new ArrayList<String>();
			for (Location l : allGameListeners.get(type)) {
				allGamesList.add(GeneralUtils.parseToString(l));
			}
			section.set("allGames", allGamesList);
			for (String game : listeners.get(type).keySet()) {
				ConfigurationSection gameSection = section.createSection(game);
				List<Location> list = listeners.get(type).get(game);
				List<String> stringList = new ArrayList<String>();
				for (Location l : list) {
					stringList.add(GeneralUtils.parseToString(l));
				}
				gameSection.set("gameLocs", stringList);
			}
		}
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
	public static boolean addSign(ListenerType type, Game game, Sign sign) {
		if (game != null) {
			Map<String, List<Location>> gameMap = listeners.get(type);
			if (gameMap == null) {
				listeners.put(type, new HashMap<String, List<Location>>());
				gameMap = listeners.get(type);
			}
			List<Location> locs = gameMap.get(game.getName());
			if (locs == null) {
				gameMap.put(game.getName(), new ArrayList<Location>());
				locs = gameMap.get(game.getName());
			}
			locs.add(sign.getLocation());
			return true;
		}
		else {
			List<Location> locs = allGameListeners.get(type);
			if (locs == null) {
				allGameListeners.put(type, new ArrayList<Location>());
				locs = allGameListeners.get(type);
			}
			locs.add(sign.getLocation());
			return true;
		}
	}

	public void run() {
		if (queues.size() <= 0) return;
		List<OffQueue> toRemove = new ArrayList<OffQueue>();
		for (OffQueue queue : queues) {
			if (queue.removeTick()) {
				// Logging.debug("Removing sign from queue");
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
	
	private void callListeners(ListenerType type, Game game) {
		Map<String, List<Location>> gameMap = listeners.get(type);
		if (gameMap == null) {
			listeners.put(type, new HashMap<String, List<Location>>());
			gameMap = listeners.get(type);
		}
		List<Location> locs = gameMap.get(game.getName());
		if (locs == null) {
			gameMap.put(game.getName(), new ArrayList<Location>());
			locs = gameMap.get(game.getName());
		}
		List<Location> toRemove = new ArrayList<Location>();
		List<SignData> signs = new ArrayList<SignData>();
		locs.addAll(allGameListeners.get(type));
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
				Logging.warning("Location is no longer a sign");
				toRemove.add(loc);
				continue;
			}
		}
		listeners.get(type).get(game.getName()).removeAll(toRemove);
		allGameListeners.get(type).removeAll(toRemove);
		queues.add(new OffQueue(signs));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		callListeners(ListenerType.GAME_END, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamePause(GamePauseEvent event) {
		callListeners(ListenerType.GAME_PAUSE, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		callListeners(ListenerType.GAME_START, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinGameEvent event) {
		callListeners(ListenerType.PLAYER_JOIN, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKillEvent event) {
		callListeners(ListenerType.PLAYER_KILL, event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		if (event.getType().equals(PlayerLeaveGameEvent.Type.LEAVE)) {
			callListeners(ListenerType.PLAYER_LEAVE, event.getGame());
		}
		else if (event.getType().equals(PlayerLeaveGameEvent.Type.QUIT)) {
			callListeners(ListenerType.PLAYER_QUIT, event.getGame());
		}
		else if (event.getType().equals(PlayerLeaveGameEvent.Type.KICK)) {
			callListeners(ListenerType.PLAYER_KICK, event.getGame());
		}
	}
	
	public enum ListenerType {
		GAME_END("gameend", Defaults.Perm.ADMIN_CREATE_SIGN_GAMEEND),
		GAME_PAUSE("gamepause", Defaults.Perm.ADMIN_CREATE_SIGN_GAMEPAUSE),
		GAME_START("gamestart", Defaults.Perm.ADMIN_CREATE_SIGN_GAMESTART),
		PLAYER_JOIN("playerjoin", Defaults.Perm.ADMIN_CREATE_SIGN_PLAYERJOIN),
		PLAYER_KICK("playerkick", Defaults.Perm.ADMIN_CREATE_SIGN_PLAYERKICK),
		PLAYER_KILL("playerkill", Defaults.Perm.ADMIN_CREATE_SIGN_PLAYERKILL),
		PLAYER_LEAVE("playerleave", Defaults.Perm.ADMIN_CREATE_SIGN_PLAYERLEAVE),
		PLAYER_QUIT("playerquit", Defaults.Perm.ADMIN_CREATE_SIGN_PLAYERQUIT);
		
		private String id;
		private Perm perm;
		private static final Map<String, ListenerType> map = new HashMap<String, ListenerType>();
		
		private ListenerType(String id, Perm perm) {
			this.id = id;
			this.perm = perm;
		}
		
		public String getId() {
			return id;
		}
		
		public Perm getPerm() {
			return perm;
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
