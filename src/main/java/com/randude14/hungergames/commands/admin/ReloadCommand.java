package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends Command {

	public ReloadCommand() {
		super(Commands.ADMIN_RELOAD, "reload", ADMIN_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;

		HungerGames.reload();
		ChatUtils.send(player, ChatUtils.getPrefix() + "Reloaded %s", HungerGames.getInstance().getDescription().getVersion());
		return true;
	}

	@Override
	public String getInfo() {
		return "reload MyHungerGames";
	}

	@Override
	public String getUsage() {
		return "/%s reload";
	}
    
}
