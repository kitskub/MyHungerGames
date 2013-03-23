package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectateCommand extends PlayerCommand {

	public SpectateCommand() {
		super(Perm.USER_SPECTATE, "spectate", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
		if (HungerGames.getInstance().getGameManager().removeSpectator(player)) return;
		String name = (args.length < 1) ? Defaults.Config.DEFAULT_GAME.getGlobalString() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_USER);
			return;
		}
		game = HungerGames.getInstance().getGameManager().getRawGame(name);
		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
			return;
		}
		Player spectated;
		boolean success;
		if (args.length < 2 || (spectated = Bukkit.getPlayer(args[1])) == null) {
			success = HungerGames.getInstance().getGameManager().addSpectator(player, game, null);
		}
		else {
			success = HungerGames.getInstance().getGameManager().addSpectator(player, game, spectated);
		}
		if (!success) {
			ChatUtils.error(player, "You are already spectating a game.");
			return;
		}
	}

	@Override
	public String getInfo() {
		return "sets player to flying to spectate a game or cancels a spectation";
	}

	@Override
	protected String getPrivateUsage() {
		return "spectate [<game name> [player]]";
	}
    
}
