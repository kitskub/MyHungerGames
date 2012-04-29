package com.randude14.hungergames.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitPermission extends Permission {
	
	public BukkitPermission() {
	}

	@Override
	public boolean hasPermission(String p, String world, String permission) {
		Player player = Bukkit.getPlayer(p);
		if(player == null) return false;
		return player.hasPermission(world);
	}

}
