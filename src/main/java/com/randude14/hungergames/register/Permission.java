package com.randude14.hungergames.register;

import com.randude14.hungergames.Defaults.Perm;
import org.bukkit.entity.Player;

public abstract class Permission {
	
	public boolean hasPermission(Player player, String permission) {
		return hasPermission(player.getWorld().getName(), player.getName(), permission);
	}
	
	public boolean hasPermission(Player player, Perm permission) {
		if (hasPermission(player.getWorld().getName(), player.getName(), permission.getPermission())) {
			return true;
		}
		if (permission.getParent() != null) {
			return hasPermission(player, permission.getParent());
		}
		return false;
	}
	
	public abstract boolean hasPermission(String world, String player, String permission);

}
