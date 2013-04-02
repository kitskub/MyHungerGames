package me.kitskub.hungergames.commands.admin.add;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.listeners.SessionListener;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddJoinSignCommand extends PlayerCommand {

	public AddJoinSignCommand() {
		super(Perm.ADMIN_ADD_JOIN_SIGN, Commands.ADMIN_ADD_HELP.getCommand(), "joinsign");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		if (args.length < 1) {
			ChatUtils.send(player, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);

		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
			return;
		}

		SessionListener.addSession(SessionListener.SessionType.JOIN_SIGN_ADDER, player, game.getName());
		ChatUtils.send(player, ChatColor.GREEN, "Left-click the sign to add it as a join sign.");
	}

	@Override
	public String getInfo() {
		return "add a join sign";
	}

	@Override
	protected String getPrivateUsage() {
		return "joinsign <game name>";
	}

}
