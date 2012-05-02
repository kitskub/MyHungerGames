package com.randude14.hungergames.register;

import org.bukkit.entity.Player;

public abstract class Permission {
	
	public boolean hasPermission(Player player, String permission) {
		return hasPermission(player.getWorld().getName(), player.getName(), permission);
	}
	
	public abstract boolean hasPermission(String world, String player, String permission);

}
