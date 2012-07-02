package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCuboidCommand extends SubCommand{

	public AddCuboidCommand() {
		super(Commands.ADMIN_ADD_CUBOID);
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
	    
	    ChatUtils.send(player, "Click the two corners add a cuboid.");
	    SessionListener.addSession(SessionType.CUBOID_ADDER, player, game.getName());
	    return true;
	}
	
}
