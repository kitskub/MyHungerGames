package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.core.*;

import java.util.HashMap;
import java.util.Map;

import org.spout.api.Engine;
import org.spout.api.Server;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.material.Material;

public class SpoutUtil {
    private SpoutUtil() {
    }

    private static final Map<World, LocalWorld> wlw = new HashMap<World, LocalWorld>();

    public static LocalWorld getLocalWorld(World w) {
        LocalWorld lw = wlw.get(w);
        if (lw == null) {
            lw = new SpoutWorld(w);
            wlw.put(w, lw);
        }
        return lw;
    }

    public static BlockVector toVector(Block block) {
        return new BlockVector(block.getX(), block.getY(), block.getZ());
    }

    public static BlockVector toVector(BlockFace face) {
        return toBlockVector(face.getOffset());
    }

    public static BlockVector toBlockVector(Vector3 vector) {
        return new BlockVector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector toVector(Point loc) {
        return new Vector(loc.getX(), loc.getY(), loc.getZ());
    }

    public static Vector toVector(org.spout.api.math.Vector3 vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Point toPoint(World world, Vector pt) {
        return new Point(world, (float)pt.getX(), (float)pt.getY(), (float)pt.getZ());
    }

    public static Point center(Point loc) {
        return new Point(
                loc.getWorld(),
                MathHelper.floor(loc.getX()) + 0.5F,
                MathHelper.floor(loc.getY()) + 0.5F,
                MathHelper.floor(loc.getZ()) + 0.5F
        );
    }

    public static Player matchSinglePlayer(Engine game, String name) {
        return game instanceof Server ? ((Server) game).getPlayer(name, false) : null;
    }

    public static World toWorld(WorldVector pt) {
        return ((SpoutWorld) pt.getWorld()).getWorld();
    }

    public static Location toLocation(Entity ent) {
        return new Location(getLocalWorld(ent.getWorld()), toVector(ent.getPosition()), ent.getYaw(), ent.getPitch());
    }
    
    public static ItemStack convertItemStack(org.spout.api.inventory.ItemStack stack) {
	    return new ItemStack(stack.getMaterial().getId(), stack.getAmount(), stack.getData());
    }
    
    public static org.spout.api.inventory.ItemStack convertItemStack(ItemStack stack) {
	    return new org.spout.api.inventory.ItemStack(Material.get(stack.getType()), stack.getAmount(), stack.getData());
    }
}
