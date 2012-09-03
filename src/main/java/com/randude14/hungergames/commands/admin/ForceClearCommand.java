package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGamesBukkit;
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
		if (args.length < 1) {
			ChatUtils.helpCommand(player, getUsage(), HungerGamesBukkit.CMD_ADMIN);
			return;
		}
		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, getUsage(), HungerGamesBukkit.CMD_ADMIN);
			return;
		}
		game = GameManager.INSTANCE.getRawGame(name);
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
