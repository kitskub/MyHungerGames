package com.randude14.hungergames.core;

/**
 * Represents a stack of BaseItems.
 *
 * @author sk89q
 */
public class ItemStack extends BaseItem {
    /**
     * Amount of an item.
     */
    private int amount = 1;

    /**
     * Construct the object with default stack size of one, with data value of 0.
     *
     * @param id with data value of 0.
     */
    public ItemStack(int id) {
        super(id);
    }

    /**
     * Construct the object.
     *
     * @param id type ID
     * @param amount amount in the stack
     */
    public ItemStack(int id, int amount) {
        super(id);
        this.amount = amount;
    }

    /**
     * Construct the object.
     *
     * @param id type ID
     * @param amount amount in the stack
     * @param data data value
     */
    public ItemStack(int id, int amount, short data) {
        super(id, data);
        this.amount = amount;
    }

    /**
     * Get the number of items in the stack.
     * 
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Set the amount of items in the stack.
     * 
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
