package me.kitskub.hungergames.commands.admin;

import me.kitskub.hungergames.Defaults;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class ForceClearCommand extends Command {

	public ForceClearCommand() {
		super(Perm.ADMIN_FORCE_CLEAR, "forceclear", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		if (args.length < 1) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		String name = (args.length < 1) ? Defaults.Config.DEFAULT_GAME.getGlobalString() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		game = HungerGames.getInstance().getGameManager().getRawGame(name);
		if (game == null) {
			ChatUtils.error(cs, Lang.getNotExist().replace("<item>", name));
			return;
		}
		ChatUtils.send(cs, "Clearing game.");
		((HungerGame) game).clear();
	}

	@Override
	public String getInfo() {
		return "force clear a game. NOT RECOMMENDED";
	}

	@Override
	protected String getPrivateUsage() {
		return "forceclear <game name>";
	}
    
}
