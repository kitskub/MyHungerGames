package me.kitskub.hungergames.commands;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.api.Game;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;

import org.bukkit.command.CommandSender;


/**
 * Represents a subcommand
 *
 */
public abstract class Command extends org.bukkit.command.Command {
	protected Game game = null;
	protected final Perm perm;
	protected final List<Command> subCommands;
	protected final String type;
	protected final Command parent;
	
	public static final String ADMIN_COMMAND = HungerGames.CMD_ADMIN;
	public static final String USER_COMMAND = HungerGames.CMD_USER;
	
	public Command(Perm perm, Command parent, String name) {
		super(name);
		this.perm = perm;
		this.subCommands = new ArrayList<Command>();
		parent.registerSubCommand(this);
		type = parent.type;
		this.parent = parent;
	}	
	
	public Command(Perm perm, String name, String type) {
		super(name);
		this.perm = perm;
		this.subCommands = new ArrayList<Command>();
		this.type = type;
		this.parent = null;
		if (type.equalsIgnoreCase(ADMIN_COMMAND)) {
			CommandHandler.getInstance(ADMIN_COMMAND).registerCommand(this);
		}
		else {
			CommandHandler.getInstance(USER_COMMAND).registerCommand(this);
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

	protected abstract String getPrivateUsage();
	
	@Override
	public final String getUsage() {
		String parentUsage = "";
		if (parent != null) {
			parentUsage = parent.getPrivateUsage() + " ";
		}
		return "/" + type + " " + parentUsage + getPrivateUsage();
	}

	public String getUsageAndInfo() {
		return ChatColor.GREEN + getUsage() + ChatColor.GOLD + " - " + getInfo();
	}

	protected void registerSubCommand(Command c) {
		subCommands.add(c);
	}

	public boolean save() {
		if(game != null) {
		    HungerGames.getInstance().getGameManager().saveGame(game);
		    return true;
		}
		else{
		    HungerGames.getInstance().getGameManager().saveGames();
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
	
	public Perm getPerm() {
		return perm;
	}
}
