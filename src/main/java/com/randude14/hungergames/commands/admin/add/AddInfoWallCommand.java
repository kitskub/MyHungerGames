package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddInfoWallCommand extends Command {

	public AddInfoWallCommand() {
		super(Commands.ADMIN_ADD_INFO_WALL, Commands.ADMIN_ADD_HELP.getCommand(), "infowall");
	}

	@Override
	public boolean handle(CommandSender cs, String label, String[] args) {
	    Player player = (Player) cs;
	    if(args.length < 1){
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.INSTANCE.getGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return true;
	    }
	    
	    ChatUtils.send(player, "Click the two corners add an infowall.");
	    SessionListener.addSession(SessionType.INFO_WALL_ADDER, player, game.getName(), "game", game.getName());
	    return true;
	}

	@Override
	public String getInfo() {
		return "add an infowall";
	}

	@Override
	public String getUsage() {
		return "/%s add infowall <game name>";
	}
	
}
