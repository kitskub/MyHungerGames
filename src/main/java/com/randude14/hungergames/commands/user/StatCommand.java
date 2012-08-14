package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatCommand extends SubCommand{

	public StatCommand() {
		super(Commands.USER_STAT);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		
		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, command.getUsage(), cmd.getLabel());
			return true;
		}

		game = GameManager.INSTANCE.getGame(name);
		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
			return true;
		}
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		game.listStats(player);
		return true;
	}
    
}
