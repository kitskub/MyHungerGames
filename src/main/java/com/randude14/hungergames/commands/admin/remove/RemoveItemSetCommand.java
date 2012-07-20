package com.randude14.hungergames.commands.admin.remove;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveItemSetCommand extends SubCommand {

	public RemoveItemSetCommand() {
		super(Commands.ADMIN_REMOVE_ITEMSET);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {	    
	    Player player = (Player) cs;
	    
	    if(args.length < 2){
		    ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return true;
	    }
	    game.removeItemSet(args[1]);
	    return true;
	}
	
}
