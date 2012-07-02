package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 *
 */
public class AddWorldCommand extends SubCommand{

    public AddWorldCommand() {
        super(Commands.ADMIN_ADD_WORLD);
    }

    @Override
    public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
 
	    if(args.length < 1){
		    ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.getGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.sendDoesNotExist(player, args[0]);
		    return true;
	    }
	    if (args.length == 1) {
		    game.addWorld(player.getWorld());
	    }
	    else {
		    World world = Bukkit.getWorld(args[1]);
		    if (world == null) {
			    ChatUtils.sendDoesNotExist(player, args[1]);
			    return true;
		    }
		    else {
			    game.addWorld(player.getWorld());
			    ChatUtils.send(player, "World added!");
		    }
	    }
	return true;
    }
}
