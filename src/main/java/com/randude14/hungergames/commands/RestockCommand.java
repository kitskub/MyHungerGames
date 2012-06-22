package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.CommandUsage;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Plugin;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RestockCommand extends SubCommand{

    @Override
    public boolean execute(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
	    if (!Plugin.checkPermission(player, Perm.ADMIN_RESTOCK)) return true;
	    if (args.length < 1) {
		    ChatUtils.send(player, CommandUsage.ADMIN_RESTOCK.getUsage(), cmd.getLabel());
		    return true;
	    }
	    HungerGame game = GameManager.getGame(args[0]);
	    if (game == null || !game.isRunning()) {
		    ChatUtils.error(player, "%s is not a running game.", game.getName());
		    return true;
	    }
	    game.fillInventories();
	    return true;
    }
    
}
