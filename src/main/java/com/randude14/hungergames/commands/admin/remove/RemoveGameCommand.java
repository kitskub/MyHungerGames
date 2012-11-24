package com.randude14.hungergames.commands.admin.remove;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.api.event.GameRemoveEvent;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RemoveGameCommand extends PlayerCommand {

	public RemoveGameCommand() {
		super(Perm.ADMIN_REMOVE_GAME, Commands.ADMIN_REMOVE_HELP.getCommand(), "game");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getPrivateUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getRawGame(args[0]);
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    GameManager.INSTANCE.removeGame(args[0]);
	    Bukkit.getPluginManager().callEvent(new GameRemoveEvent(game));
	    ChatUtils.send(player, ChatColor.GREEN, "%s has been removed.", args[0]);
	}

	@Override
	public String getInfo() {
		return "remove a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "game <game name>";
	}
	
}
