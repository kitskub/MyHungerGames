package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * Same as command except it checks if CommandSender is player. If it is, passes it to handlePlayer
 */
public abstract class PlayerCommand extends Command {

	public PlayerCommand(Perm perm, String name, String type) {
		super(perm, name, type);
	}

	public PlayerCommand(Perm perm, Command parent, String name) {
		super(perm, parent, name);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		if (!(cs instanceof Player)) {
			cs.sendMessage("In-game use only.");
			return;
		}
		handlePlayer((Player) cs, label, args);
	}
	
	public abstract void handlePlayer(Player player, String label, String[] args);
}
