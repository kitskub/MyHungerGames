package me.kitskub.hungergames.commands.admin.add;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class AddHelp extends PlayerCommand {

	public AddHelp() {
		super(Perm.ADMIN_ADD_HELP, "add", ADMIN_COMMAND);
	}

	@Override
	public void handlePlayer(Player cs, String label, String[] args) {
		for (Command c : subCommands) {
			ChatUtils.helpCommand(cs, c.getUsageAndInfo(), "hga");
		}
	}

	@Override
	public String getInfo() {
		return "add items";
	}

	@Override
	protected String getPrivateUsage() {
		return "add";
	}
	
}
