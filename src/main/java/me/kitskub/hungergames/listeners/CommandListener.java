package me.kitskub.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.games.User;
import me.kitskub.hungergames.utils.ChatUtils;

public class CommandListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		if(message.startsWith("/" + HungerGames.CMD_ADMIN) || message.startsWith("/" + HungerGames.CMD_USER)) return;
		Game session = User.get(player).getGameInEntry().getGame();
		if(session == null) return;
		message = message.split(" ")[0];
		if(Config.USE_COMMAND.getBoolean(session.getSetup()) ^ Config.SPECIAL_COMMANDS.getStringList(session.getSetup()).contains("/" + message)) {
			ChatUtils.error(player, "Cannot use that command while in game %s.", session.getName());
			event.setCancelled(true);
		}
		
	}

}
