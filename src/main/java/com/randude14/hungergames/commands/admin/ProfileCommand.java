package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.PerformanceMonitor;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand extends SubCommand {

	public ProfileCommand() {
		super(Commands.ADMIN_PROFILE);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		PerformanceMonitor.on();
		ChatUtils.send((Player) cs, "Enabled profiler.");
		return true;	
	}

}
