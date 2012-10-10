package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class AddCuboidCommand extends PlayerCommand {

	public AddCuboidCommand() {
		super(Perm.ADMIN_ADD_CUBOID, Commands.ADMIN_ADD_HELP.getCommand(), "cuboid");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
	    if(args.length < 1){
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getRawGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    ChatUtils.send(player, "Click the two corners add a cuboid.");
	    SessionListener.addSession(SessionType.CUBOID_ADDER, player, game.getName());
	}

	@Override
	public String getInfo() {
		return "add a cuboid";
	}

	@Override
	public String getUsage() {
		return "/%s add cuboid <game name>";
	}
	
}
