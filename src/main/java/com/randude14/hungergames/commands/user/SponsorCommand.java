package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SponsorCommand extends PlayerCommand {

	public SponsorCommand() {
		super(Perm.USER_SPONSOR, "sponsor", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		if (args.length < 1) {
			ChatUtils.send(player, getUsage(), HungerGames.CMD_USER);
			return;
		}

		Player p = Bukkit.getServer().getPlayer(args[0]);
		if (p == null) {
			ChatUtils.error(player, "%s is not online.", args[0]);
			return;
		}
		if (HungerGames.getInstance().getGameManager().getPlayingSession(p) == null) {
			ChatUtils.error(player, "%s is not playing in a game.", p.getName());
			return;
		}
		HungerGames.getInstance().getGameManager().addSponsor(player, p);
	}

	@Override
	public String getInfo() {
		return "sponsor a player an item";
	}

	@Override
	protected String getPrivateUsage() {
		return "sponsor <player>";
	}
    
}
