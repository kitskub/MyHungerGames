package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Represents a subcommand
 *
 */
public abstract class SubCommand {
	protected HungerGame game = null;
	protected final Commands command;

	public SubCommand(Commands command) {
		this.command = command;
	}

	public abstract boolean handle(CommandSender cs, Command cmd, String[] args);

	/**
	 * Checks permission then calls the handle
	 * @param cs
	 * @param cmd
	 * @param args
	 * @return
	 */
	public boolean execute(CommandSender cs, Command cmd, String[] args) {
		if (!HungerGames.checkPermission(cs, command.getPerm())) return true;
		return handle(cs, cmd, args);
	}
	
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
