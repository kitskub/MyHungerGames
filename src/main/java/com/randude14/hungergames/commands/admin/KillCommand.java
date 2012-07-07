package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand extends SubCommand{

	public KillCommand() {
		super(Commands.ADMIN_KILL);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		if (args.length < 1) {
			ChatUtils.helpCommand(player, command.getUsage(), cmd.getLabel());
			return true;
		}

		Player kill = Bukkit.getServer().getPlayer(args[0]);
		if (kill == null) {
		    ChatUtils.error(player, "%s is not online.", args[0]);
		    return true;
		}
		game = GameManager.getSession(kill);
		if (game == null) {
		    ChatUtils.error(player, "%s is currently not in a game.", kill.getName());
		    return true;
		}
		ChatUtils.broadcast(true, "%s has been killed by an admin.", player.getName());
		game.killed(kill);
		return true;
	}
	
}
