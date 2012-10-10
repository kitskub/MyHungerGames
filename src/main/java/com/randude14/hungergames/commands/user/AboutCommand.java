package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AboutCommand extends Command {

	public AboutCommand() {
		super(Perm.USER_ABOUT, "about", Command.USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		ChatUtils.send(cs, ChatUtils.getHeadLiner());
		ChatUtils.send(cs, "Original Author - Randude14");
		ChatUtils.send(cs, "Active Developer - kitskub");
		ChatUtils.send(cs, "Sponsor - http://treepuncher.com");
	}

	@Override
	public String getInfo() {
		return "gives basic info about MyHungerGames";
	}

	@Override
	public String getUsage() {
		return "/%s about";
	}

}
