package me.kitskub.hungergames.commands.admin.remove;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class RemoveItemSetCommand extends PlayerCommand {

	public RemoveItemSetCommand() {
		super(Perm.ADMIN_REMOVE_ITEMSET, Commands.ADMIN_REMOVE_HELP.getCommand(), "itemset");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {	    	    
	    if(args.length < 2){
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    game.removeItemSet(args[1]);
	}

	@Override
	public String getInfo() {
		return "remove a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "itemset <game name> <itemset name>";
	}
	
}
