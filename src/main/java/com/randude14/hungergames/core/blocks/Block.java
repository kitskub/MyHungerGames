package com.randude14.hungergames.core.blocks;


public class Block {
    
    /**
     * Indicates the highest possible block ID (inclusive) that can be used. This value
     * is subject to change depending on the implementation, but internally this class
     * only supports a range of 4096 IDs (for space reasons), which coincides with the
     * number of possible IDs that official Minecraft supports as of version 1.3.
     */
    public static final int MAX_ID = 4095;
    
    /**
     * Indicates the maximum data value (inclusive) that can be used. Minecraft 1.4 may
     * abolish usage of data values and this value may be removed in the future.
     */
    public static final int MAX_DATA = 15;
    
    // Instances of this class should be _as small as possible_ because there will
    // be millions of instances of this object.
    
    private short id;
    private short data;
    
    /**
     * Construct a block with the given ID and a data value of 0.
     * 
     * @param id ID value
     * @see #setId(int)
     */
    public Block(int id) {
        setId(id);
        setData(0);
    }
    
    /**
     * Construct a block with the given ID and data value.
     * 
     * @param id ID value
     * @param data data value
     * @see #setId(int)
     * @see #setData(int)
     */
    public Block(int id, int data) {
        setId(id);
        setData(data);
    }
    
    /**
     * Get the ID of the block.
     * 
     * @return ID (between 0 and {@link #MAX_ID})
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the block ID.
     * 
     * @param id block id (between 0 and {@link #MAX_ID}).
     */
    public void setId(int id) {
        if (id > MAX_ID) {
            throw new IllegalArgumentException("Can't have a block ID above "
                    + MAX_ID + " (" + id + " given)");
        }

        if (id < 0) {
            throw new IllegalArgumentException("Can't have a block ID below 0");
        }
        
        this.id = (short) id;
    }
    
    /**
     * Get the block's data value.
     * 
     * @return data value (0-15)
     */
    public int getData() {
        return data;
    }

    /**
     * Set the block's data value.
     * 
     * @param data block data value (between 0 and {@link #MAX_DATA}).
     */
    public void setData(int data) {
        if (data > MAX_DATA) {
            throw new IllegalArgumentException(
                    "Can't have a block data value above " + MAX_DATA + " ("
                            + data + " given)");
        }
        
        if (data < -1) {
            throw new IllegalArgumentException("Can't have a block data value below -1");
        }
        
        this.data = (short) data;
    }
    
    /**
     * Set both the block's ID and data value.
     * 
     * @param id ID value
     * @param data data value
     * @see #setId(int)
     * @see #setData(int)
     */
    public void setIdAndData(int id, int data) {
        setId(id);
        setData(data);
    }
    
    /**
     * Returns whether the data value is -1, indicating that this block is to be
     * used as a wildcard matching block.
     * 
     * @return true if the data value is -1
     */
    public boolean hasWildcardData() {
        return getData() == -1;
    }

    @Override
    public int hashCode() {
        int ret = getId() << 3;
        if (getData() != (byte) -1) ret |= getData();
        return ret;
    }

    @Override
    public String toString() {
        return "Block{ID:" + getId() + ", Data: " + getData() + "}";
    }
    
}
