package com.randude14.hungergames.commands.admin.set;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class SetFixedChestCommand extends PlayerCommand {

    public SetFixedChestCommand() {
	    super(Perm.ADMIN_SET_FIXED_CHEST, Commands.ADMIN_SET_HELP.getCommand(), "fixedchest");
    }

    @Override
    public void handlePlayer(Player player, String cmd, String[] args) {
	    if (args.length < 2) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getRawGame(args[0]);
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    String name = args[1];
	    if (name.equalsIgnoreCase("false")) {
		    SessionListener.addSession(SessionType.FIXED_CHEST_REMOVER, player, game.getName());
		    ChatUtils.send(player, "Click chest to remove it from being a fixed item chest.");
		    return;
	    }
	    if (!ItemConfig.getFixedChests().contains(name)) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
		    return;
	    }
	    SessionListener.addSession(SessionType.FIXED_CHEST_ADDER, player, game.getName(), "name", name);
	    ChatUtils.send(player, "Click chest to add it as a fixed item chest.");
    }

	@Override
	public String getInfo() {
		return "Sets a chest to a specific fixed chest itemset or removes it from being a fixed chest if name is false";
	}

	@Override
	public String getUsage() {
		return "/%s set fixedchest <game name> <name|false>";
	}
    
}
