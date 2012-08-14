package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopCommand extends SubCommand{

	public StopCommand() {
		super(Commands.ADMIN_STOP);
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
		    ChatUtils.error(player, "%s does not exist.", name);
		    return true;
		}
		if (!game.checkForGameOver(false)) return game.stopGame(player, false);
		return true;
	}
    
}
