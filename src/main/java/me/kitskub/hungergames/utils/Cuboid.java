package me.kitskub.hungergames.utils;

import me.kitskub.hungergames.WorldNotFoundException;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Cuboid {

	private final Vector lower;
	private final Vector upper;
	private final World world;
	//private final Location lower;
	//private final Location upper;

	public Cuboid(Location lower, Location upper) {

		this.lower = new Vector(
			Math.min(lower.getX(), upper.getX()),
			Math.min(lower.getY(), upper.getY()),
			Math.min(lower.getZ(), upper.getZ()));
		this.upper = new Vector(
			Math.max(lower.getX(), upper.getX()),
			Math.max(lower.getY(), upper.getY()),
			Math.max(lower.getZ(), upper.getZ()));
		world = lower.getWorld();
	}

	private Cuboid(Vector lower, Vector upper, World world) {
		this.lower = lower;
		this.upper = upper;
		this.world = world;
	}

	public boolean isLocationWithin(Location loc) {
		return (upper.getX() > loc.getX() && lower.getX() < loc.getX()
			&& upper.getZ() > loc.getZ() && lower.getZ() < loc.getZ()
			&& upper.getY() > loc.getY() && lower.getY() < loc.getY());
	}

	public Location getLowerLocation() {
		return lower.toLocation(world);
	}

	public Location getUpperLocation() {
		return upper.toLocation(world);
	}

	public Vector getLower() {
		return lower;
	}

	public Vector getUpper() {
		return upper;
	}

	public World getWorld() {
		return world;
	}

	public String parseToString() {
		return GeneralUtils.parseToString(lower) + ":" + GeneralUtils.parseToString(upper) + ":" + world.getName();
	}

	public static Cuboid parseFromString(String string) {
		try {
			String[] parts = string.split(":");
			if (parts.length == 2) {
				// old
				Location lower = GeneralUtils.parseToLoc(parts[0]);
				Location upper = GeneralUtils.parseToLoc(parts[1]);
				return new Cuboid(lower.toVector(), upper.toVector(), lower.getWorld());
			} else {
				Vector lower = GeneralUtils.parseToVector(parts[0]);
				Vector upper = GeneralUtils.parseToVector(parts[1]);
				World world = Bukkit.getWorld(parts[3]);
				if (world == null) {
					throw new WorldNotFoundException();
				}
				return new Cuboid(lower, upper, world);
			}
		} catch (NumberFormatException ex) {
			return null;
		} catch (WorldNotFoundException ex) {
			return null;
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public boolean contains(Location loc) {
		if (loc == null || !world.getName().equals(loc.getWorld().getName())) {
			return false;
		}
		return upper.getBlockX() >= loc.getBlockX() && lower.getBlockX() <= loc.getBlockX()
			&& upper.getBlockZ() >= loc.getBlockZ() && lower.getBlockZ() <= loc.getBlockZ()
			&& upper.getBlockY() >= loc.getBlockY() && lower.getBlockY() <= loc.getBlockY();
	}

	public boolean contains(Location loc, int radius) {
		if (loc == null || !world.getName().equals(loc.getWorld().getName())) {
			return false;
		}
		return (upper.getBlockX() >= loc.getBlockX() - radius && lower.getBlockX() <= loc.getBlockX() + radius
			&& upper.getBlockZ() >= loc.getBlockZ() - radius && lower.getBlockZ() <= loc.getBlockZ() + radius
			&& upper.getBlockY() >= loc.getBlockY() - radius && lower.getBlockY() <= loc.getBlockY() + radius);
	}
}
