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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddChestCommand extends PlayerCommand {

	public AddChestCommand() {
		super(Perm.ADMIN_ADD_CHEST, Commands.ADMIN_ADD_HELP.getCommand(), "chest");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
	    
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    ChatUtils.send(player, ChatColor.GREEN, "Hit a chest to add it to %s.", game.getName());
	    if (args.length == 2){
		    try {
			    float weight = Float.valueOf(args[1]);
			    SessionListener.addSession(SessionType.CHEST_ADDER, player, args[0], "weight", weight);
			    return;
		    } catch (NumberFormatException numberFormatException) {}
	    }
	    SessionListener.addSession(SessionType.CHEST_ADDER, player, args[0]);
	}

	@Override
	public String getInfo() {
		return "add a chest with optional weight";
	}

	@Override
	protected String getPrivateUsage() {
		return "chest <game name> [weight]";
	}
	
}
