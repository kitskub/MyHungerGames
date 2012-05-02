package com.randude14.hungergames.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitPermission extends Permission {
	
	public BukkitPermission() {
	}

	@Override
	public boolean hasPermission(String world, String p, String permission) {
		Player player = Bukkit.getPlayer(p);
		if(player == null) return false;
		return player.hasPermission(permission);
	}

}
