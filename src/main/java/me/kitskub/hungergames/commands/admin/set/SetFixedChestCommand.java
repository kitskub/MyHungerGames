package me.kitskub.hungergames.commands.admin.set;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.ItemConfig;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.listeners.SessionListener;
import me.kitskub.hungergames.listeners.SessionListener.SessionType;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class SetFixedChestCommand extends PlayerCommand {

    public SetFixedChestCommand() {
	    super(Perm.ADMIN_SET_FIXED_CHEST, Commands.ADMIN_SET_HELP.getCommand(), "fixedchest");
    }

    @Override
    public void handlePlayer(Player player, String cmd, String[] args) {
	    if (args.length < 2) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    if (game == null) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    String name = args[1];
	    if (name.equalsIgnoreCase("false")) {
		    SessionListener.addSession(SessionType.FIXED_CHEST_REMOVER, player, game.getName());
		    ChatUtils.send(player, "Click chest to remove it from being a fixed item chest.");
		    return;
	    }
	    if (!ItemConfig.getFixedChests().contains(name)) {
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
		    return;
	    }
	    SessionListener.addSession(SessionType.FIXED_CHEST_ADDER, player, game.getName(), "name", name);
	    ChatUtils.send(player, "Click chest to add it as a fixed item chest.");
    }

	@Override
	public String getInfo() {
		return "Sets a chest to a specific fixed chest itemset or removes it from being a fixed chest if name is false";
	}

	@Override
	protected String getPrivateUsage() {
		return "fixedchest <game name> <name|false>";
	}
    
}
