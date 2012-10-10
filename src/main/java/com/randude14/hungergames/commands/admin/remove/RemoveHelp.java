package com.randude14.hungergames.commands.admin.remove;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

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
	public String getUsage() {
		return "/%s remove";
	}
	
}
