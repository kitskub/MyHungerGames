package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.Defaults.CommandUsage;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.GameCreateEvent;
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
public class AddCommand extends SubCommand{

    @Override
    public boolean execute(CommandSender cs, Command cmd, String[] args) {
	Player player = (Player) cs;
	
	if (args.length == 0 || "?".equalsIgnoreCase(args[0])) {
	    ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
	    ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_SPAWNPOINT.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_CHEST.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_GAME.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_ITEMSET.getUsageAndInfo(),
		    HungerGames.CMD_ADMIN);
	    return true;
	}
	
	game = GameManager.getGame(args[1]);
	if ("spawnpoint".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_ADD_SPAWNPOINT)) return true;
	    
	    if (args.length == 1) {
		    ChatUtils.send(player, CommandUsage.ADMIN_ADD_SPAWNPOINT.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }

	    if (game == null) {
		 ChatUtils.sendDoesNotExist(player, args[1]);
		 return true;
	    }
	    
	    SessionListener.addSpawnAdder(player, game.getName());
	    ChatUtils.send(player, ChatColor.GREEN,
		    "Left-click blocks to add them as spawn points for %s. Right-click to finish.", game.getName());
	}

	else if ("chest".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_ADD_CHEST)) return true;
	    
	    if (args.length == 1) {
		    ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_CHEST.getUsage(),
			    HungerGames.CMD_ADMIN);
		    return true;
	    }
	    
	    if (game == null) {
		ChatUtils.sendDoesNotExist(player, args[1]);
		return true;
	    }
	    
	    SessionListener.addChestAdder(player, args[1]);
	    ChatUtils.send(player, ChatColor.GREEN,
		    "Hit a chest to add it to %s.", game.getName());
	}

	else if ("game".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_ADD_GAME)) return true;

	    if (args.length == 1) {
		    ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_GAME.getUsage(),
			    HungerGames.CMD_ADMIN);
	    }

	    if (game != null) {
		    ChatUtils.error(player, "%s already exists.", args[1]);
		    return true;
	    }
	    if(args.length == 2){
		    GameManager.createGame(args[1]);
	    }
	    else{
		    GameManager.createGame(args[1], args[2]);
	    }
	    GameCreateEvent event = new GameCreateEvent(GameManager.getGame(args[1]));
	    if(event.isCancelled()) {
	    	GameManager.removeGame(args[1]);
	    	ChatUtils.error(player, "Creation of game %s was cancelled.", args[1]);
	    }
	    else {
	    	ChatUtils.send(player, ChatColor.GREEN, "%s has been created. To add spawn points, simply", args[1]);
	    	ChatUtils.send(player, ChatColor.GREEN, "type the command '/%s add spawnpoint <game name>'", HungerGames.CMD_ADMIN);
	    }
	    
	}

	else if("itemset".equalsIgnoreCase(args[0])){
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_ADD_ITEMSET)) return true;
	    
	    if(args.length == 2){
		    ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_ITEMSET.getUsage(),
			    HungerGames.CMD_ADMIN);
	    }

	    if (game == null) {
		    ChatUtils.sendDoesNotExist(player, args[1]);
		    return true;
	    }
	    game.addItemSet(args[2]);
	}

	else {
		ChatUtils.error(player, "'%s' is not recognized.", args[0]);
	}
	return true;
    }
}
