package register.copy;

import org.bukkit.entity.Player;

public abstract class Permission {
	
	public boolean hasPermission(Player player, String permission) {
		return hasPermission(player.getName(), player.getWorld().getName(), permission);
	}
	
	public abstract boolean hasPermission(String player, String world, String permission);

}
