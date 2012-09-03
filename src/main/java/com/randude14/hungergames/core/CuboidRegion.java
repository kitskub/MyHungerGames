package com.randude14.hungergames.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sk89q
 */
public class CuboidRegion extends AbstractRegion {
    /**
     * Store the first point.
     */
    private Vector pos1;
    /**
     * Store the second point.
     */
    private Vector pos2;
    /**
     * Construct a new instance of this cuboid region.
     *
     * @param pos1
     * @param pos2
     */
    public CuboidRegion(Vector pos1, Vector pos2) {
        this(null, pos1, pos2);
    }

    /**
     * Construct a new instance of this cuboid region.
     *
     * @param world
     * @param pos1
     * @param pos2
     */
    public CuboidRegion(LocalWorld world, Vector pos1, Vector pos2) {
        super(world);
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * Get the lower point of the cuboid.
     *
     * @return min point
     */
    public Vector getMinimumPoint() {
        return new Vector(Math.min(pos1.getX(), pos2.getX()),
                         Math.min(pos1.getY(), pos2.getY()),
                         Math.min(pos1.getZ(), pos2.getZ()));
    }

    /**
     * Get the upper point of the cuboid.
     *
     * @return max point
     */
    public Vector getMaximumPoint() {
        return new Vector(Math.max(pos1.getX(), pos2.getX()),
                         Math.max(pos1.getY(), pos2.getY()),
                         Math.max(pos1.getZ(), pos2.getZ()));
    }

    public int getMinimumY() {
        return Math.min(pos1.getBlockY(), pos2.getBlockY());
    }

    public int getMaximumY() {
        return Math.max(pos1.getBlockY(), pos2.getBlockY());
    }

    /**
     * Get the number of blocks in the region.
     *
     * @return number of blocks
     */
    public int getArea() {
        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        return (int)((max.getX() - min.getX() + 1) *
                     (max.getY() - min.getY() + 1) *
                     (max.getZ() - min.getZ() + 1));
    }

    /**
     * Get X-size.
     *
     * @return width
     */
    public int getWidth() {
        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        return (int) (max.getX() - min.getX() + 1);
    }

    /**
     * Get Y-size.
     *
     * @return height
     */
    public int getHeight() {
        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        return (int) (max.getY() - min.getY() + 1);
    }

    /**
     * Get Z-size.
     *
     * @return length
     */
    public int getLength() {
        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        return (int) (max.getZ() - min.getZ() + 1);
    }

    /**
     * Get position 1.
     *
     * @return position 1
     */
    public Vector getPos1() {
        return pos1;
    }

    /**
     * Set position 1.
     *
     * @param pos1
     */
    public void setPos1(Vector pos1) {
        this.pos1 = pos1;
    }

    /**
     * Get position 2.
     *
     * @return position 2
     */
    public Vector getPos2() {
        return pos2;
    }

    /**
     * Set position 2.
     *
     * @param pos2
     */
    public void setPos2(Vector pos2) {
        this.pos2 = pos2;
    }

    /**
     * Returns true based on whether the region contains the point,
     *
     * @param pt
     */
    public boolean contains(Vector pt) {
        double x = pt.getX();
        double y = pt.getY();
        double z = pt.getZ();

        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        return x >= min.getBlockX() && x <= max.getBlockX()
                && y >= min.getBlockY() && y <= max.getBlockY()
                && z >= min.getBlockZ() && z <= max.getBlockZ();
    }

    /**
     * Get the iterator.
     *
     * @return iterator of points inside the region
     */
    @Override
    public Iterator<BlockVector> iterator() {
        return new Iterator<BlockVector>() {
            private Vector min = getMinimumPoint();
            private Vector max = getMaximumPoint();
            private int nextX = min.getBlockX();
            private int nextY = min.getBlockY();
            private int nextZ = min.getBlockZ();

            public boolean hasNext() {
                return (nextX != Integer.MIN_VALUE);
            }

            public BlockVector next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                BlockVector answer = new BlockVector(nextX, nextY, nextZ);
                if (++nextX > max.getBlockX()) {
                    nextX = min.getBlockX();
                    if (++nextY > max.getBlockY()) {
                        nextY = min.getBlockY();
                        if (++nextZ > max.getBlockZ()) {
                            nextX = Integer.MIN_VALUE;
                        }
                    }
                }
                return answer;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    /**
     * Get a list of chunks that this region is within.
     *
     * @return
     */
    public Set<Vector2D> getChunks() {
        Set<Vector2D> chunks = new HashSet<Vector2D>();

        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                chunks.add(new BlockVector2D(x >> 4, z >> 4));
            }
        }

        return chunks;
    }

    public Set<Vector> getChunkCubes() {
        Set<Vector> chunks = new HashSet<Vector>();

        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                    chunks.add(new BlockVector(x >> 4, y >> 4, z >> 4));
                }
            }
        }

        return chunks;
    }

    /**
     * Returns string representation in the format
     * "(minX, minY, minZ) - (maxX, maxY, maxZ)".
     *
     * @return string
     */
    @Override
    public String toString() {
        return getMinimumPoint() + " - " + getMaximumPoint();
    }

    public CuboidRegion clone() {
        return (CuboidRegion) super.clone();
    }
}
