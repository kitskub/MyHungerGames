package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SponsorCommand extends Command {

	public SponsorCommand() {
		super(Commands.USER_SPONSOR, "sponsor", USER_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String cmd, String[] args) {
		Player player = (Player) cs;

		if (args.length < 1) {
			ChatUtils.send(player, getUsage(), HungerGames.CMD_USER);
			return true;
		}

		Player p = Bukkit.getServer().getPlayer(args[0]);
		if (p == null) {
			ChatUtils.error(player, "%s is not online.", args[0]);
			return true;
		}
		if (GameManager.INSTANCE.getPlayingSession(p) == null) {
			ChatUtils.error(player, "%s is not playing in a game.", p.getName());
			return true;
		}
		GameManager.INSTANCE.addSponsor(player, p);
		return true;
	}

	@Override
	public String getInfo() {
		return "sponsor a player an item";
	}

	@Override
	public String getUsage() {
		return "/%s sponsor <player>";
	}
    
}
