package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddSponsorLootCommand extends PlayerCommand {
	
	public AddSponsorLootCommand() {
		super(Perm.ADMIN_ADD_SPONSOR_LOOT, Commands.ADMIN_ADD_HELP.getCommand(), "sponsorloot");
	}
	
	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		if (args.length < 1) {
			ChatUtils.helpCommand(player, getPrivateUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		float chance = Float.valueOf(args[0]);
		if (args.length < 2) {
			ItemConfig.addSponsorLoot(null, player.getItemInHand(), chance);
		}
		else {
			ItemConfig.addSponsorLoot(args[1], player.getItemInHand(), chance);
		}
		ChatUtils.send(player, ChatColor.GREEN, "Item in hand added to sponsor loot", game.getName());
	}

	@Override
	public String getInfo() {
		return "adds the itemstack in hand to the specified itemset or global if no itemset is specified";
	}

	@Override
	protected String getPrivateUsage() {
		return "sponsorloot <money> [itemset]";
	}
	
}
