package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.GameCreateEvent;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddGameCommand extends Command {

	public AddGameCommand() {
		super(Perm.ADMIN_ADD_GAME, Commands.ADMIN_ADD_HELP.getCommand(), "game");
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
	    Player player = (Player) cs;
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getGame(args[0]);

	    if (game != null) {
		    ChatUtils.error(player, "%s already exists.", args[0]);
		    return;
	    }
	    if(args.length == 1){
		    GameManager.INSTANCE.createGame(args[0]);
	    }
	    else{
		    GameManager.INSTANCE.createGame(args[0], args[1]);
	    }
	    GameCreateEvent event = new GameCreateEvent(GameManager.INSTANCE.getGame(args[0]));
	    if(event.isCancelled()) {
	    	GameManager.INSTANCE.removeGame(args[0]);
	    	ChatUtils.error(player, "Creation of game %s was cancelled.", args[0]);
	    }
	    else {
	    	ChatUtils.send(player, ChatColor.GREEN, "%s has been created. To add spawn points, simply", args[0]);
	    	ChatUtils.send(player, ChatColor.GREEN, "type the command '/%s add spawnpoint <game name>'", HungerGames.CMD_ADMIN);
	    }
	    return;
	}

	@Override
	public String getInfo() {
		return "add a game";
	}

	@Override
	public String getUsage() {
		return "/%s add game <game name> [setup]";
	}
	
}
