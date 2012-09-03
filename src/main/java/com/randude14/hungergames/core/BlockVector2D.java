package com.randude14.hungergames.core;

/**
 * Extension of Vector2D that supports being compared as ints (for accuracy).
 *
 * @author sk89q
 */
public class BlockVector2D extends Vector2D {
    /**
     * Construct the Vector object.
     *
     * @param pt
     */
    public BlockVector2D(Vector2D pt) {
        super(pt);
    }

    /**
     * Construct the Vector object.
     *
     * @param x
     * @param z
     */
    public BlockVector2D(int x, int z) {
        super(x, z);
    }

    /**
     * Construct the Vector object.
     *
     * @param x
     * @param z
     */
    public BlockVector2D(float x, float z) {
        super(x, z);
    }

    /**
     * Construct the Vector object.
     *
     * @param x
     * @param z
     */
    public BlockVector2D(double x, double z) {
        super(x, z);
    }

    /**
     * Checks if another object is equivalent.
     *
     * @param obj
     * @return whether the other object is equivalent
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2D)) {
            return false;
        }
        Vector2D other = (Vector2D) obj;
        return (int) other.x == (int) this.x && (int) other.z == (int) this.z;

    }

    /**
     * Gets the hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return (Integer.valueOf((int) x).hashCode() >> 13) ^
                Integer.valueOf((int) z).hashCode();
    }

    @Override
    public BlockVector2D toBlockVector2D() {
        return this;
    }
}
