package com.randude14.hungergames.commands.admin.set;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 *
 */
public class SetEnabledCommand extends SubCommand{

    public SetEnabledCommand() {
	    super(Commands.ADMIN_SET_ENABLED);
    }

    @Override
    public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.getGame(args[0]);
	    if (game == null) {
		    ChatUtils.sendDoesNotExist(player, args[0]);
		    return true;
	    }
	    
	    boolean flag;
	    if (args.length == 1) {
		    flag = true;
	    } else {
		    flag = Boolean.valueOf(args[1]);
	    }
	    game.setEnabled(flag);
	    if (flag) {
		    ChatUtils.send(player, "%s has been enabled.", game.getName());
	    } else {
		    ChatUtils.send(player, "%s has been disabled.", game.getName());
	    }
	    return true;
    }
    
}
