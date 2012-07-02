package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.GameCreateEvent;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddGameCommand extends SubCommand{

	public AddGameCommand() {
		super(Commands.ADMIN_ADD_GAME);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.getGame(args[0]);

	    if (game != null) {
		    ChatUtils.error(player, "%s already exists.", args[0]);
		    return true;
	    }
	    if(args.length == 1){
		    GameManager.createGame(args[0]);
	    }
	    else{
		    GameManager.createGame(args[0], args[1]);
	    }
	    GameCreateEvent event = new GameCreateEvent(GameManager.getGame(args[0]));
	    if(event.isCancelled()) {
	    	GameManager.removeGame(args[0]);
	    	ChatUtils.error(player, "Creation of game %s was cancelled.", args[0]);
	    }
	    else {
	    	ChatUtils.send(player, ChatColor.GREEN, "%s has been created. To add spawn points, simply", args[0]);
	    	ChatUtils.send(player, ChatColor.GREEN, "type the command '/%s add spawnpoint <game name>'", HungerGames.CMD_ADMIN);
	    }
	    return true;
	}
	
}
