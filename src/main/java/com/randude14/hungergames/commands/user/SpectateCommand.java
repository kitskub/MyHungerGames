package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.games.HungerGame.GameState;

import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 */
public class SpectateCommand extends SubCommand{

	public SpectateCommand() {
		super(Commands.USER_SPECTATE);
	}
	
	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
	    if (GameManager.getGame(GameManager.getSpectating(player)) != null) {
		    GameManager.getGame(GameManager.getSpectating(player)).removeSpectator(player);
		    GameManager.removeSpectator(player);
		    return true;
	    }
	    if (args.length < 1) {
		    ChatUtils.send(player, command.getUsage(), cmd.getLabel());
		    return true;
	    }
	    game = GameManager.getGame(args[0]);
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return true;
	    }
	    if (game.getState() != GameState.RUNNING) {
		    ChatUtils.error(player, Lang.getNotRunning(game.getSetup()).replace("<game>", game.getName()));
		    return true;
	    }
	    if (args.length == 2) {
		    game.addSpectator(player, null);    
	    }
	    else {
		    Player spectated = Bukkit.getPlayer(args[2]);
		    if (spectated == null) {
			    game.addSpectator(player, null);
		    }
		    else {
			    game.addSpectator(player, spectated);
		    }
	    }
	    GameManager.addSpectator(player, game.getName());
	    return true;
	}
    
}
