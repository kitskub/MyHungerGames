package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddSpawnPointCommand extends PlayerCommand {

	public AddSpawnPointCommand() {
		super(Perm.ADMIN_ADD_SPAWNPOINT, Commands.ADMIN_ADD_HELP.getCommand(), "spawnpoint");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.send(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getRawGame(args[0]);

	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    SessionListener.addSession(SessionType.SPAWN_ADDER, player, game.getName());
	    ChatUtils.send(player, ChatColor.GREEN, "Left-click blocks to add them as spawn points for %s. Right-click to finish.", game.getName());
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
