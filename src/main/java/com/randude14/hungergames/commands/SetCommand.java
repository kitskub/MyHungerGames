package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.Defaults.CommandUsage;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Plugin;
import com.randude14.hungergames.games.HungerGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 *
 */
public class SetCommand extends SubCommand{

    @Override
    public boolean execute(CommandSender cs, Command cmd, String[] args) {
	Player player = (Player) cs;
	
	if (args.length == 0 || "?".equals(args[0])) {
		Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		Plugin.helpCommand(player, CommandUsage.ADMIN_SET_SPAWN.getUsageAndInfo(),
				Plugin.CMD_ADMIN);
		Plugin.helpCommand(player, CommandUsage.ADMIN_SET_ENABLED.getUsageAndInfo(),
				Plugin.CMD_ADMIN);
		return true;
	}

	HungerGame game = GameManager.getGame(args[1]);
	if ("spawn".equals(args[0])) {
	    if(!Plugin.checkPermission(player, Perm.ADMIN_SET_SPAWN)) return true;
	    
	    if (args.length < 2) {
		    Plugin.helpCommand(player, CommandUsage.ADMIN_SET_SPAWN.getUsage(),
			    Plugin.CMD_ADMIN);
		    return true;
	    }

	    if (game == null) {
		    Plugin.sendDoesNotExist(player, args[1]);
		    return true;
	    }
	    
	    Location loc = player.getLocation();
	    game.setSpawn(loc);
	    Plugin.send(player, "Spawn has been set for %s.", game.getName());
	}

	else if ("enabled".equals(args[0])) {
	    if(!Plugin.checkPermission(player, Perm.ADMIN_SET_ENABLED)) return true;
	    
	    if (args.length < 2) {
		    Plugin.helpCommand(player, CommandUsage.ADMIN_SET_ENABLED.getUsage(),
			    Plugin.CMD_ADMIN);
		    return true;
	    }

	    if (game == null) {
		    Plugin.sendDoesNotExist(player, args[1]);
		    return true;
	    }
	    
	    boolean flag;
	    if (args.length == 3) {
		    flag = Boolean.valueOf(args[2]);
	    } else {
		    flag = true;
	    }
	    game.setEnabled(flag);
	    if (flag) {
		    Plugin.send(player, "%s has been enabled.", game.getName());
	    } else {
		    Plugin.send(player,
				    String.format("%s has been disabled.", game.getName()));
	    }
	}

	else {
		Plugin.error(player, "'%s' is not recognized.", args[0]);
		return true;
	}
	return true;
    }
    
}
