package com.randude14.hungergames.register;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Economy {
	private final net.milkbowl.vault.economy.Economy econ;

	public Economy() {
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> provider = Bukkit
				.getServicesManager().getRegistration(
						net.milkbowl.vault.economy.Economy.class);
		econ = provider.getProvider();
	}
	
	public static boolean isVaultInstalled() {
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
