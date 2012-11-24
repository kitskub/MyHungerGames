package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.PlayerLeaveGameEvent;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends Command {

	public KickCommand() {
		super(Perm.ADMIN_KICK, "kick", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {

		if (args.length < 1) {
			ChatUtils.helpCommand(cs, getPrivateUsage(), HungerGames.CMD_ADMIN);
			return;
		}

		Player kick = Bukkit.getServer().getPlayer(args[0]);
		if (kick == null) {
		    ChatUtils.error(cs, "%s is not online.", args[0]);
		    return;
		}
		game = GameManager.INSTANCE.getRawSession(kick);
		if (game == null) {
		    ChatUtils.error(cs, "%s is currently not in a game.", kick.getName());
		    return;
		}
		ChatUtils.broadcast(game, "%s has been kicked from the game %s.", cs.getName(), game.getName());
		Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(game, kick, PlayerLeaveGameEvent.Type.KICK));
		game.leave(kick, false);
	}

	@Override
	public String getInfo() {
		return "kick a player from a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "kick <player>";
	}
    
}
