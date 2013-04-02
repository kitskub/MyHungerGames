package me.kitskub.hungergames.commands.admin;

import me.kitskub.hungergames.Defaults;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.api.Game.GameState;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class RestockCommand extends Command {

	public RestockCommand() {
		super(Perm.ADMIN_RESTOCK, "restock", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
	    
	    String name = (args.length < 1) ? Defaults.Config.DEFAULT_GAME.getGlobalString() : args[0];
	    if (name == null) {
		    ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }

	    game = HungerGames.getInstance().getGameManager().getRawGame(name);
	    if (game == null) {
		    ChatUtils.error(cs, "%s does not exist.", name);
		    return;
	    }
	    if (game.getState() != GameState.RUNNING) {
		    ChatUtils.error(cs, Lang.getNotRunning(game.getSetup()).replace("<game>", game.getName()));
		    return;
	    }
	    game.fillInventories();
	}

	@Override
	public String getInfo() {
		return "restock all a game's chests";
	}

	@Override
	protected String getPrivateUsage() {
		return "restock [game name]";
	}
    
}
