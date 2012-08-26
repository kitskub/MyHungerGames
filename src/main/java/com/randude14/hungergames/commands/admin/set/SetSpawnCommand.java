package com.randude14.hungergames.commands.admin.set;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends Command {

    public SetSpawnCommand() {
	    super(Commands.ADMIN_SET_SPAWN, Commands.ADMIN_SET_HELP.getCommand(), "spawn");
    }

    @Override
    public boolean handle(CommandSender cs, String cmd, String[] args) {
	    Player player = (Player) cs;
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return true;
	    }
	    game = GameManager.INSTANCE.getGame(args[0]);
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return true;
	    }
	    
	    Location loc = player.getLocation();
	    game.setSpawn(loc);
	    ChatUtils.send(player, "Spawn has been set for %s.", game.getName());
	    return true;
    }

	@Override
	public String getInfo() {
		return "set the spawnpoint for a game";
	}

	@Override
	public String getUsage() {
		return "/%s set spawn <game name>";
	}
    
}
