package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddSpawnPointCommand extends SubCommand{

	public AddSpawnPointCommand() {
		super(Commands.ADMIN_ADD_SPAWNPOINT);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;	    
	    if (args.length < 1) {
		    ChatUtils.send(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.getGame(args[0]);

	    if (game == null) {
		 ChatUtils.sendDoesNotExist(player, args[0]);
		 return true;
	    }
	    
	    SessionListener.addSession(SessionType.SPAWN_ADDER, player, game.getName());
	    ChatUtils.send(player, ChatColor.GREEN, "Left-click blocks to add them as spawn points for %s. Right-click to finish.", game.getName());
	    return true;
	}
	
}
