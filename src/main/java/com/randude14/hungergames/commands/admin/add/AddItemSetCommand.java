package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class AddItemSetCommand extends PlayerCommand {

	public AddItemSetCommand() {
		super(Perm.ADMIN_ADD_ITEMSET, Commands.ADMIN_ADD_HELP.getCommand(), "itemset");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {	    
	    if(args.length < 2){
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    game.addItemSet(args[1]);
	    ChatUtils.send(player, "Itemset created!");
	}

	@Override
	public String getInfo() {
		return "add an itemset";
	}

	@Override
	protected String getPrivateUsage() {
		return "itemset <game name> <itemset name>";
	}
	
}
