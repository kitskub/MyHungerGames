package com.randude14.hungergames.core.blocks;

import com.randude14.hungergames.core.blocks.Block;


/**
 * Represents a block.
 *
 * @see Block new class to replace this one
 * @author sk89q
 */
public class BaseBlock extends Block {

    /**
     * Construct the block with its type, with default data value 0.
     *
     * @param type type ID of block
     */
    public BaseBlock(int type) {
        this(type, 0);
    }

    /**
     * Construct the block with its type and data.
     *
     * @param type type ID of block
     * @param data data value
     */
    public BaseBlock(int type, int data) {
        super(type, data);
    }

    /**
     * Get the type of block.
     * 
     * @return the type
     */
    public int getType() {
        return getId();
    }

    /**
     * Set the type of block.
     * 
     * @param type the type to set
     */
    public void setType(int type) {
        setId(type);
    }

    /**
     * Checks whether the type ID and data value are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseBlock)) {
            return false;
        }

        return getType() == ((BaseBlock) o).getType() && getData() == ((BaseBlock) o).getData();
    }
}
