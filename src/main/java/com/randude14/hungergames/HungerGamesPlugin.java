package com.randude14.hungergames;

import com.randude14.hungergames.core.ServerInterface;

public interface HungerGamesPlugin {
	
	public void registerCommands();
	
	public void updateConfig();
	
	public void loadRegistry();
	
	public void loadResetter();
	
	public void registerEvents();
	
	public void loadMetrics();
	
	public ServerInterface getServerInterface();
	
	public String getVersion();
}
