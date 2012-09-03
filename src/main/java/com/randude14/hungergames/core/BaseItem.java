package com.randude14.hungergames.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item, without an amount value. See {@link BaseItemStack} for an instance
 * with stack amount information.
 *
 * @author sk89q
 */
public class BaseItem {
    
    private int id;
    private short data;
    private final Map<Integer, Integer> enchantments = new HashMap<Integer, Integer>();

    /**
     * Construct the object.
     *
     * @param id ID of the item
     */
    public BaseItem(int id) {
        this.id = id;
        this.data = 0;
    }

    /**
     * Construct the object.
     *
     * @param id ID of the item
     * @param data data value of the item
     */
    public BaseItem(int id, short data) {
        this.id = id;
        this.data = data;
    }

    /**
     * Get the type of item.
     * 
     * @return the id
     */
    public int getType() {
        return id;
    }

    /**
     * Get the type of item.
     * 
     * @param id the id to set
     */
    public void setType(int id) {
        this.id = id;
    }

    /**
     * Get the damage value.
     * 
     * @return the damage
     */
    @Deprecated
    public short getDamage() {
        return data;
    }

    /**
     * Get the data value.
     * 
     * @return the data
     */
    public short getData() {
        return data;
    }

    /**
     * Set the data value.
     * 
     * @param data the damage to set
     */
    @Deprecated
    public void setDamage(short data) {
        this.data = data;
    }

    /**
     * Set the data value.
     * 
     * @param data the damage to set
     */
    public void setData(short data) {
        this.data = data;
    }

    /**
     * Get the map of enchantments.
     * 
     * @return map of enchantments
     */
    public Map<Integer, Integer> getEnchantments() {
        return enchantments;
    }
}
