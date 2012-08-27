package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceClearCommand extends Command {

	public ForceClearCommand() {
		super(Perm.ADMIN_FORCE_CLEAR, "forceclear", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;

		String name = args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		game = GameManager.INSTANCE.getGame(name);
		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
			return;
		}
		ChatUtils.send(player, "Clearing game.");
		game.clear();
	}

	@Override
	public String getInfo() {
		return "force clear a game. NOT RECOMMENDED";
	}

	@Override
	public String getUsage() {
		return "/%s forceclear <game name>";
	}
    
}
