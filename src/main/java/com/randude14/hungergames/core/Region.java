package com.randude14.hungergames.core;

import java.util.Set;

/**
 *
 * @author sk89q
 */
public interface Region extends Iterable<BlockVector>, Cloneable {
    /**
     * Get the lower point of a region.
     *
     * @return min. point
     */
    public Vector getMinimumPoint();

    /**
     * Get the upper point of a region.
     *
     * @return max. point
     */
    public Vector getMaximumPoint();

    /**
     * Get the center point of a region.
     * Note: Coordinates will not be integers
     * if the corresponding lengths are even.
     *
     * @return center point
     */
    public Vector getCenter();

    /**
     * Get the number of blocks in the region.
     *
     * @return number of blocks
     */
    public int getArea();

    /**
     * Get X-size.
     *
     * @return width
     */
    public int getWidth();

    /**
     * Get Y-size.
     *
     * @return height
     */
    public int getHeight();

    /**
     * Get Z-size.
     *
     * @return length
     */
    public int getLength();

    public Set<Vector2D> getChunks();

    public Set<Vector> getChunkCubes();
    /**
     * Returns true based on whether the region contains the point,
     *
     * @param pt
     * @return
     */
    public boolean contains(Vector pt);


    /**
     * Get the world the selection is in
     *
     * @return
     */
    public LocalWorld getWorld();

    public Region clone();
}
