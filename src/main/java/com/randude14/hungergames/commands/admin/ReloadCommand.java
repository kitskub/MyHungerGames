package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {

	public ReloadCommand() {
		super(Perm.ADMIN_RELOAD, "reload", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		HungerGames.reload();
		ChatUtils.send(cs, ChatUtils.getPrefix() + "Reloaded %s", HungerGames.getInstance().getDescription().getVersion());
	}

	@Override
	public String getInfo() {
		return "reload MyHungerGames";
	}

	@Override
	protected String getPrivateUsage() {
		return "reload";
	}
    
}
