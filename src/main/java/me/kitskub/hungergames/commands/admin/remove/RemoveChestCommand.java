package me.kitskub.hungergames.commands.admin.remove;

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

public class RemoveChestCommand extends PlayerCommand {

	public RemoveChestCommand() {
		super(Perm.ADMIN_REMOVE_CHEST, Commands.ADMIN_REMOVE_HELP.getCommand(), "chest");
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }

	    SessionListener.addSession(SessionType.CHEST_REMOVER, player, args[0]);
	    ChatUtils.send(player, ChatColor.GREEN, "Hit a chest to remove it from %s.", game.getName());
	}

	@Override
	public String getInfo() {
		return "remove a chest if it added to the game or blacklists it if it isn't";
	}

	@Override
	protected String getPrivateUsage() {
		return "chest <game name>";
	}
}
