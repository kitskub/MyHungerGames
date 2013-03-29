package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class AddInfoWallCommand extends PlayerCommand {

	public AddInfoWallCommand() {
		super(Perm.ADMIN_ADD_INFO_WALL, Commands.ADMIN_ADD_HELP.getCommand(), "infowall");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
	    if(args.length < 1){
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    ChatUtils.send(player, "Click the two corners add an infowall.");
	    SessionListener.addSession(SessionType.INFO_WALL_ADDER, player, game.getName(), "game", game.getName());
	}

	@Override
	public String getInfo() {
		return "add an infowall";
	}

	@Override
	protected String getPrivateUsage() {
		return "infowall <game name>";
	}
	
}
