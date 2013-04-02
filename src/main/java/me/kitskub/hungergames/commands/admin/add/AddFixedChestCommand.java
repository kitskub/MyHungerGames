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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddFixedChestCommand extends PlayerCommand {

	public AddFixedChestCommand() {
		super(Perm.ADMIN_ADD_FIXED_CHEST, Commands.ADMIN_ADD_HELP.getCommand(), "fixedchest");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
	    
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    ChatUtils.send(player, ChatColor.GREEN, "Hit a chest to add it to %s.", game.getName());
	    if (args.length == 2){
		    try {
			    SessionListener.addSession(SessionType.FIXED_CHEST_ADDER, player, args[0], "name", args[1]);
			    return;
		    } catch (NumberFormatException numberFormatException) {}
	    }
	    SessionListener.addSession(SessionType.FIXED_CHEST_ADDER, player, args[0]);
	}

	@Override
	public String getInfo() {
		return "add a fixedchest with a name";
	}

	@Override
	protected String getPrivateUsage() {
		return "fixedchest <game name> <name>";
	}
	
}
