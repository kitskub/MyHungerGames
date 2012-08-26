package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddChestLootCommand extends Command {

	public AddChestLootCommand() {
		super(Commands.ADMIN_ADD_CHEST_LOOT, Commands.ADMIN_ADD_HELP.getCommand(), "chestloot");
	}

	@Override
	public boolean handle(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;

		if (args.length < 1) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
			return true;
		}
		float chance = 0;
		try {
			chance = Float.valueOf(args[0]);
		}
		catch (NumberFormatException e) {
			ChatUtils.error(player,"{0} is not a valid number", args[0]);
		}
		
		if (args.length < 2) {
			if (player.getItemInHand() == null) {
				ChatUtils.error(player,"There is no item in hand. Perhaps you used the command wrong?");
				return true;
			}
			ItemConfig.addChestLoot(null, player.getItemInHand(), chance);
		}
		else {
			ItemConfig.addChestLoot(args[1], player.getItemInHand(), chance);
		}
		ChatUtils.send(player, ChatColor.GREEN, "Item in hand added to chest loot");
		return true;
	}

	@Override
	public String getInfo() {
		return "adds the itemstack in hand to the specified itemset or global if no itemset is specified";
	}

	@Override
	public String getUsage() {
		return "/%s add chestloot <chance> [itemset]";
	}
	
}
