package com.randude14.hungergames.core;

/**
 * A vector with a world component.
 * 
 * @author sk89q
 */
public class WorldVector extends Vector {
    /**
     * Represents the world.
     */
    private LocalWorld world;

    /**
     * Construct the Vector object.
     *
     * @param world 
     * @param x
     * @param y
     * @param z
     */
    public WorldVector(LocalWorld world, double x, double y, double z) {
        super(x, y, z);
        this.world = world;
    }

    /**
     * Construct the Vector object.
     *
     * @param world 
     * @param x
     * @param y
     * @param z
     */
    public WorldVector(LocalWorld world, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
    }

    /**
     * Construct the Vector object.
     *
     * @param world 
     * @param x
     * @param y
     * @param z
     */
    public WorldVector(LocalWorld world, float x, float y, float z) {
        super(x, y, z);
        this.world = world;
    }

    /**
     * Construct the Vector object.
     *
     * @param world 
     * @param pt
     */
    public WorldVector(LocalWorld world, Vector pt) {
        super(pt);
        this.world = world;
    }

    /**
     * Construct the Vector object.
     * 
     * @param world 
     */
    public WorldVector(LocalWorld world) {
        super();
        this.world = world;
    }

    /**
     * Get the world.
     * 
     * @return
     */
    public LocalWorld getWorld() {
        return world;
    }

    /**
     * Get a block point from a point.
     * 
     * @param world 
     * @param x
     * @param y
     * @param z
     * @return point
     */
    public static WorldVector toBlockPoint(LocalWorld world, double x, double y, double z) {
        return new WorldVector(world, (int) Math.floor(x),
                 (int) Math.floor(y),
                 (int) Math.floor(z));
    }
}
