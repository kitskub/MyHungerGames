package me.kitskub.hungergames.utils;

import me.kitskub.hungergames.WorldNotFoundException;

import org.bukkit.Location;

public class Cuboid {
	private final Location lower;
	private final Location upper;

	public Cuboid(Location lower, Location upper) {
		
		this.lower = new Location(
			lower.getWorld(),
			Math.min(lower.getX(), upper.getX()),
			Math.min(lower.getY(), upper.getY()),
			Math.min(lower.getZ(), upper.getZ())
			);
		this.upper  = new Location(
			lower.getWorld(),
			Math.max(lower.getX(), upper.getX()),
			Math.max(lower.getY(), upper.getY()),
			Math.max(lower.getZ(), upper.getZ())
			);
	}
	
	private Cuboid(Location lower, Location upper, boolean internal) {
		this.lower = lower;
		this.upper = upper;
	}
	
	public boolean isLocationWithin(Location loc) {
		return (upper.getX() >  loc.getX() && lower.getX() < loc.getX()
			&& upper.getZ() > loc.getZ() && lower.getZ() < loc.getZ()
			&& upper.getY() > loc.getY() && lower.getY() < loc.getY());
	}

	public Location getLower() {
		return lower;
	}

	public Location getUpper() {
		return upper;
	}
	
	public String parseToString() {
		return GeneralUtils.parseToString(lower) + ":" + GeneralUtils.parseToString(upper);
	}
	
	public static Cuboid parseFromString(String string) {
		try {
			String[] parts = string.split(":");
			Location lower = GeneralUtils.parseToLoc(parts[0]);
			Location upper = GeneralUtils.parseToLoc(parts[1]);
			return new Cuboid(lower, upper, true);
		} catch (NumberFormatException ex) {
			return null;
		} catch (WorldNotFoundException ex) {
			return null;
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
