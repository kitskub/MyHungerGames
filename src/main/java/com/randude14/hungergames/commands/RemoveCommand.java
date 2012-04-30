package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.CommandUsage;;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.Plugin;
import com.randude14.hungergames.games.HungerGame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.Plugin;
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

	if (args.length == 1 || "?".equals(args[1])) {
	    Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
	    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_SPAWNPOINT.getUsage(),
		    Plugin.CMD_ADMIN);
	    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_CHEST.getUsage(),
		    Plugin.CMD_ADMIN);
	    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_GAME.getUsage(),
		    Plugin.CMD_ADMIN);
	    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_ITEMSET.getUsage(),
		    Plugin.CMD_ADMIN);
	    return true;
	}

	HungerGame game = GameManager.getGame(args[1]);
	
	if ("spawnpoint".equals(args[0])) {
	    if(!Plugin.checkPermission(player, Perm.ADMIN_REMOVE_SPAWNPOINT)) return true;
	    
	    if (args.length == 1) {
		    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_SPAWNPOINT.getUsage(),
			    Plugin.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null) {
		Plugin.sendDoesNotExist(player, args[1]);
		return true;
	    }
	    
	    Plugin.addSpawnRemover(player, game.getName());
	    Plugin.send(player, ChatColor.GREEN,
			    "Hit a spawn point to remove it from %s.",
			    game.getName());
	}

	else if ("chest".equals(args[0])) {
	    if(!Plugin.checkPermission(player, Perm.ADMIN_REMOVE_CHEST)) return true;
	    
	    if (args.length == 1) {
		    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_CHEST.getUsage(),
			    Plugin.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null){
		Plugin.sendDoesNotExist(player, args[1]);
		return true;
	    }

	    Plugin.addChestRemover(player, args[1]);
	    Plugin.send(player, ChatColor.GREEN,
			    "Hit a chest to remove it from %s.", game.getName());
	}

	else if ("game".equals(args[0])) {
	    if(!Plugin.checkPermission(player, Perm.ADMIN_REMOVE_GAME)) return true;
	    
	    if (args.length == 1) {
		    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_GAME.getUsage(),
			    Plugin.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null){
		Plugin.sendDoesNotExist(player, args[1]);
		return true;
	    }
	    
	    GameManager.removeGame(args[1]);
	    Plugin.send(player, ChatColor.GREEN, "%s has been removed.", args[1]);
	}

	else if("itemset".equals(args[0])){
	    if(!Plugin.checkPermission(player, Perm.ADMIN_REMOVE_ITEMSET)) return true;
	    
	    if(args.length == 2){
		    Plugin.helpCommand(player, CommandUsage.ADMIN_REMOVE_ITEMSET.getUsage(),
			    Plugin.CMD_ADMIN);
	    }
	    
	    if(game == null){
		Plugin.sendDoesNotExist(player, args[1]);
		return true;
	    }

	    game.removeItemSet(args[2]);
	}

	else {
		Plugin.error(player, "'%s' is not recognized.", args[0]);
	}

	return true;
    }
    
}
