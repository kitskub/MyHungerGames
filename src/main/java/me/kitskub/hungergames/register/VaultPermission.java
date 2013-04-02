package me.kitskub.hungergames.register;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermission extends HGPermission {
	private final net.milkbowl.vault.permission.Permission perm;
	
	public VaultPermission() {
		RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> provider =
				Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		perm = provider.getProvider();
	}
	
	public static boolean isVaultInstalled() {
		try {
			Class.forName("net.milkbowl.vault.permission.Permission");
			return true;
		} catch (Exception ex) {
			return false;
		}
		
	}

	@Override
	public boolean hasPermission(CommandSender cs, String permission) {
		if (cs instanceof Player) {
			return perm.has(((Player) cs).getWorld(), ((Player) cs).getName(), permission);
		}
		return perm.has(cs, permission);
	}
}
