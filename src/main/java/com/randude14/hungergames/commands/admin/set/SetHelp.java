package com.randude14.hungergames.commands.admin.set;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.apache.commons.lang.ArrayUtils;

import org.bukkit.command.CommandSender;

public class SetHelp extends Command {

	public SetHelp() {
		super(Perm.ADMIN_SET_HELP, "set", ADMIN_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String label, String[] args) {
		if (args.length >= 1) {
			Command com = searchSubCommands(args[0]);
			if (com != null) return com.execute(cs, args[0], (String[]) ArrayUtils.removeElement(args, args[0]));
		}
		for (Command c : subCommands) {
			ChatUtils.helpCommand(cs, c.getUsageAndInfo(), "hga");
		}
		return true;
	}

	@Override
	public String getInfo() {
		return "set items";
	}

	@Override
	public String getUsage() {
		return "/%s set";
	}
	
}
