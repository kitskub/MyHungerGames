package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.utils.ChatUtils;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends Command {

	public ListCommand() {
		super(Perm.USER_LIST, "list", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {
		ChatUtils.send(cs, ChatColor.GREEN, ChatUtils.getHeadLiner());
		Collection<HungerGame> games = ((GameManager) HungerGames.getInstance().getGameManager()).getRawGames();
		if (games.isEmpty()) {
			ChatUtils.error(cs, "No games have been created yet.");
			return;
		}

		for (HungerGame g : games) {
			ChatUtils.send(cs, ChatColor.GOLD, "- " + g.getInfo());
		}
	}

	@Override
	public String getInfo() {
		return "list games";
	}

	@Override
	protected String getPrivateUsage() {
		return "list";
	}
    
}
