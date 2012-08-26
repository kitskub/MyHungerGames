package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.api.Game.GameState;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RestockCommand extends Command {

	public RestockCommand() {
		super(Commands.ADMIN_RESTOCK, "restock", ADMIN_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String label, String[] args) {
	    Player player = (Player) cs;
	    
	    String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
	    if (name == null) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }

	    game = GameManager.INSTANCE.getGame(name);
	    if (game == null) {
		    ChatUtils.error(player, "%s does not exist.", name);
		    return true;
	    }
	    if (game.getState() != GameState.RUNNING) {
		    ChatUtils.error(player, Lang.getNotRunning(game.getSetup()).replace("<game>", game.getName()));
		    return true;
	    }
	    game.fillInventories();
	    return true;
	}

	@Override
	public String getInfo() {
		return "restock all a game's chests";
	}

	@Override
	public String getUsage() {
		return "/%s restock [game name]";
	}
    
}
