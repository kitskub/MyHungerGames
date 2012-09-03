package com.randude14.hungergames.commands.admin.remove;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGamesBukkit;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveItemSetCommand extends Command {

	public RemoveItemSetCommand() {
		super(Perm.ADMIN_REMOVE_ITEMSET, Commands.ADMIN_REMOVE_HELP.getCommand(), "itemset");
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {	    
	    Player player = (Player) cs;
	    
	    if(args.length < 2){
		    ChatUtils.helpCommand(player, getUsage(), HungerGamesBukkit.CMD_ADMIN);
		    return;
	    }
	    
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    game.removeItemSet(args[1]);
	    return;
	}

	@Override
	public String getInfo() {
		return "remove a game";
	}

	@Override
	public String getUsage() {
		return "/%s remove itemset <game name> <itemset name>";
	}
	
}
