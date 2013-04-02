package me.kitskub.hungergames.commands.admin.add;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.listeners.SessionListener;
import me.kitskub.hungergames.listeners.SessionListener.SessionType;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class AddCuboidCommand extends PlayerCommand {

	public AddCuboidCommand() {
		super(Perm.ADMIN_ADD_CUBOID, Commands.ADMIN_ADD_HELP.getCommand(), "cuboid");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
	    if(args.length < 1){
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    ChatUtils.send(player, "Click the two corners add a cuboid.");
	    SessionListener.addSession(SessionType.CUBOID_ADDER, player, game.getName());
	}

	@Override
	public String getInfo() {
		return "add a cuboid";
	}

	@Override
	protected String getPrivateUsage() {
		return "cuboid <game name>";
	}
	
}
