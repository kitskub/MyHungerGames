package com.randude14.hungergames.core;

/**
 * Represents a world.
 *
 * @author sk89q
 */
public abstract class LocalWorld {

    /**
     * Get the name of the world.
     *
     * @return
     */
    public abstract String getName();

    /**
     * Set block type.
     *
     * @param pt
     * @param type
     * @return
     */
    @Deprecated
    public abstract boolean setBlockType(Vector pt, int type);

    /**
     * Set block type.
     *
     * @param pt
     * @param type
     * @return
     */
    @Deprecated
    public boolean setBlockTypeFast(Vector pt, int type) {
        return setBlockType(pt, type);
    }

    /**
     * Get block type.
     *
     * @param pt
     * @return
     */
    public abstract int getBlockType(Vector pt);

    /**
     * Set block data.
     *
     * @param pt
     * @param data
     */
    @Deprecated
    public abstract void setBlockData(Vector pt, int data);

    /**
     * Set block data.
     *
     * @param pt
     * @param data
     */
    @Deprecated
    public abstract void setBlockDataFast(Vector pt, int data);

    /**
     * set block type & data
     * @param pt
     * @param type
     * @param data
     * @return
     */
    @Deprecated
    public boolean setTypeIdAndData(Vector pt, int type, int data) {
        boolean ret = setBlockType(pt, type);
        setBlockData(pt, data);
        return ret;
    }

    /**
     * set block type & data
     * @param pt
     * @param type
     * @param data
     * @return
     */
    @Deprecated
    public boolean setTypeIdAndDataFast(Vector pt, int type, int data) {
        boolean ret = setBlockTypeFast(pt, type);
        setBlockDataFast(pt, data);
        return ret;
    }

    /**
     * Get block data.
     *
     * @param pt
     * @return
     */
    public abstract int getBlockData(Vector pt);

    /**
     * Get block light level.
     *
     * @param pt
     * @return
     */
    public abstract int getBlockLightLevel(Vector pt);

    /**
     * Drop an item.
     *
     * @param pt
     * @param item
     * @param times
     */
    public void dropItem(Vector pt, BaseItemStack item, int times) {
        for (int i = 0; i < times; ++i) {
            dropItem(pt, item);
        }
    }

    /**
     * Drop an item.
     *
     * @param pt
     * @param item
     */
    public abstract void dropItem(Vector pt, BaseItemStack item);

    /**
     * Remove entities in an area.
     *
     * @param type
     * @param origin
     * @param radius
     * @return
     */
    public abstract int removeEntities(EntityType type, Vector origin, int radius);

    /**
     * Checks if the chunk pt is in is loaded. if not, loads the chunk
     *
     * @param pt Position to check
     */
    public void checkLoadedChunk(Vector pt) {
    }

    /**
     * Compare if the other world is equal.
     *
     * @param other
     * @return
     */
    @Override
    public abstract boolean equals(Object other);

    /**
     * Hash code.
     *
     * @return
     */
    @Override
    public abstract int hashCode();

    /**
     * Get the world's height
     *
     * @return
     */
    public int getMaxY() {
        return 255;
    }

    public LocalEntity[] getEntities(Region region) {
        return new LocalEntity[0];
    }

    public int killEntities(LocalEntity... entities) {
        return 0;
    }
    
    public boolean setBlock(Vector pt, Block block, boolean notifyAdjacent) {
        boolean successful;
        
        // Default implementation will call the old deprecated methods
        if (notifyAdjacent) {
            successful = setTypeIdAndData(pt, block.getId(), block.getData());
        } else {
            successful = setTypeIdAndDataFast(pt, block.getId(), block.getData());
        }
        
        return successful;
    }

    public BaseBlock getBlock(Vector pt) {
        checkLoadedChunk(pt);

        int type = getBlockType(pt);
        int data = getBlockData(pt);
        
	return new BaseBlock(type, data);
    }
}
