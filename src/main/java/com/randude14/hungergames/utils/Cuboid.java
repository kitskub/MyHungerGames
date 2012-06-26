package com.randude14.hungergames.utils;

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
}
