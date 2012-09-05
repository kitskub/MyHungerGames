package com.randude14.hungergames.register;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.core.CommandSender;

public abstract class HGPermission {
	
	public boolean hasPermission(CommandSender cs, Perm permission) {
		if (permission == null) return true;
		if (hasPermission(cs, permission.getPermission().getName())) {
			return true;
		}
		if (permission.getParent() != null) {
			return hasPermission(cs, permission.getParent());
		}
		return false;
	}
	
	public abstract boolean hasPermission(CommandSender cs, String permission);

}
