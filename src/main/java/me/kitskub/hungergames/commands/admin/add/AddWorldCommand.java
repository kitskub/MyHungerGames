package me.kitskub.hungergames.commands.admin.add;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

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
		game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);

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
