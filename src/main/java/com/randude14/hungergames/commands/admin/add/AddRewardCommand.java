package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddRewardCommand extends Command {

	public AddRewardCommand() {
		super(Perm.ADMIN_ADD_REWARD, Commands.ADMIN_ADD_HELP.getCommand(), "reward");
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {
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
				ChatUtils.send(player, ChatColor.GREEN, "{0} is not a valid number. Defaulting to 0", args[0]);
			}
			ItemConfig.addRandomReward(player.getItemInHand(), chance);
		}
		ChatUtils.send(player, ChatColor.GREEN, "Item in hand added to rewards");
		return;
	}

	@Override
	public String getInfo() {
		return "add current item in hand to static rewards or as a random if chance is specified";
	}

	@Override
	public String getUsage() {
		return "/%s add reward [chance]";
	}
	
}
