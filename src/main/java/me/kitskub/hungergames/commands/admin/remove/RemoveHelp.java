package me.kitskub.hungergames.commands.admin.remove;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class RemoveHelp extends PlayerCommand {

	public RemoveHelp() {
		super(Perm.ADMIN_REMOVE_HELP, "remove", ADMIN_COMMAND);
	}

	@Override
	public void handlePlayer(Player cs, String label, String[] args) {
		for (Command c : subCommands) {
			ChatUtils.helpCommand(cs, c.getUsageAndInfo(), "hga");
		}
	}

	@Override
	public String getInfo() {
		return "remove items";
	}

	@Override
	protected String getPrivateUsage() {
		return "remove";
	}
	
}
