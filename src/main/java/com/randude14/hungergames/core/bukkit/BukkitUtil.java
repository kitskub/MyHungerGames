package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.World;

public class BukkitUtil {
    private BukkitUtil() {
    }

    private static final Map<World, LocalWorld> wlw = new HashMap<World, LocalWorld>();

    public static LocalWorld getLocalWorld(World w) {
        LocalWorld lw = wlw.get(w);
        if (lw == null) {
            lw = new BukkitWorld(w);
            wlw.put(w, lw);
        }
        return lw;
    }

    public static BlockVector toVector(Block block) {
        return new BlockVector(block.getX(), block.getY(), block.getZ());
    }

    public static BlockVector toVector(BlockFace face) {
        return new BlockVector(face.getModX(), face.getModY(), face.getModZ());
    }

    public static Vector toVector(org.bukkit.Location loc) {
        return new Vector(loc.getX(), loc.getY(), loc.getZ());
    }

    public static Location toLocation(org.bukkit.Location loc) {
        return new Location(
            getLocalWorld(loc.getWorld()),
            new Vector(loc.getX(), loc.getY(), loc.getZ()),
            loc.getYaw(), loc.getPitch()
        );
    }

    public static Vector toVector(org.bukkit.util.Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static org.bukkit.Location toLocation(WorldVector pt) {
        return new org.bukkit.Location(toWorld(pt), pt.getX(), pt.getY(), pt.getZ());
    }

    public static org.bukkit.Location toLocation(World world, Vector pt) {
        return new org.bukkit.Location(world, pt.getX(), pt.getY(), pt.getZ());
    }

    public static org.bukkit.Location center(org.bukkit.Location loc) {
        return new org.bukkit.Location(
                loc.getWorld(),
                loc.getBlockX() + 0.5,
                loc.getBlockY() + 0.5,
                loc.getBlockZ() + 0.5,
                loc.getPitch(),
                loc.getYaw()
        );
    }

    public static Player matchSinglePlayer(Server server, String name) {
        List<Player> players = server.matchPlayer(name);
        if (players.size() == 0) {
            return null;
        }
        return players.get(0);
    }

    public static World toWorld(WorldVector pt) {
        return ((BukkitWorld) pt.getWorld()).getWorld();
    }

    /**
     * Bukkit's Location class has serious problems with floating point
     * precision.
     */
    public static boolean equals(org.bukkit.Location a, org.bukkit.Location b) {
        if (Math.abs(a.getX() - b.getX()) > EQUALS_PRECISION) return false;
        if (Math.abs(a.getY() - b.getY()) > EQUALS_PRECISION) return false;
        if (Math.abs(a.getZ() - b.getZ()) > EQUALS_PRECISION) return false;
        return true;
    }

    public static final double EQUALS_PRECISION = 0.0001;

    public static org.bukkit.Location toLocation(Location teleportLocation) {
        Vector pt = teleportLocation.getPosition();
        return new org.bukkit.Location(
            toWorld(teleportLocation.getWorld()),
            pt.getX(), pt.getY(), pt.getZ(),
            teleportLocation.getYaw(), teleportLocation.getPitch()
        );
    }

    public static World toWorld(final LocalWorld world) {
        return ((BukkitWorld) world).getWorld();
    }

    public static BukkitEntity toLocalEntity(Entity e) {
        switch (e.getType()) {
            case DROPPED_ITEM:
                return new BukkitItem(toLocation(e.getLocation()), ((Item)e).getItemStack(), e.getUniqueId());
            default:
                return new BukkitEntity(toLocation(e.getLocation()), e.getType(), e.getUniqueId());
        }
    }
}
