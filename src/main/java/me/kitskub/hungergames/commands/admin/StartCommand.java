package me.kitskub.hungergames.commands.admin;

import me.kitskub.hungergames.Defaults;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class StartCommand extends Command {

	public StartCommand() {
		super(Perm.ADMIN_START, "start", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
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

		int seconds;

		if (args.length == 2) {//TODO better
			try {
				seconds = Integer.parseInt(args[1]);
			} catch (Exception ex) {
				ChatUtils.error(cs, "'%s' is not an integer.", args[1]);
				return;
			}
		}

		else {
			seconds = Defaults.Config.DEFAULT_TIME.getInt(game.getSetup());
		}
		if (!game.startGame(cs, seconds)) {
			ChatUtils.error(cs, "Failed to start %s.", game.getName());
		}
	}

	@Override
	public String getInfo() {
		return "manually start a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "start [[game name] [seconds]]";
	}
    
}
