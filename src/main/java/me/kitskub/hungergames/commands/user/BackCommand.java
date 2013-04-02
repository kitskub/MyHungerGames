package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BackCommand extends PlayerCommand {

	public BackCommand() {
		super(Perm.USER_BACK, "back", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {		
		if (HungerGames.getInstance().getGameManager().getSession(player) != null) {
			ChatUtils.send(player, "You cannot use that command while you are in-game.");
			return;
		}
		Location loc = ((GameManager) HungerGames.getInstance().getGameManager()).getAndRemoveBackLocation(player);
		if (loc != null) {
			ChatUtils.send(player, "Teleporting you to your back location.");
			player.teleport(loc);
		}
		else {
			ChatUtils.error(player, "For some reason, there was no back location set. Did you already teleport back?");
		}
	}

	@Override
	public String getInfo() {
		return "returns a player to where they were before they joined";
	}

	@Override
	protected String getPrivateUsage() {
		return "back";
	}
	
}
