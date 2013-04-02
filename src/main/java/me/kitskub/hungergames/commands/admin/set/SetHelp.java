package me.kitskub.hungergames.commands.admin.set;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class SetHelp extends Command {

	public SetHelp() {
		super(Perm.ADMIN_SET_HELP, "set", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		for (Command c : subCommands) {
			ChatUtils.helpCommand(cs, c.getUsageAndInfo(), "hga");
		}
	}

	@Override
	public String getInfo() {
		return "set items";
	}

	@Override
	protected String getPrivateUsage() {
		return "set";
	}
	
}
