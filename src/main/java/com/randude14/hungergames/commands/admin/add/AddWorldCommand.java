package com.randude14.hungergames.commands.admin.add;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AddWorldCommand extends PlayerCommand {

	public AddWorldCommand() {
		super(Perm.ADMIN_ADD_WORLD, Commands.ADMIN_ADD_HELP.getCommand(), "world");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
		if(args.length < 1){
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		game = GameManager.INSTANCE.getRawGame(args[0]);

		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
			return;
		}
		if (args.length == 1) {
			game.addWorld(player.getWorld());
		}
		else {
			World world = Bukkit.getWorld(args[1]);
			if (world == null) {
				ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[1]));
				return;
			}
			else {
				game.addWorld(player.getWorld());
			}
		}
		ChatUtils.send(player, "World added!");
	}

	@Override
	public String getInfo() {
		return "adds the world specified or you are currently in to the game";
	}

	@Override
	protected String getPrivateUsage() {
		return "world <game name> [world]";
	}
}
