package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.SubCommand;

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
	    if (GameManager.INSTANCE.getGame(GameManager.INSTANCE.getSpectating(player)) != null) {
		    GameManager.INSTANCE.getGame(GameManager.INSTANCE.getSpectating(player)).removeSpectator(player);
		    GameManager.INSTANCE.removeSpectator(player);
		    return true;
	    }
	    String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
	    if (name == null) {
		    ChatUtils.helpCommand(player, command.getUsage(), cmd.getLabel());
		    return true;
	    }
	    game = GameManager.INSTANCE.getGame(name);
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
		    return true;
	    }
	    boolean success;
	    if (args.length < 2) {
		    success = game.addSpectator(player, null);    
	    }
	    else {
		    Player spectated = Bukkit.getPlayer(args[1]);
		    if (spectated == null) {
			    success = game.addSpectator(player, null);
		    }
		    else {
			    success = game.addSpectator(player, spectated);
		    }
	    }
	    if (success) GameManager.INSTANCE.addSpectator(player, game.getName());
	    return true;
	}
    
}
