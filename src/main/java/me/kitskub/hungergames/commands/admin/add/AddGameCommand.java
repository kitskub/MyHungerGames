package me.kitskub.hungergames.commands.admin.add;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.api.event.GameCreateEvent;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddGameCommand extends PlayerCommand {

	public AddGameCommand() {
		super(Perm.ADMIN_ADD_GAME, Commands.ADMIN_ADD_HELP.getCommand(), "game");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);

	    if (game != null) {
		    ChatUtils.error(player, "%s already exists.", args[0]);
		    return;
	    }
	    if(args.length == 1){
		    HungerGames.getInstance().getGameManager().createGame(args[0]);
	    }
	    else{
		    HungerGames.getInstance().getGameManager().createGame(args[0], args[1]);
	    }
	    GameCreateEvent event = new GameCreateEvent(HungerGames.getInstance().getGameManager().getRawGame(args[0]));
	    if(event.isCancelled()) {
	    	HungerGames.getInstance().getGameManager().removeGame(args[0]);
	    	ChatUtils.error(player, "Creation of game %s was cancelled.", args[0]);
	    }
	    else {
	    	ChatUtils.send(player, ChatColor.GREEN, "%s has been created. To add spawn points, simply", args[0]);
	    	ChatUtils.send(player, ChatColor.GREEN, "type the command 'add spawnpoint <game name>'", HungerGames.CMD_ADMIN);
	    }
	}

	@Override
	public String getInfo() {
		return "add a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "game <game name> [setup]";
	}
	
}
