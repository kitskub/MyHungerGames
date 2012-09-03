package com.randude14.hungergames.commands.admin.remove;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGamesBukkit;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.api.event.GameRemoveEvent;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveGameCommand extends Command{

	public RemoveGameCommand() {
		super(Perm.ADMIN_REMOVE_GAME, Commands.ADMIN_REMOVE_HELP.getCommand(), "game");
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {
	    Player player = (Player) cs;	    
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGamesBukkit.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getRawGame(args[0]);
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    GameManager.INSTANCE.removeGame(args[0]);
	    HungerGamesBukkit.callEvent(new GameRemoveEvent(game));
	    ChatUtils.send(player, ChatColor.GREEN, "%s has been removed.", args[0]);
	    return;
	}

	@Override
	public String getInfo() {
		return "remove a game";
	}

	@Override
	public String getUsage() {
		return "/%s remove game <game name>";
	}
	
}
