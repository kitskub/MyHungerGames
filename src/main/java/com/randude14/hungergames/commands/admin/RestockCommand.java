package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.api.Game.GameState;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RestockCommand extends SubCommand{

	public RestockCommand() {
		super(Commands.ADMIN_RESTOCK);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
	    
	    String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
	    if (name == null) {
		    ChatUtils.helpCommand(player, command.getUsage(), cmd.getLabel());
		    return true;
	    }

	    game = GameManager.getGame(name);
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
    
}
