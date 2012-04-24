package com.randude14.hungergames.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;

import com.randude14.hungergames.Plugin;

public class LocationList {
	private List<Location> locs;
	
	public LocationList(Location... args) {
		this();
		for(Location loc : args) {
			add(loc);
		}
		
	}
	
	public LocationList() {
		locs = new ArrayList<Location>();
	}
	
	public boolean add(Location loc) {
		if(contains(loc)) {
			return false;
		}
		locs.add(loc);
		return true;
	}
	
	public Location get(int index) {
		if(index >= locs.size()) {
			return null;
		}
		return locs.get(index);
	}
	
	public boolean isEmpty() {
		return locs.isEmpty();
	}
	
	public int getSize() {
		return locs.size();
	}
	
	public void clear() {
		locs.clear();
	}
	
	public boolean remove(Location loc) {
		Plugin plugin = Plugin.getInstance();
		for(int cntr = 0;cntr < locs.size();cntr++) {
			if(plugin.equals(locs.get(cntr), loc)) {
				locs.remove(cntr);
				return true;
			}
			
		}
		return false;
	}
	
	public boolean contains(Location loc) {
		Plugin plugin = Plugin.getInstance();
		for(Location comp : locs) {
			if(plugin.equals(comp, loc)) {
				return true;
			}
			
		}
		return false;
	}
	
	public Location random() {
		if(locs.isEmpty()) {
			return null;
		}
		Random rand = Plugin.getInstance().getRandom();
		return locs.get( rand.nextInt(locs.size()) );
	}

}
