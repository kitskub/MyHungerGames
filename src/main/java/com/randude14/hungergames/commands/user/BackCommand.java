package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand extends SubCommand{

	public BackCommand() {
		super(Commands.USER_BACK);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		
		if (GameManager.INSTANCE.getSession(player) != null) {
			ChatUtils.send(player, "You cannot use that command while you are in-game.");
			return true;
		}
		Location loc = GameManager.INSTANCE.getAndRemoveBackLocation(player);
		if (loc != null) {
			ChatUtils.send(player, "Teleporting you to your back location.");
			player.teleport(loc);
		}
		else {
			ChatUtils.error(player, "For some reason, there was no back location set. Did you already teleport back?");
		}
		return true;
	}
	
}
