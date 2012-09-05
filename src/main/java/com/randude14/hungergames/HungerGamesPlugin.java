package com.randude14.hungergames;

import com.randude14.hungergames.core.ServerInterface;
import com.randude14.hungergames.register.Economy;
import com.randude14.hungergames.register.HGPermission;

public interface HungerGamesPlugin {
	
	public void registerCommands();
		
	public Economy loadEconomy();

	public HGPermission loadPermission();
	
	public void loadResetter();
	
	public void registerEvents();
	
	public void loadMetrics();
	
	public ServerInterface getServerInterface();
	
	public String getVersion();
	
}
