package com.randude14.hungergames.core;

/**
 *
 * @author sk89q
 */
public abstract class LocalPlayer extends CommandSender {
    /**
     * Server.
     */
    protected ServerInterface server;

    /**
     * Construct the object.
     *
     * @param server
     */
    protected LocalPlayer(ServerInterface server) {
        this.server = server;
    }

    /**
     * Get the player's cardinal direction (N, W, NW, etc.). May return null.
     *
     * @return
     */
    public PlayerDirection getCardinalDirection() {
        return getCardinalDirection(0);
    }

    /**
     * Get the player's cardinal direction (N, W, NW, etc.) with an offset. May return null.
     * @param yawOffset offset that is added to the player's yaw before determining the cardinal direction
     *
     * @return
     */
    public PlayerDirection getCardinalDirection(int yawOffset) {
        if (getPitch() > 67.5) {
            return PlayerDirection.DOWN;
        }
        if (getPitch() < -67.5) {
            return PlayerDirection.UP;
        }

        // From hey0's code
        double rot = (getYaw() + yawOffset - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    /**
     * Returns direction according to rotation. May return null.
     *
     * @param rot
     * @return
     */
    private static PlayerDirection getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return PlayerDirection.NORTH;
        } else if (22.5 <= rot && rot < 67.5) {
            return PlayerDirection.NORTH_EAST;
        } else if (67.5 <= rot && rot < 112.5) {
            return PlayerDirection.EAST;
        } else if (112.5 <= rot && rot < 157.5) {
            return PlayerDirection.SOUTH_EAST;
        } else if (157.5 <= rot && rot < 202.5) {
            return PlayerDirection.SOUTH;
        } else if (202.5 <= rot && rot < 247.5) {
            return PlayerDirection.SOUTH_WEST;
        } else if (247.5 <= rot && rot < 292.5) {
            return PlayerDirection.WEST;
        } else if (292.5 <= rot && rot < 337.5) {
            return PlayerDirection.NORTH_WEST;
        } else if (337.5 <= rot && rot < 360.0) {
            return PlayerDirection.NORTH;
        } else {
            return null;
        }
    }

    /**
     * Get the ID of the item that the player is holding.
     *
     * @return
     */
    public abstract int getItemInHand();

    /**
     * Get the name of the player.
     *
     * @return String
     */
    @Override
    public abstract String getName();

    /**
     * Get the player's position.
     *
     * @return point
     */
    public abstract WorldVector getPosition();

    /**
     * Get the player's world.
     *
     * @return point
     */
    public abstract LocalWorld getWorld();

    /**
     * Get the player's view yaw.
     *
     * @return yaw
     */
    public abstract double getYaw();

    /**
     * Get the player's view pitch.
     *
     * @return pitch
     */
    public abstract double getPitch();

    /**
     * Print a WorldEdit message.
     *
     * @param msg
     */
    public abstract void send(String msg);

    /**
     * Move the player.
     *
     * @param pos
     * @param pitch
     * @param yaw
     */
    public abstract void setPosition(Vector pos, float pitch, float yaw);

    /**
     * Move the player.
     *
     * @param pos
     */
    public void setPosition(Vector pos) {
        setPosition(pos, (float) getPitch(), (float) getYaw());
    }

    /**
     * Checks if a player has permission.
     *
     * @param perm
     * @return
     */
    @Override
    public abstract boolean hasPermission(String perm);

    public abstract void giveItem(int type, int amt);
    /**
     * Returns true if equal.
     *
     * @param other
     * @return whether the other object is equivalent
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LocalPlayer)) {
            return false;
        }
        LocalPlayer other2 = (LocalPlayer) other;
        return other2.getName().equals(getName());
    }

    /**
     * Gets the hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
