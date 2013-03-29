package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddJoinSignCommand extends PlayerCommand {

	public AddJoinSignCommand() {
		super(Perm.ADMIN_ADD_JOIN_SIGN, Commands.ADMIN_ADD_HELP.getCommand(), "joinsign");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		if (args.length < 1) {
			ChatUtils.send(player, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);

		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
			return;
		}

		SessionListener.addSession(SessionListener.SessionType.JOIN_SIGN_ADDER, player, game.getName());
		ChatUtils.send(player, ChatColor.GREEN, "Left-click the sign to add it as a join sign.");
	}

	@Override
	public String getInfo() {
		return "add a join sign";
	}

	@Override
	protected String getPrivateUsage() {
		return "joinsign <game name>";
	}

}
