package me.kitskub.hungergames.commands.admin;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.games.User;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand extends Command {

	public KillCommand() {
		super(Perm.ADMIN_KILL, "kill", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		if (args.length < 1) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}

		Player kill = Bukkit.getServer().getPlayer(args[0]);
		if (kill == null) {
		    ChatUtils.error(cs, "%s is not online.", args[0]);
		    return;
		}
		User get = User.get(kill);
		game = get.getGameInEntry().getGame();
		if (game == null) {
		    ChatUtils.error(cs, "%s is currently not in a game.", kill.getName());
		    return;
		}
		ChatUtils.broadcast(game, "%s has been killed by an admin.", kill.getName());
		kill.setHealth(0);
	}

	@Override
	public String getInfo() {
		return "kills a player in a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "kill <player>";
	}
	
}
