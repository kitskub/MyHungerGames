package me.kitskub.hungergames.register;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Economy {
	private final net.milkbowl.vault.economy.Economy econ;

	public Economy() {
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> provider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (provider == null) throw new IllegalStateException("Vault was not registered but is enabled");
		econ = provider.getProvider();
	}
	
	public static boolean isVaultInstalled() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
		if (plugin == null || !plugin.isEnabled()) return false;
		try {
			Class.forName("net.milkbowl.vault.economy.Economy");
			return true;
		} catch (Exception ex) {
			return false;
		}
		
	}
	
	public void withdraw(String player, double amount) {
		if (econ == null) return;
		econ.withdrawPlayer(player, amount);
	}
	
	public void deposit(String player, double amount) {
		if (econ == null) return;
		econ.depositPlayer(player, amount);
	}
	
	public boolean hasEnough(String player, double amount) {
		if (econ == null) return true;
		return econ.has(player, amount);
	}

}
