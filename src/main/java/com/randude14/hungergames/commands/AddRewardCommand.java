package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddRewardCommand extends SubCommand{

	public AddRewardCommand() {
		super(Commands.ADMIN_ADD_REWARD);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;	    

		if (args.length < 1) {
			ItemConfig.addStaticReward(player.getItemInHand());
		}
		else {
			float chance = 0;
			try {
				chance = Float.valueOf(args[0]);
			}
			catch (NumberFormatException e) {
				ChatUtils.send(player, ChatColor.GREEN, "{0} is not a valid number", args[0]);
			}
			ItemConfig.addRandomReward(player.getItemInHand(), chance);
		}
		ChatUtils.send(player, ChatColor.GREEN, "Item in hand added to rewards");
		return true;
	}
	
}
