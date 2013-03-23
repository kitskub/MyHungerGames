package com.randude14.hungergames.commands.admin.set;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class SetEnabledCommand extends Command {

    public SetEnabledCommand() {
	    super(Perm.ADMIN_SET_ENABLED, Commands.ADMIN_SET_HELP.getCommand(), "enabled");
    }

    @Override
    public void handle(CommandSender cs, String cmd, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    if (game == null) {
		    ChatUtils.error(cs, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    boolean flag;
	    if (args.length == 1) {
		    flag = true;
	    } else {
		    flag = Boolean.valueOf(args[1]);
	    }
	    game.setEnabled(flag);
	    if (flag) {
		    ChatUtils.send(cs, "%s has been enabled.", game.getName());
	    } else {
		    ChatUtils.send(cs, "%s has been disabled and stopped if it was running.", game.getName());
	    }
    }

	@Override
	public String getInfo() {
		return "enable or disable a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "enabled <game name> [true/false]";
	}
    
}
