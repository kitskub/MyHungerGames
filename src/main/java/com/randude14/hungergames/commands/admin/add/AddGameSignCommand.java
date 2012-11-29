package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddGameSignCommand extends PlayerCommand {

	public AddGameSignCommand() {
		super(Perm.ADMIN_ADD_GAME_SIGN, Commands.ADMIN_ADD_HELP.getCommand(), "gamesign");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
		if (args.length < 1) {
			ChatUtils.send(player, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		game = GameManager.INSTANCE.getRawGame(args[0]);

		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
			return;
		}

		SessionListener.addSession(SessionListener.SessionType.GAME_SIGN_ADDER, player, game.getName(), "game", game.getName());
		ChatUtils.send(player, ChatColor.GREEN, "Left-click the sign to add it as a game sign.");
	}

	@Override
	public String getInfo() {
		return "add a game sign";
	}

	@Override
	protected String getPrivateUsage() {
		return "gamesign <game name>";
	}

}
