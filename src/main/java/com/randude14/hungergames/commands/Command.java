package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import org.bukkit.command.CommandSender;


/**
 * Represents a subcommand
 *
 */
public abstract class Command extends org.bukkit.command.Command {
	protected HungerGame game = null;
	protected final Perm perm;
	protected final List<Command> subCommands;
	
	public static final String ADMIN_COMMAND = "ADMIN";
	public static final String USER_COMMAND = "USER";
	
	public Command(Perm perm, Command parent, String name) {
		super(name);
		this.perm = perm;
		this.subCommands = new ArrayList<Command>();
		parent.registerSubCommand(this);
	}	
	
	public Command(Perm perm, String name, String type) {
		super(name);
		this.perm = perm;
		this.subCommands = new ArrayList<Command>();
		if (type.equalsIgnoreCase(ADMIN_COMMAND)) {
			CommandHandler.registerAdminCommand(this);
		}
		else {
			CommandHandler.registerUserCommand(this);
		}
	}	
	
	public abstract void handle(CommandSender cs, String label, String[] args);

	/**
	 * Checks permission then calls the handle
	 * @param cs
	 * @param label 
	 * @param args
	 * @return
	 */
	public boolean execute(CommandSender cs, String label, String[] args) {
		if (args.length >= 1) {
			Command com = searchSubCommands(args[0]);
			if (com != null) return com.execute(cs, args[0], (String[]) ArrayUtils.removeElement(args, args[0]));
		}
		if (!HungerGames.checkPermission(cs, perm)) return true;
		handle(cs, label, args);
		return true;
	}

	public abstract String getInfo();

	@Override
	public abstract String getUsage();

	public String getUsageAndInfo() {
		return getUsage() + " - " + getInfo();
	}

	protected void registerSubCommand(Command c) {
		subCommands.add(c);
	}

	public boolean save() {
		if(game != null) {
		    GameManager.INSTANCE.saveGame(game);
		    return true;
		}
		else{
		    GameManager.INSTANCE.saveGames();
		    return false;
		}
	}
	
	public Command searchSubCommands(String com) {
		for (Command c : subCommands) {
			if (c.getName().equalsIgnoreCase(com)) {
				return c;
			}
			for (String alias : c.getAliases()) {
				if (alias.equalsIgnoreCase(com)) {
					return c;
				}
			}
		}
		return null;
	}
}
