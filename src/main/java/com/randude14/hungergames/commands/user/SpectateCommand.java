package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand extends Command {

	public SpectateCommand() {
		super(Commands.USER_SPECTATE, "spectate", USER_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String label, String[] args) {
	    Player player = (Player) cs;
	    if (GameManager.INSTANCE.getGame(GameManager.INSTANCE.getSpectating(player)) != null) {
		    GameManager.INSTANCE.getGame(GameManager.INSTANCE.getSpectating(player)).removeSpectator(player);
		    GameManager.INSTANCE.removeSpectator(player);
		    return true;
	    }
	    String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
	    if (name == null) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_USER);
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

	@Override
	public String getInfo() {
		return "sets player to flying to spectate a game or cancels a spectation";
	}

	@Override
	public String getUsage() {
		return "/%s spectate [<game name> [player]]";
	}
    
}
