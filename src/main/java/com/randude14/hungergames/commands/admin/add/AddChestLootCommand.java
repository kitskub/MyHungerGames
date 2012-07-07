package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddChestLootCommand extends SubCommand{

	public AddChestLootCommand() {
		super(Commands.ADMIN_ADD_CHEST_LOOT);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		if (args.length < 1) {
			ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
			return true;
		}
		float chance = 0;
		try {
			chance = Float.valueOf(args[0]);
		}
		catch (NumberFormatException e) {
			ChatUtils.send(player, ChatColor.GREEN, "{0} is not a valid number", args[0]);
		}
		
		if (args.length < 2) {
			ItemConfig.addChestLoot(null, player.getItemInHand(), chance);
		}
		else {
			ItemConfig.addChestLoot(args[1], player.getItemInHand(), chance);
		}
		ChatUtils.send(player, ChatColor.GREEN, "Item in hand added to chest loot");
		return true;
	}
	
}
