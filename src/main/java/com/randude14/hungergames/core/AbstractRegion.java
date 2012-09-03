package com.randude14.hungergames.core;

import java.util.Iterator;

public abstract class AbstractRegion implements Region {
    /**
     * Stores the world.
     */
    protected LocalWorld world;

    public AbstractRegion(LocalWorld world) {
        this.world = world;
    }

    @Override
    public Vector getCenter() {
        return getMinimumPoint().add(getMaximumPoint()).divide(2);
    }

    /**
     * Get the iterator.
     *
     * @return iterator of points inside the region
     */
    public Iterator<BlockVector> iterator() {
        return new RegionIterator(this);
    }

    public LocalWorld getWorld() {
        return world;
    }


    public AbstractRegion clone() {
        try {
            return (AbstractRegion) super.clone();
        } catch (CloneNotSupportedException exc) {
            return null;
        }
    }
}
