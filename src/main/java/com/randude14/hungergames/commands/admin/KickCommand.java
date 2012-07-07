package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.PlayerKickGameEvent;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends SubCommand{

	public KickCommand() {
		super(Commands.ADMIN_KICK);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		if (args.length < 1) {
			ChatUtils.helpCommand(player, command.getUsage(), cmd.getLabel());
			return true;
		}

		Player kick = Bukkit.getServer().getPlayer(args[0]);
		if (kick == null) {
		    ChatUtils.error(player, "%s is not online.", args[0]);
		    return true;
		}
		game = GameManager.getSession(kick);
		if (game == null) {
		    ChatUtils.error(player, "%s is currently not in a game.", kick.getName());
		    return true;
		}
		ChatUtils.broadcast(true, "%s has been kicked from the game %s.", player.getName(), game.getName());
		HungerGames.callEvent(new PlayerKickGameEvent(game, kick));
		game.leave(kick);
		return true;
	}
    
}
