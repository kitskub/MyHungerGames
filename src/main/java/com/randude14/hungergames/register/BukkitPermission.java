package com.randude14.hungergames.register;

import org.bukkit.command.CommandSender;

public class BukkitPermission extends Permission {
	
	@Override
	public boolean hasPermission(CommandSender cs, String permission) {
		return cs.hasPermission(permission);
	}

}
