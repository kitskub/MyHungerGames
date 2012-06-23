package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.GameRemoveEvent;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 *
 */
public class RemoveCommand extends SubCommand{

    @Override
    public boolean execute(CommandSender cs, Command cmd, String[] args) {
	Player player = (Player) cs;

	if (args.length == 0 || "?".equalsIgnoreCase(args[0])) {
	    ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
	    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_SPAWNPOINT.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_CHEST.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_GAME.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_ITEMSET.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    return true;
	}
	
	game = GameManager.getGame(args[1]);
	
	if ("spawnpoint".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_REMOVE_SPAWNPOINT)) return true;
	    
	    if (args.length == 1) {
		    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_SPAWNPOINT.getUsage(),
			    HungerGames.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null) {
		ChatUtils.sendDoesNotExist(player, args[1]);
		return true;
	    }
	    
	    SessionListener.addSpawnRemover(player, game.getName());
	    ChatUtils.send(player, ChatColor.GREEN,
			    "Hit a spawn point to remove it from %s.",
			    game.getName());
	}

	else if ("chest".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_REMOVE_CHEST)) return true;
	    
	    if (args.length == 1) {
		    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_CHEST.getUsage(),
			    HungerGames.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null){
		ChatUtils.sendDoesNotExist(player, args[1]);
		return true;
	    }

	    SessionListener.addChestRemover(player, args[1]);
	    ChatUtils.send(player, ChatColor.GREEN,
			    "Hit a chest to remove it from %s.", game.getName());
	}

	else if ("game".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_REMOVE_GAME)) return true;
	    
	    if (args.length == 1) {
		    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_GAME.getUsage(),
			    HungerGames.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null){
		ChatUtils.sendDoesNotExist(player, args[1]);
		return true;
	    }
	    
	    GameManager.removeGame(args[1]);
	    HungerGames.callEvent(new GameRemoveEvent(game));
	    ChatUtils.send(player, ChatColor.GREEN, "%s has been removed.", args[1]);
	}

	else if("itemset".equalsIgnoreCase(args[0])){
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_REMOVE_ITEMSET)) return true;
	    
	    if(args.length == 2){
		    ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_ITEMSET.getUsage(),
			    HungerGames.CMD_ADMIN);
	    }
	    
	    if(game == null){
		ChatUtils.sendDoesNotExist(player, args[1]);
		return true;
	    }

	    game.removeItemSet(args[2]);
	}

	else {
		ChatUtils.error(player, "'%s' is not recognized.", args[0]);
	}

	return true;
    }
    
}
