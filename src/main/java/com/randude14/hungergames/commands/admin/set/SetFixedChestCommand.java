package com.randude14.hungergames.commands.admin.set;

import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.listeners.SessionListener;
import com.randude14.hungergames.listeners.SessionListener.SessionType;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 *
 */
public class SetFixedChestCommand extends SubCommand{

    public SetFixedChestCommand() {
	    super(Commands.ADMIN_SET_FIXED_CHEST);
    }

    @Override
    public boolean handle(CommandSender cs, Command cmd, String[] args) {
	    Player player = (Player) cs;
	    if (args.length < 2) {
		    ChatUtils.helpCommand(player, command.getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.INSTANCE.getGame(args[0]);
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return true;
	    }
	    
	    String name = args[1];
	    if (name.equalsIgnoreCase("false")) {
		    SessionListener.addSession(SessionType.FIXED_CHEST_REMOVER, player, game.getName());
		    ChatUtils.send(player, "Click chest to remove it from being a fixed item chest.");
		    return true;
	    }
	    if (!ItemConfig.getFixedChests().contains(name)) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
		    return true;
	    }
	    SessionListener.addSession(SessionType.FIXED_CHEST_ADDER, player, game.getName(), "name", name);
	    ChatUtils.send(player, "Click chest to add it as a fixed item chest.");
	    return true;
    }
    
}
