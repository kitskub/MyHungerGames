package me.kitskub.hungergames.register;

import org.bukkit.command.CommandSender;

public class BukkitPermission extends HGPermission {
	
	@Override
	public boolean hasPermission(CommandSender cs, String permission) {
		return cs.hasPermission(permission);
	}

}
