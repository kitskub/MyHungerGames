package me.kitskub.hungergames.games;

import me.kitskub.hungergames.utils.config.ConfigSection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.stats.PlayerStat.Team;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.ConfigUtils;
import me.kitskub.hungergames.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaRegion {

    public abstract class Area {
        protected Cuboid cuboid;
        protected Location warp;
        
        public abstract String getPoint1();
        public abstract String getPoint2();
        private boolean isValid() {
            return cuboid != null && warp != null;
        }
    }

    public class LobbyArea extends Area {
        @Override
        public String getPoint1() {return RegionPoint.LOBBY1;}

        @Override
        public String getPoint2() {return RegionPoint.LOBBY2;}

        public String getWarp() {return RegionPoint.LOBBY_WARP;}
    }

    public class MainArea extends Area {
        @Override
        public String getPoint1() {return RegionPoint.MAIN1;}

        @Override
        public String getPoint2() {return RegionPoint.MAIN2;}
    }

    public class SpecArea extends Area {
        @Override
        public String getPoint1() {return RegionPoint.SPEC1;}

        @Override
        public String getPoint2() {return RegionPoint.SPEC2;}

        public String getWarp() {return RegionPoint.SPEC_WARP;}
    }
    
    /*
    public static class Lobby {
        private final Map<Location, me.kitskub.paintball.framework.Class> signs = new HashMap<Location, me.kitskub.paintball.framework.Class>();
    
        //private Location readyBlock;

        public Map<Location, Class> getSigns() {
            return Collections.unmodifiableMap(signs);
        }

        /*public Location getReadyBlock() {//TODO ask
            return readyBlock;
        }
        
        public void setReadyBlock(Location loc) {
            readyBlock = loc;
        }*
        
        public void addSign(Location loc, Class c) {
            signs.put(loc, c);
        }

        public void removeSign(Location loc) {
            signs.remove(loc);
        }
    }*/

    private LobbyArea lobbyArea;
    private MainArea main;
    private SpecArea spec;
    
    //private Location leaderboard;

    private final Arena arena;
    private final Map<Team, Location> spawnpoints;//, containers;
    private boolean mainOk, lobbyOk, specOk;
    private final ConfigSection coords;
    private final ConfigSection spawns;
    //private final ConfigSection chests;
    private final ConfigSection zones;
    private final ConfigSection lobbySec;
    //private final Lobby lobby;

    public ArenaRegion(Arena arena, ConfigSection coords) {
        this.arena = arena;
        this.coords = coords;
        this.lobbyArea = new LobbyArea();
        this.main = new MainArea();
        this.spec = new SpecArea();
        this.spawns = ConfigUtils.getOrCreateSection(coords, "spawnpoints");
        //this.chests = coords.getConfigSection("containers");
        this.zones = ConfigUtils.getOrCreateSection(coords, "zones");
        this.lobbySec = ConfigUtils.getOrCreateSection(coords, "lobby");
        this.spawnpoints = new HashMap<Team, Location>();
        //this.lobby = new Lobby();
        //containers = new HashMap<String, Location>();
    }

    public boolean save() {
        boolean bad = false;
        //lobbySec.set("readyblock", lobby.readyBlock);
        //Map<String, String> signs = new HashMap<String, String>();
        //for (Entry<Location, Class> e : lobby.signs.entrySet()) {
        //    signs.put(GeneralUtils.parseToString(e.getKey()), e.getValue().getName());
        //}
        //for (String s : signs.keySet()) lobbySec.getConfigSection("signs").set(s, signs.get(s));
        bad |= !coords.save();
        return !bad;
    }

    public boolean load() {
        boolean bad = false;
        bad |= !coords.reload();
        //lobby.readyBlock = lobbySec.getLocation("readyblock");
	/*
        for (String key : lobbySec.getConfigSection("signs").getKeys()) {
            try {
                Location loc = GeneralUtils.parseToLoc(key);
                Class c = Paintball.getClassManager().getClass(lobbySec.getConfigSection("signs").getString(key));
                if (c == null) {
                    Logging.warning("Cannot find class: " + lobbySec.getConfigSection("signs").getString(key));
                    continue;
                }
                lobby.signs.put(loc, c);
            } catch (WorldNotFoundException ex) {
                Logging.warning(ex.getMessage());
                continue;
            }
        }*/
        reloadRegions();
        reloadWarps();
        reloadLeaderboards();
        reloadSpawnpoints();
        reloadZones();
        //reloadChests();
        adjustArena();
        save();
        verifyData();
        return !bad;
    }

    public void reloadArea(Area a) {
        Location one = coords.getLocation(a.getPoint1());
		Location two = coords.getLocation(a.getPoint2());
        if (one != null && two != null) a.cuboid = new Cuboid(one, two);

    }

    public void reloadRegions() {
		reloadArea(main);
		reloadArea(lobbyArea);
		reloadArea(spec);
    }

    public void reloadWarps() {
        //main.warp = coords.getLocation(main.getWarp());
        lobbyArea.warp = coords.getLocation(RegionPoint.LOBBY_WARP);
        spec.warp = coords.getLocation(RegionPoint.SPEC_WARP);

        //leaderboard = coords.getLocation("leaderboard", world);
    }

    public void reloadLeaderboards() {
        //leaderboard = coords.getLocation("leaderboard", world);
    }

    public void reloadSpawnpoints() {
        spawnpoints.clear();
        Set<String> keys = spawns.getKeys();
        if (keys != null) {
	/*
            for (String teamName : keys) {
                Team team = Paintball.getTeamManager().get(teamName);
                if (TeamManager.NEUTRAL.equals(team)) {
                    Logging.warning("Team '" + teamName + "' does not exist. Using the neutral/default team!");
                }
                spawnpoints.put(team, spawns.getLocation(teamName));
            }
	*/
        }
    }

    public void reloadZones() {
    	/*zoneList.clear();
        Set<String> keys = zones.getKeys();
        if (keys != null) {
            for (String spwn : keys) {
                try {
                    Cuboid c = Cuboid.parseFromString(spwn);
                    if (c == null) throw new IllegalArgumentException("Malformed arenas.yml! " + spwn + " cannot be parsed as a cuboid!");
                    String teamName = zones.getConfigSection(spwn).getString("team");
                    Team team = Paintball.getTeamManager().get(teamName);
                    if (TeamManager.NEUTRAL.equals(team)) {
                        Logging.warning("Team '" + teamName + "' does not exist. Using the neutral team!");
                    }
                    Zone newZone = new ZoneImpl(c, team, arena);
                    zoneList.add(newZone);
                } catch (IllegalStateException ex) {
                    Logging.warning(ex.getMessage());
                }
            }
        }
	*/
    }

    public void reloadChests() {//TODO need?
        /*containers.clear();
        Set<String> keys = chests.getKeys();
        if (keys != null) {
            for (String chst : keys) {
                containers.put(chst, chests.getLocation(chst));
            }
        }*/
    }

    public void verifyData() {
        mainOk = main.cuboid != null;
        mainOk &= spawnpoints.size() >= 2;
        //mainOk &= zoneList.size() >= 1;
        lobbyOk = lobbyArea.cuboid != null && lobbyArea.warp != null;
        //lobbyOk &= lobby.readyBlock != null;
        //lobbyOk &= !lobby.signs.isEmpty();
        specOk = spec.cuboid != null && spec.warp != null;
    }

    public void checkData(CommandSender s) {//TODO checkdata command
        boolean nogood = false;
        if (lobbyArea.warp == null) {
            nogood = true;
            ChatUtils.send(s, "Missing warp: lobby");
        }
        if (spec.warp == null) {
            nogood = true;
            ChatUtils.send(s, "Missing warp: spectator");
        }
        if (main.cuboid == null) {
            nogood = true;
            ChatUtils.send(s, "Cuboid not set: arena");
        }
        if (lobbyArea.cuboid == null) {
            nogood = true;
            ChatUtils.send(s, "Cuboid not set: lobby");
        }
        if (spec.cuboid == null) {
            nogood = true;
            ChatUtils.send(s, "Cuboid not set: spectator");
        }
        /*if (lobby.readyBlock == null) {
            nogood = true;
            ChatUtils.send(s, "Readyblock not set");
        }*/
        /*if (lobby.signs.isEmpty()) {
            nogood = true;
            ChatUtils.send(s, "No class signs added");
        }*/
        if (spawnpoints.size() < 2) {
            nogood = true;
            ChatUtils.send(s, "Less than 2 team spawnpoints have been added");
        }
        /*if (zoneList.isEmpty()) {
            nogood = true;
            ChatUtils.send(s, "No zones added");
        }*/
        if (!nogood) {
            ChatUtils.send(s, "Arena is ready to be used!");
        }
    }

    public boolean isMainSetup() {
        return mainOk;
    }

    public boolean isLobbySetup() {
        return lobbyOk;
    }

    public boolean isSpecSetup() {
        return specOk;
    }

    public MainArea getMainArea() {
        return main;
    }

    public LobbyArea getLobbyArea() {
        return lobbyArea;
    }

    public SpecArea getSpecArea() {
        return spec;
    }

    public boolean isWarp(Location l) {
        return (l.equals(main.warp)
                || l.equals(lobbyArea.warp)
                || l.equals(spec.warp));
    }

    public boolean contains(Location l) {
        if (main.cuboid == null) {
            return false;
        }

        // Check the lobby first.
	/*
        if (lobbyArea.cuboid != null) {
            if (lobbyArea.cuboid.contains(l)) return true;
        }
        // Then spec.
        if (spec.cuboid != null) {
            if (spec.cuboid.contains(l)) return true;
        }
	*/

        // Returns false if the location is outside of the region.
        return main.cuboid.contains(l);
    }

    public boolean contains(Location l, int radius) {
        if (main.cuboid == null) {
            return false;
        }

        // Check the lobby first.
        if (lobbyArea.cuboid != null) {
            if (lobbyArea.cuboid.contains(l, radius)) return true;
        }
        // Then spec.
        if (spec.cuboid != null) {
            if (spec.cuboid.contains(l, radius)) return true;
        }

        // Returns false if the location is outside of the region.
        return main.cuboid.contains(l, radius);
    }

    // Region expand
    public void expandUp(Area area, int amount) {
        Location upper = area.cuboid.getUpperLocation();
        upper.setY(Math.min(area.cuboid.getWorld().getMaxHeight(), upper.getY() + amount));
        set(area.getPoint2(), upper);
    }

    public void expandDown(Area area, int amount) {
        Location lower = area.cuboid.getLowerLocation();
        lower.setY(Math.max(0D, lower.getY() - amount));
        set(area.getPoint1(), lower);
    }

    public void expandP1(Area area, int x, int z) {
        Location lower = area.cuboid.getLowerLocation();
        lower.setX(lower.getX() - x);
        lower.setZ(lower.getZ() - z);
        set(area.getPoint1(), lower);
    }

    public void expandP2(Area area, int x, int z) {
        Location upper = area.cuboid.getUpperLocation();
        upper.setX(upper.getX() + x);
        upper.setZ(upper.getZ() + z);
        set(area.getPoint2(), upper);
    }

    public void expandOut(Area area, int amount) {
        expandP1(area, amount, amount);
        expandP2(area, amount, amount);
    }

    public void fix(Area a) {
		String point1 = a.getPoint1();
		String point2 = a.getPoint2();
        Location loc1 = coords.getLocation(point1);
        Location loc2 = coords.getLocation(point2);

        if (loc1 == null || loc2 == null) {
            return;
        }

        if (loc1.getX() > loc2.getX()) {
            double tmp = loc1.getX();
            loc1.setX(loc2.getX());
            loc2.setX(tmp);
        }

        if (loc1.getZ() > loc2.getZ()) {
            double tmp = loc1.getZ();
            loc1.setZ(loc2.getZ());
            loc2.setZ(tmp);
        }

        if (loc1.getY() > loc2.getY()) {
            double tmp = loc1.getY();
            loc1.setY(loc2.getY());
            loc2.setY(tmp);
        }

        coords.set(point1, loc1);
        coords.set(point2, loc2);
        reloadArea(a);
    }

    private void adjustArena() {
        if (!mainOk) {
            return;
        }

        // Make sure the arena warp is inside the region.
        readjust(main, main.warp);

        // Re-adjust for all spawnpoints and containers.
        for (Location spawnpoint : spawnpoints.values()) {
            readjust(main, spawnpoint);
        }

        /*for (Location chest : containers.values()) {
            readjust(main, chest);
        }*/
    }

    private void readjust(Area area, Location l) {
        if (main.cuboid == null || l == null) {
            return;
        }

        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        int p1x = area.cuboid.getLower().getBlockX();
        int p1y = area.cuboid.getLower().getBlockY();
        int p1z = area.cuboid.getLower().getBlockZ();

        int p2x = area.cuboid.getUpper().getBlockX();
        int p2y = area.cuboid.getUpper().getBlockY();
        int p2z = area.cuboid.getUpper().getBlockZ();

        if (x <= p1x) {
            expandP1(area, p1x - x + 2, 0);
        } else if (x >= p2x) {
            expandP2(area, x - p2x + 2, 0);
        }

        if (y <= p1y) {
            expandDown(area, p1y - y + 2);
        } else if (y >= p2y) {
            expandUp(area, y - p2y + 2);
        }

        if (z <= p1z) {
            expandP1(area, 0, p1z - z + 2);
        } else if (z >= p2z) {
            expandP2(area, 0, z - p2z + 2);
        }
    }

    public List<Chunk> getMainChunks() {
        List<Chunk> result = new ArrayList<Chunk>();

        if (main.cuboid == null) {
            return result;
        }

        World world = main.cuboid.getWorld();
        Chunk c1 = world.getChunkAt(main.cuboid.getLowerLocation());
        Chunk c2 = world.getChunkAt(main.cuboid.getUpperLocation());

        for (int i = c1.getX(); i <= c2.getX(); i++) {
            for (int j = c1.getZ(); j <= c2.getZ(); j++) {
                result.add(world.getChunkAt(i, j));
            }
        }

        return result;
    }

    public Location getLobbyWarp() {
        return lobbyArea.warp;
    }

    public Location getSpecWarp() {
        return spec.warp;
    }

    public Location getSpawnpoint(Team name) {
        return spawnpoints.get(name);
    }

    public Map<Team, Location> getSpawnpoints() {
        return Collections.unmodifiableMap(spawnpoints);
    }

    public List<Location> getSpawnpointList() {
        return new ArrayList<Location>(spawnpoints.values());
    }
    
    /*public List<Zone> getZones() {
        return Collections.unmodifiableList(zoneList);
    }*/

    //public Collection<Location> getContainers() {
    //   return containers.values();
    //}

    //public Location getLeaderboard() {
    //    return leaderboard;
    //}

    public void setNoFix(String point, Location loc) {
        coords.set(point, loc);
    }

    public void set(String point, Location loc) {
        setNoFix(point, loc);
        // Adjust the region to accomodate any bounding box breaking.
        if (point.equals(RegionPoint.LOBBY_WARP)) {
            readjust(lobbyArea, loc);
        }

        if (point.equals(RegionPoint.SPEC_WARP)) {
            readjust(spec, loc);
        }
		if (point.equalsIgnoreCase(RegionPoint.MAIN1) || point.equalsIgnoreCase(RegionPoint.MAIN2)) fix(main);
		if (point.equalsIgnoreCase(RegionPoint.LOBBY1) || point.equalsIgnoreCase(RegionPoint.LOBBY2)) fix(lobbyArea);
		if (point.equalsIgnoreCase(RegionPoint.SPEC1) || point.equalsIgnoreCase(RegionPoint.SPEC2)) fix(spec);
        reloadWarps();
        reloadLeaderboards();
        verifyData();
        save();
    }

    public void addSpawn(String name, Location loc) {
        spawns.set(name, loc);
        readjust(main, loc);
        reloadSpawnpoints();
        verifyData();
        save();
    }

    public boolean removeSpawn(String name) {
        if (spawns.getString(name) == null) {
            return false;
        }

        spawns.set(name, null);
        reloadSpawnpoints();
        verifyData();
        save();
        return true;
    }

/*	public void addZone(Cuboid c, Team team) {
        ConfigSection section = zones.createSection(c.parseToString());
        section.set("team", team.getName());
        readjust(main, c.getUpper());
        readjust(main, c.getLower());
        reloadZones();
        verifyData();
        save();
    }
*/

    /*public void addChest(String name, Location loc) {
        chests.set(name, loc);
        reloadChests();
        save();
    }

    public boolean removeChest(String name) {
        if (chests.getString(name) == null) {
            return false;
        }

        chests.set(name, null);
        reloadChests();
        save();
        return true;
    }*/

    public void showRegion(final Player p) {
        if (main.cuboid == null) {
            return;
        }

        // Grab all the blocks, and send block change events.
        final Map<Location, BlockState> blocks = new HashMap<Location, BlockState>();
        for (Location l : getFramePoints()) {
            Block b = l.getBlock();
            blocks.put(l, b.getState());
            p.sendBlockChange(l, 35, (byte) 14);
        }

        Bukkit.getScheduler().runTaskLater(HungerGames.getInstance(), new Runnable() {
            public void run() {
                // If the player isn't online, just forget it.
                if (!p.isOnline()) {
                    return;
                }

                // Send block "restore" events.
                for (Map.Entry<Location, BlockState> entry : blocks.entrySet()) {
                    Location l = entry.getKey();
                    BlockState b = entry.getValue();
                    int id = b.getTypeId();
                    byte data = b.getRawData();

                    p.sendBlockChange(l, id, data);
                }
            }
        }, 100);
    }

    private List<Location> getFramePoints() {
        List<Location> result = new ArrayList<Location>();
        int x1 = main.cuboid.getLower().getBlockX();
        int y1 = main.cuboid.getLower().getBlockY();
        int z1 = main.cuboid.getLower().getBlockZ();
        int x2 = main.cuboid.getUpper().getBlockX();
        int y2 = main.cuboid.getUpper().getBlockY();
        int z2 = main.cuboid.getUpper().getBlockZ();

        World world = main.cuboid.getWorld();
        for (int i = x1; i <= x2; i++) {
            result.add(world.getBlockAt(i, y1, z1).getLocation());
            result.add(world.getBlockAt(i, y1, z2).getLocation());
            result.add(world.getBlockAt(i, y2, z1).getLocation());
            result.add(world.getBlockAt(i, y2, z2).getLocation());
        }

        for (int j = y1; j <= y2; j++) {
            result.add(world.getBlockAt(x1, j, z1).getLocation());
            result.add(world.getBlockAt(x1, j, z2).getLocation());
            result.add(world.getBlockAt(x2, j, z1).getLocation());
            result.add(world.getBlockAt(x2, j, z2).getLocation());
        }

        for (int k = z1; k <= z2; k++) {
            result.add(world.getBlockAt(x1, y1, k).getLocation());
            result.add(world.getBlockAt(x1, y2, k).getLocation());
            result.add(world.getBlockAt(x2, y1, k).getLocation());
            result.add(world.getBlockAt(x2, y2, k).getLocation());
        }
        return result;
    }

/*
    public Lobby getLobby() {
        return lobby;
    }
*/

    public static class RegionPoint {
        public static final String MAIN1 = "p1";
        public static final String MAIN2 = "p2";
        public static final String LOBBY1 = "l1";
        public static final String LOBBY2 = "l2";
        public static final String SPEC1 = "s1";
        public static final String SPEC2 = "s2";
        //public static final String MAIN_WARP = "main.warp";
        public static final String LOBBY_WARP = "lobby.warp";
        public static final String SPEC_WARP = "spectator.warp";
        public static final String LEADERBOARD = "leaderboard";
    }
}
