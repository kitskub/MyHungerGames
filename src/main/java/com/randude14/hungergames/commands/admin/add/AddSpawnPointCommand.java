package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddSpawnPointCommand extends Command {

	public AddSpawnPointCommand() {
		super(Perm.ADMIN_ADD_SPAWNPOINT, Commands.ADMIN_ADD_HELP.getCommand(), "spawnpoint");
	}

	@Override
	public boolean handle(CommandSender cs, String cmd, String[] args) {
	    Player player = (Player) cs;	    
	    if (args.length < 1) {
		    ChatUtils.send(player, getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.INSTANCE.getGame(args[0]);

	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return true;
	    }
	    
	    SessionListener.addSession(SessionType.SPAWN_ADDER, player, game.getName());
	    ChatUtils.send(player, ChatColor.GREEN, "Left-click blocks to add them as spawn points for %s. Right-click to finish.", game.getName());
	    return true;
	}

	@Override
	public String getInfo() {
		return "add a spawnpoint";
	}

	@Override
	public String getUsage() {
		return "/%s add spawnpoint <game name>";
	}
	
}
