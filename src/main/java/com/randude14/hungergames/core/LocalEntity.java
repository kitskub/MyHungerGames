package com.randude14.hungergames.core;

/**
 * @author zml2008
 */
public abstract class LocalEntity {
    private final Location position;

    protected LocalEntity(Location position) {
        this.position = position;
    }

    public Location getPosition() {
        return position;
    }

    public boolean spawn() {
        return spawn(getPosition());
    }

    public abstract boolean spawn(Location loc);
}
