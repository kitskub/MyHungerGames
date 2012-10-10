package com.randude14.hungergames.commands.admin.set;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends PlayerCommand {

    public SetSpawnCommand() {
	    super(Perm.ADMIN_SET_SPAWN, Commands.ADMIN_SET_HELP.getCommand(), "spawn");
    }

    @Override
    public void handlePlayer(Player player, String cmd, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = GameManager.INSTANCE.getRawGame(args[0]);
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    Location loc = player.getLocation();
	    game.setSpawn(loc);
	    ChatUtils.send(player, "Spawn has been set for %s.", game.getName());
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
