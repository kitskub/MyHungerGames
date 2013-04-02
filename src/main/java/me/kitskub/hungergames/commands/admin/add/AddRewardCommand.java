package me.kitskub.hungergames.commands.admin.add;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.ItemConfig;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddRewardCommand extends PlayerCommand {

	public AddRewardCommand() {
		super(Perm.ADMIN_ADD_REWARD, Commands.ADMIN_ADD_HELP.getCommand(), "reward");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		if (player.getItemInHand() == null) {
			ChatUtils.error(player, "Cannot add no item to rewards!");
		}
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
	}

	@Override
	public String getInfo() {
		return "add current item in hand to static rewards or as a random if chance is specified";
	}

	@Override
	protected String getPrivateUsage() {
		return "reward [chance]";
	}
	
}
