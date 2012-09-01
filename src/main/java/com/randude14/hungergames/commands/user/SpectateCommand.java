package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand extends Command {

	public SpectateCommand() {
		super(Perm.USER_SPECTATE, "spectate", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;
		if (GameManager.INSTANCE.removeSpectator(player)) return;
		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_USER);
			return;
		}
		game = GameManager.INSTANCE.getRawGame(name);
		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
			return;
		}
		boolean success;
		Player spectated;
		if (args.length < 2 || (spectated = Bukkit.getPlayer(args[1])) == null) {
			success = GameManager.INSTANCE.addSpectator(player, game, null);
		}
		else {
			success = GameManager.INSTANCE.addSpectator(player, game, spectated);
		}
		
		if (success) {
			ChatUtils.send(player, "You are now spectating %s", name);
		}
		else {
			ChatUtils.error(player, "You are already spectating a game.");
		}
	}

	@Override
	public String getInfo() {
		return "sets player to flying to spectate a game or cancels a spectation";
	}

	@Override
	public String getUsage() {
		return "/%s spectate [<game name> [player]]";
	}
    
}
