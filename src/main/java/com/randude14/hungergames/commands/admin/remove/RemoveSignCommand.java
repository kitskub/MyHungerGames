package com.randude14.hungergames.commands.admin.remove;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveSignCommand extends SubCommand {

	public RemoveSignCommand() {
		super(Commands.ADMIN_REMOVE_SIGN);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		SessionListener.addSession(SessionListener.SessionType.SIGN_REMOVER, player, "");
		ChatUtils.send(player, ChatColor.GREEN, "Hit a sign to remove it. If you do not hit a sign, nothing will happen.");
		return true;
	}
	
}
