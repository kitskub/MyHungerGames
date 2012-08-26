package com.randude14.hungergames.commands.admin.remove;

import com.randude14.hungergames.Defaults.Commands;
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

public class RemoveSpawnPointCommand extends Command {

	public RemoveSpawnPointCommand() {
		super(Commands.ADMIN_REMOVE_SPAWNPOINT, Commands.ADMIN_REMOVE_HELP.getCommand(), "spawnpoint");
	}

	@Override
	public boolean handle(CommandSender cs, String cmd, String[] args) {
	    Player player = (Player) cs;	    
	    
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    
	    game = GameManager.INSTANCE.getGame(args[0]);
	    if(game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return true;
	    }
	    
	    SessionListener.addSession(SessionType.SPAWN_REMOVER, player, game.getName());
	    ChatUtils.send(player, ChatColor.GREEN, "Hit a spawn point to remove it from %s.", game.getName());
	    return true;
	}

	@Override
	public String getInfo() {
		return "remove a spawnpoint";
	}

	@Override
	public String getUsage() {
		return "/%s remove spawnpoint <game name>";
	}
	
}
