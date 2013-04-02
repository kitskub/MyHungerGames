package me.kitskub.hungergames.commands.admin.remove;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.api.event.GameRemoveEvent;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RemoveGameCommand extends PlayerCommand {

	public RemoveGameCommand() {
		super(Perm.ADMIN_REMOVE_GAME, Commands.ADMIN_REMOVE_HELP.getCommand(), "game");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
	    if (args.length < 1) {
		    ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
		    return;
	    }
	    game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
	    if(game == null){
		    ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
		    return;
	    }
	    
	    HungerGames.getInstance().getGameManager().removeGame(args[0]);
	    Bukkit.getPluginManager().callEvent(new GameRemoveEvent(game));
	    ChatUtils.send(player, ChatColor.GREEN, "%s has been removed.", args[0]);
	}

	@Override
	public String getInfo() {
		return "remove a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "game <game name>";
	}
	
}
