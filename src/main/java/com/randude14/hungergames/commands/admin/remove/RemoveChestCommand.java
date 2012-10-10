package com.randude14.hungergames.commands.admin.remove;

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

public class RemoveChestCommand extends PlayerCommand {

	public RemoveChestCommand() {
		super(Perm.ADMIN_REMOVE_CHEST, Commands.ADMIN_REMOVE_HELP.getCommand(), "chest");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getRawGame(args[0]);
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }

	    SessionListener.addSession(SessionType.CHEST_REMOVER, player, args[0]);
	    ChatUtils.send(player, ChatColor.GREEN, "Hit a chest to remove it from %s.", game.getName());
	}

	@Override
	public String getInfo() {
		return "remove a chest if it added to the game or blacklists it if it isn't";
	}

	@Override
	public String getUsage() {
		return "/%s remove chest <game name>";
	}
}
