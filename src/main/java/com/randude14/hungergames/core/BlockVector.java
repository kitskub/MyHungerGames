package com.randude14.hungergames.core;

/**
 * Extension of Vector that supports being compared as ints (for accuracy).
 *
 * @author sk89q
 */
public class BlockVector extends Vector {
    /**
     * Construct the Vector object.
     *
     * @param pt
     */
    public BlockVector(Vector pt) {
        super(pt);
    }

    /**
     * Construct the Vector object.
     *
     * @param x
     * @param y
     * @param z
     */
    public BlockVector(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * Construct the Vector object.
     *
     *
     * @param x
     * @param y
     * @param z
     */
    public BlockVector(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * Construct the Vector object.
     *
     *
     * @param x
     * @param y
     * @param z
     */
    public BlockVector(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Checks if another object is equivalent.
     *
     * @param obj
     * @return whether the other object is equivalent
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector)) {
            return false;
        }
        Vector other = (Vector) obj;
        return (int) other.getX() == (int) this.x && (int) other.getY() == (int) this.y
                && (int) other.getZ() == (int) this.z;

    }

    /**
     * Gets the hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return ((int) x << 19) ^
                ((int) y << 12) ^
                (int) z;
    }

    @Override
    public BlockVector toBlockVector() {
        return this;
    }
}
