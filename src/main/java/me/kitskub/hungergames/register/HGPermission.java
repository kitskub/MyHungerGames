package me.kitskub.hungergames.register;

import me.kitskub.hungergames.Defaults.Perm;
import org.bukkit.command.CommandSender;

public abstract class HGPermission {
	public static HGPermission INSTANCE;
	
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
