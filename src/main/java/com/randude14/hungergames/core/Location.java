package com.randude14.hungergames.core;

import com.randude14.hungergames.core.blocks.Block;

public class Location {
    private final LocalWorld world;
    private final Vector position;
    private final float yaw;
    private final float pitch;

    public Location(LocalWorld world, Vector position) {
        this(world, position, 0, 0);
    }

    public Location(LocalWorld world, Vector position, float yaw, float pitch) {
        this.world = world;
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LocalWorld getWorld() {
        return world;
    }

    public Vector getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Location setAngles(float yaw, float pitch) {
        return new Location(world, position, yaw, pitch);
    }

    public Location setPosition(Vector position) {
        return new Location(world, position, yaw, pitch);
    }

    public Location add(Vector other) {
        return setPosition(position.add(other));
    }

    public Location add(double x, double y, double z) {
        return setPosition(position.add(x, y, z));
    }

    public Vector getDirection() {
        final double yawRadians = Math.toRadians(yaw);
        final double pitchRadians = Math.toRadians(pitch);
        final double y = -Math.sin(pitchRadians);

        final double h = Math.cos(pitchRadians);

        final double x = -h * Math.sin(yawRadians);
        final double z = h * Math.cos(yawRadians);

        return new Vector(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location))
            return false;

        Location location = (Location) obj;
        if (!world.equals(location.world))
            return false;

        if (!position.equals(location.position))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return position.hashCode() + 19 * world.hashCode();
    }


    public static Location fromLookAt(LocalWorld world, Vector start, Vector lookAt) {
        final Vector diff = lookAt.subtract(start);

        return fromEye(world, start, diff);
    }

    public static Location fromEye(LocalWorld world, Vector start, Vector eye) {
        final double eyeX = eye.getX();
        final double eyeZ = eye.getZ();
        final float yaw = (float) Math.toDegrees(Math.atan2(-eyeX, eyeZ));
        final double length = Math.sqrt(eyeX * eyeX + eyeZ * eyeZ);
        final float pitch = (float) Math.toDegrees(Math.atan2(-eye.getY(), length));

        return new Location(world, start, yaw, pitch);
    }
    
    public Block getBlock() {
	    return world.getBlock(position);
    }
}
