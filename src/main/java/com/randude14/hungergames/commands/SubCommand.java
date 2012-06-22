package com.randude14.hungergames.commands;

import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Represents a subcommand
 *
 */
public abstract class SubCommand {
	protected HungerGame game = null;

	public abstract boolean execute(CommandSender cs, Command cmd, String[] args);
	
	public boolean save() {
		if(game != null) {
		    GameManager.saveGame(game);
		    return true;
		}
		else{
		    GameManager.saveGames();
		    return false;
		}
	}
}
