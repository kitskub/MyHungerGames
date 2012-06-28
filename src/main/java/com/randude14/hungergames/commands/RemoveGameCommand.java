package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.GameRemoveEvent;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveGameCommand extends SubCommand{

	public RemoveGameCommand() {
		super(Commands.ADMIN_REMOVE_GAME);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;	    
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null){
		ChatUtils.sendDoesNotExist(player, args[0]);
		return true;
	    }
	    
	    GameManager.removeGame(args[0]);
	    HungerGames.callEvent(new GameRemoveEvent(game));
	    ChatUtils.send(player, ChatColor.GREEN, "%s has been removed.", args[0]);
	    return true;
	}
	
}
