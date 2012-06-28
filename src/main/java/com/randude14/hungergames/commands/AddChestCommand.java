package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddChestCommand extends SubCommand{

	public AddChestCommand() {
		super(Commands.ADMIN_ADD_CHEST);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
	    
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.getGame(args[0]);
	    
	    if (game == null) {
		ChatUtils.sendDoesNotExist(player, args[0]);
		return true;
	    }
	    
	    SessionListener.addChestAdder(player, args[0]);
	    ChatUtils.send(player, ChatColor.GREEN, "Hit a chest to add it to %s.", game.getName());
	    return true;
	}
	
}
