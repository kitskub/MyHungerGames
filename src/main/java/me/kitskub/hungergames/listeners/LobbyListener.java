package me.kitskub.hungergames.listeners;

import me.kitskub.hungergames.Files;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Logging;
import me.kitskub.hungergames.WorldNotFoundException;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.stats.PlayerStat;
import me.kitskub.hungergames.utils.EquatableWeakReference;
import me.kitskub.hungergames.utils.GeneralUtils;

import java.util.*;
import java.util.Map.Entry;
import me.kitskub.hungergames.games.User;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyListener implements Listener, Runnable {
	private static Map<Location, EquatableWeakReference<? extends Game>> joinSigns = new HashMap<Location, EquatableWeakReference<? extends Game>>();
	private static Map<Location, EquatableWeakReference<? extends Game>> gameSigns = new HashMap<Location, EquatableWeakReference<? extends Game>>();
	private static List<InfoWall> infoWalls = new ArrayList<InfoWall>();
	private static int currentCheckPeriod = 0;
	private static final int MAX_CHECK_PERIOD = 5;
	
	public LobbyListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.getInstance(), this, 0, 20);//Update once a second
	}
	
	public static void removeSign(Location loc) {
		joinSigns.remove(loc);
		gameSigns.remove(loc);
		BlockState b = loc.getBlock().getState();
		if (b instanceof Sign) {
			Sign sign = (Sign) b;
			sign.setLine(0, "");
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();	
		}
		for (Iterator<InfoWall> it = infoWalls.iterator(); it.hasNext();) {
			InfoWall w = it.next();
			if (w.signs.contains(loc)) {
				w.clearSigns();
				it.remove();
			}
		}
		save();
	}
	
	public static boolean addInfoWall(Location one, Location two, BlockFace clickedFace, String str) {
		if (one.getWorld() != two.getWorld()) return false;
		EquatableWeakReference<? extends Game> game = HungerGames.getInstance().getGameManager().getGame(str);
		if (game == null) return false;
		World w = one.getWorld();
		int oneX = one.getBlockX();
		int oneY = one.getBlockY();
		int oneZ = one.getBlockZ();
		int twoX = two.getBlockX();
		int twoY = two.getBlockY();
		int twoZ = two.getBlockZ();
		int xSize = Math.abs(twoX - oneX);
		int zSize = Math.abs(twoZ - oneZ);
		if (xSize > 0 && zSize > 0) return false; // Sorry can't be a 3d wall
		int startY = Math.max(oneY, twoY);
		int endY = Math.min(oneY, twoY);
		List<Location> locs = new ArrayList<Location>();
		if (xSize > 0) {
			switch (clickedFace) {
				// S to N, + to - X
				case NORTH:
				case EAST:
					for (int y = startY; y >= endY; y--) {
						for (int x = Math.max(oneX, twoX); x >= Math.min(oneX, twoX); x--) {
							Location loc = new Location(w, x, y, oneZ);
							if (loc.getBlock().getState() instanceof Sign) locs.add(loc);
						}
					}
					break;
				// N to S, - to + X
				case WEST:
				case SOUTH:
				default:
					for (int y = startY; y >= endY; y--) {
						for (int x = Math.min(oneX, twoX); x <= Math.max(oneX, twoX); x++) {
							Location loc = new Location(w, x, y, oneZ);
							if (loc.getBlock().getState() instanceof Sign) locs.add(loc);
						}
					}
					break;
			}
		}
		else if (zSize > 0) {
			switch (clickedFace) {
				// W to E, + to - Z
				case SOUTH:
				case EAST:
					for (int y = startY; y >= endY; y--) {
						for (int z = Math.max(oneZ, twoZ); z >= Math.min(oneZ, twoZ); z--) {
							Location loc = new Location(w, oneX, y, z);
							if (loc.getBlock().getState() instanceof Sign) locs.add(loc);
						}
					}
					break;
				// E to W, - to + Z
				case NORTH:
				case WEST:
				default:
					for (int y = startY; y >= endY; y--) {
						for (int z = Math.min(oneZ, twoZ); z <= Math.max(oneZ, twoZ); z++) {
							Location loc = new Location(w, oneX, y, z);
							if (loc.getBlock().getState() instanceof Sign) locs.add(loc);
						}
					}
					break;
			}
		}
		infoWalls.add(new InfoWall(game, locs));
		save();
		return true;
	}

	public static boolean addJoinSign(Location location, String name) {
		if (location == null) return false;
		Block block = location.getBlock();
		if (!(block.getState() instanceof Sign)) return false;
		EquatableWeakReference<? extends Game> game = HungerGames.getInstance().getGameManager().getGame(name);
		if (game == null) return false;
		joinSigns.put(location, game);
		Sign sign = (Sign) block.getState();
		sign.setLine(0, "[MyHungerGames]");
		sign.setLine(1, "Click the sign");
		sign.setLine(2, "to join");
		sign.setLine(3, name);
		sign.update(false);
		save();
		return true;
	}

	public static boolean addGameSign(Location location, String str) {
		if (location == null) return false;
		Block block = location.getBlock();
		if (!(block.getState() instanceof Sign)) return false;
		EquatableWeakReference<? extends Game> game = HungerGames.getInstance().getGameManager().getGame(str);
		if (game == null) return false;
		gameSigns.put(location, game);
		updateGameSigns();
		save();
		return true;
	}
	
	@EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
	public static void playerClickedBlock(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (!joinSigns.containsKey(event.getClickedBlock().getLocation())) return;
		EquatableWeakReference<? extends Game> game = joinSigns.get(event.getClickedBlock().getLocation());
		if (game == null || game.get() == null) {
			joinSigns.remove(event.getClickedBlock().getLocation());
			return;
		}
		game.get().join(event.getPlayer());
	}
	
	@EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
	public static void onBlockBreak(BlockBreakEvent event) {
		removeSign(event.getBlock().getLocation());		
	}

	public void run() {
		currentCheckPeriod++;
		if (currentCheckPeriod < MAX_CHECK_PERIOD) return;//Only MAX_CHECK_PERIOD seconds
		currentCheckPeriod = 0;
		for (Location l : joinSigns.keySet()) {
			EquatableWeakReference<? extends Game> game = joinSigns.get(l);
			if (game == null || game.get() == null) {
				joinSigns.remove(l);
			}
		}
		updateGameSigns();
		for (Iterator<InfoWall> it = infoWalls.iterator(); it.hasNext();) {
			InfoWall w = it.next();
			if (w.signs.isEmpty()) {
				it.remove();
				continue;
			}
			w.update();
		}
		save();
	}
	
	public static void save() {
		YamlConfiguration config = Files.LOBBY_SIGNS.getConfig();
		ConfigurationSection joinSection = config.createSection("join-signs");
		ConfigurationSection gameSection = config.createSection("game-signs");
		ConfigurationSection infoSection = config.createSection("info-walls");

		int count = 0;
		for (Iterator<Entry<Location, EquatableWeakReference<? extends Game>>> it = joinSigns.entrySet().iterator(); it.hasNext();) {
			Entry<Location, EquatableWeakReference<? extends Game>> entry = it.next();
			if (entry.getValue().get() == null) {
				it.remove();
				continue;
			}
			count++;
			ConfigurationSection section = joinSection.createSection(String.valueOf(count));
			section.set("location", GeneralUtils.parseToString(entry.getKey()));
			section.set("game", entry.getValue().get().getName());
		}
		count = 0;
		for (Iterator<Entry<Location, EquatableWeakReference<? extends Game>>> it = gameSigns.entrySet().iterator(); it.hasNext();) {
			Entry<Location, EquatableWeakReference<? extends Game>> entry = it.next();
			if (entry.getValue().get() == null) {
				it.remove();
				continue;
			}
			count++;
			ConfigurationSection section = gameSection.createSection(String.valueOf(count));
			section.set("location", GeneralUtils.parseToString(entry.getKey()));
			section.set("game", entry.getValue().get().getName());
		}
		count = 0;
		for (InfoWall w : infoWalls) {
			if (w.game.get() == null) continue;
			count++;
			ConfigurationSection section = infoSection.createSection(String.valueOf(count));
			section.set("game", w.game.get().getName());
			List<String> strings = new ArrayList<String>();
			for (Location l : w.signs) {
				strings.add(GeneralUtils.parseToString(l));
			}
			section.set("signs", strings);
		}
		Files.LOBBY_SIGNS.save();
	}

	public static void load() {
		YamlConfiguration config = Files.LOBBY_SIGNS.getConfig();
		ConfigurationSection joinSection = config.getConfigurationSection("join-signs");
		ConfigurationSection gameSection = config.getConfigurationSection("game-signs");
		ConfigurationSection infoSection = config.getConfigurationSection("info-walls");
		
		if (joinSection != null) {
			joinSigns.clear();
			for (String key : joinSection.getKeys(false)) {
				ConfigurationSection section = joinSection.getConfigurationSection(key);
				Location loc;
				try {
					loc = GeneralUtils.parseToLoc(section.getString("location", ""));
				} catch (NumberFormatException ex) {
					Logging.debug(ex.getMessage());
					continue;
				} catch (WorldNotFoundException ex) {
					Logging.warning(ex.getMessage());
					continue;
				} catch (IllegalArgumentException ex) {
					continue;
				}
				EquatableWeakReference<? extends Game> game = HungerGames.getInstance().getGameManager().getGame(section.getString("game", ""));
				if (game == null) continue;
				joinSigns.put(loc, game);
			}
		}
		if (gameSection != null) {
			gameSigns.clear();
			for (String key : gameSection.getKeys(false)) {
				ConfigurationSection section = gameSection.getConfigurationSection(key);
				Location loc;
				try {
					loc = GeneralUtils.parseToLoc(section.getString("location", ""));
				} catch (NumberFormatException ex) {
					Logging.debug(ex.getMessage());
					continue;
				} catch (WorldNotFoundException ex) {
					Logging.warning(ex.getMessage());
					continue;
				} catch (IllegalArgumentException ex) {
					continue;
				}
				EquatableWeakReference<? extends Game> game = HungerGames.getInstance().getGameManager().getGame(section.getString("game", ""));
				if (loc == null || game == null) continue;
				gameSigns.put(loc, game);
			}
		}
		if (infoSection != null) {
			infoWalls.clear();
			for (String key : infoSection.getKeys(false)) {
				ConfigurationSection section = infoSection.getConfigurationSection(key);
				EquatableWeakReference<? extends Game> game = HungerGames.getInstance().getGameManager().getGame(section.getString("game", ""));
				List<String> strings = section.getStringList("signs");
				List<Location> locs = new ArrayList<Location>();
				for (String s : strings) {
					Location loc;
					try {
						loc = GeneralUtils.parseToLoc(s);
					} catch (NumberFormatException ex) {
						Logging.debug(ex.getMessage());
						continue;
					} catch (WorldNotFoundException ex) {
						Logging.warning(ex.getMessage());
						continue;
					} catch (IllegalArgumentException ex) {
						continue;
					}
					locs.add(loc);
				}
				InfoWall w = new InfoWall(game, locs);
				infoWalls.add(w);
			}
		}
	}

	public static void updateGameSigns() {
		for (Iterator<Location> it = gameSigns.keySet().iterator(); it.hasNext();) {
			Location l = it.next();
			EquatableWeakReference<? extends Game> gameRef = gameSigns.get(l);
			if (gameRef == null || gameRef.get() == null) {
				it.remove();
				continue;
			}
			Game game = gameRef.get();
			BlockState b = l.getBlock().getState();
			if (!(b instanceof Sign)) {
				it.remove();
				continue;
			}

			Sign sign = (Sign) b;
			sign.setLine(0, (game.getState() != HungerGame.GameState.DISABLED ? ChatColor.GREEN : ChatColor.RED) + game.getName());

			if (game.getState() == HungerGame.GameState.DISABLED) {
				sign.setLine(1, ChatColor.RED + "Disabled");
				sign.setLine(2, "");
				sign.setLine(3, "");
			}
			else if (game.getState() == HungerGame.GameState.STOPPED) {
				sign.setLine(1, "Stopped");
				sign.setLine(2, "Ready:" + game.getRemainingPlayers().size());
				sign.setLine(3, "Available:" + (game.getSize() - game.getRemainingPlayers().size()));
			}
			else if (game.getState() == HungerGame.GameState.RUNNING || game.getState() == HungerGame.GameState.COUNTING_FOR_START) {
				sign.setLine(1, "Running");
				sign.setLine(2, "Remaining:" + game.getRemainingPlayers().size());
			}
			sign.update();
		}
	}
	
	public static class Ranker implements Comparator<User> {

		public int compare(User o1, User o2) {
			if (o1 == null) return o2 == null ? 0 : -1;
			return o1.getStat(o1.getGameInEntry().getGame()).compareTo(o2 == null ? null : o2.getStat(o1.getGameInEntry().getGame()));
		}
		
	}


	private static final class InfoWall {
		private final List<Location> signs;
		private final EquatableWeakReference<? extends Game> game;

		public InfoWall(EquatableWeakReference<? extends Game> game, List<Location> list) {
			this.signs = list;
			this.game = game;
			update();
		}
		
		private List<Sign> getSignsWithVerification() {
			List<Sign> signList = new ArrayList<Sign>();
			for (Iterator<Location> it = signs.iterator(); it.hasNext();) {
				Location l = it.next();
				if (!(l.getBlock().getState() instanceof Sign)) {
					it.remove();
					continue;
				}
				signList.add((Sign) l.getBlock().getState());
			}
			return signList;
		}
		
		public void update() {
			if (game.get() == null) return;
			List<Sign> signList = getSignsWithVerification();
			TreeSet<User> stats = new TreeSet<User>(new Ranker());
			stats.addAll(game.get().getUsers());
			Iterator<User> statIt = stats.iterator();
			Iterator<Sign> signIt = signList.iterator();
			while(signIt.hasNext()) {
				Sign nextSign = signIt.next();
				if (statIt.hasNext()) {
					User nextStat = statIt.next();
					nextSign.setLine(0, nextStat.getPlayer().getName());
					nextSign.setLine(1, "Kills:" + nextStat.getStat(game.get()).getKills().size());
					nextSign.setLine(2, "Deaths:" + nextStat.getStat(game.get()).getDesths().size());
					nextSign.setLine(3, "Lives:" + nextStat.getStat(game.get()).getLivesLeft());
				}
				else {
					nextSign.setLine(0, "");
					nextSign.setLine(1, "");
					nextSign.setLine(2, "");
					nextSign.setLine(3, "");
				}
				nextSign.update();
			}
		}

			
		public void clearSigns() {
			for (Iterator<Location> it = signs.iterator(); it.hasNext();) {
				Location l = it.next();
				BlockState b = l.getBlock().getState();
				if (!(b instanceof Sign)) {
					it.remove();
					continue;
				}
				Sign sign = (Sign) b;
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();
			}
		}
	
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final InfoWall other = (InfoWall) obj;
			if (this.signs != other.signs && (this.signs == null || !this.signs.equals(other.signs))) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 61 * hash + (this.signs != null ? this.signs.hashCode() : 0);
			return hash;
		}	
	}
}
