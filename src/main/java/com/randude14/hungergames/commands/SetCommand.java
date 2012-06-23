package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;
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
	
	if (args.length == 0 || "?".equalsIgnoreCase(args[0])) {
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		ChatUtils.helpCommand(player, Commands.ADMIN_SET_SPAWN.getUsageAndInfo(), HungerGames.CMD_ADMIN);
		ChatUtils.helpCommand(player, Commands.ADMIN_SET_ENABLED.getUsageAndInfo(), HungerGames.CMD_ADMIN);
		return true;
	}

	game = GameManager.getGame(args[1]);
	if ("spawn".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_SET_SPAWN)) return true;
	    if (args.length < 2) {
		    ChatUtils.helpCommand(player, Commands.ADMIN_SET_SPAWN.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }

	    if (game == null) {
		    ChatUtils.sendDoesNotExist(player, args[1]);
		    return true;
	    }
	    
	    Location loc = player.getLocation();
	    game.setSpawn(loc);
	    ChatUtils.send(player, "Spawn has been set for %s.", game.getName());
	}

	else if ("enabled".equalsIgnoreCase(args[0])) {
	    if(!HungerGames.checkPermission(player, Perm.ADMIN_SET_ENABLED)) return true;
	    
	    if (args.length < 2) {
		    ChatUtils.helpCommand(player, Commands.ADMIN_SET_ENABLED.getUsage(),
			    HungerGames.CMD_ADMIN);
		    return true;
	    }

	    if (game == null) {
		    ChatUtils.sendDoesNotExist(player, args[1]);
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
		    ChatUtils.send(player, "%s has been enabled.", game.getName());
	    } else {
		    ChatUtils.send(player,
				    String.format("%s has been disabled.", game.getName()));
	    }
	}

	else {
		ChatUtils.error(player, "'%s' is not recognized.", args[0]);
		return true;
	}
	return true;
    }
    
}
