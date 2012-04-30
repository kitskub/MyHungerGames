package register.copy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermission extends Permission {
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
	public boolean hasPermission(String player, String world, String permission) {
		return perm.has(player, world, permission);
	}

}
