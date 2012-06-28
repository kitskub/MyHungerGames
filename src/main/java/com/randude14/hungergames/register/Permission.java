package com.randude14.hungergames.register;

import com.randude14.hungergames.Defaults.Perm;
import org.bukkit.command.CommandSender;

public abstract class Permission {
	
	public boolean hasPermission(CommandSender cs, Perm permission) {
		if (permission == null) return true;
		if (hasPermission(cs, permission.getPermission())) {
			return true;
		}
		if (permission.getParent() != null) {
			return hasPermission(cs, permission.getParent());
		}
		return false;
	}
	
	public abstract boolean hasPermission(CommandSender cs, String permission);

}
