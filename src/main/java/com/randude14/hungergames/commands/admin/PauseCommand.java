package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PauseCommand extends Command {

	public PauseCommand() {
		super(Perm.ADMIN_PAUSE, "pause", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;

		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}

		game = GameManager.INSTANCE.getRawGame(name);
		if (game == null) {
		    ChatUtils.error(player, "%s does not exist.", name);
		    return;
		}

		if(game.pauseGame(player)) {
			ChatUtils.broadcast(game, "%s has been paused.", game.getName());
		}
		return;
	}

	@Override
	public String getInfo() {
		return "pause a game";
	}

	@Override
	public String getUsage() {
		return "/%s pause [game name]";
	}
    
}
