package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddSponsorLootCommand extends Command {
	
	public AddSponsorLootCommand() {
		super(Commands.ADMIN_ADD_SPONSOR_LOOT, Commands.ADMIN_ADD_HELP.getCommand(), "sponsorloot");
	}
	
	@Override
	public boolean handle(CommandSender cs, String cmd, String[] args) {
		Player player = (Player) cs;

		if (args.length < 1) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
			return true;
		}
		float chance = Float.valueOf(args[0]);
		if (args.length < 2) {
			ItemConfig.addSponsorLoot(null, player.getItemInHand(), chance);
		}
		else {
			ItemConfig.addSponsorLoot(args[1], player.getItemInHand(), chance);
		}
		ChatUtils.send(player, ChatColor.GREEN, "Item in hand added to sponsor loot", game.getName());
		return true;
	}

	@Override
	public String getInfo() {
		return "adds the itemstack in hand to the specified itemset or global if no itemset is specified";
	}

	@Override
	public String getUsage() {
		return "/%s add sponsorloot <money> [itemset]";
	}
	
}
