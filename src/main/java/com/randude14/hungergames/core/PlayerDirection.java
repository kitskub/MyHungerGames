package com.randude14.hungergames.core;

/**
 * Direction.
 */
public enum PlayerDirection {
    NORTH(new Vector(-1, 0, 0), new Vector(0, 0, 1), true),
    NORTH_EAST((new Vector(-1, 0, -1)).normalize(), (new Vector(-1, 0, 1)).normalize(), false),
    EAST(new Vector(0, 0, -1), new Vector(-1, 0, 0), true),
    SOUTH_EAST((new Vector(1, 0, -1)).normalize(), (new Vector(-1, 0, -1)).normalize(), false),
    SOUTH(new Vector(1, 0, 0), new Vector(0, 0, -1), true),
    SOUTH_WEST((new Vector(1, 0, 1)).normalize(), (new Vector(1, 0, -1)).normalize(), false),
    WEST(new Vector(0, 0, 1), new Vector(1, 0, 0), true),
    NORTH_WEST((new Vector(-1, 0, 1)).normalize(), (new Vector(1, 0, 1)).normalize(), false),
    UP(new Vector(0, 1, 0), new Vector(0, 0, 1), true),
    DOWN(new Vector(0, -1, 0), new Vector(0, 0, 1), true);

    private Vector dir;
    private Vector leftDir;
    private boolean isOrthogonal;

    PlayerDirection(Vector vec, Vector leftDir, boolean isOrthogonal) {
        this.dir = vec;
        this.leftDir = leftDir;
        this.isOrthogonal = isOrthogonal;
    }

    public Vector vector() {
        return dir;
    }

    @Deprecated
    public Vector leftVector() {
        return leftDir;
    }

    public boolean isOrthogonal() {
        return isOrthogonal;
    }
}
