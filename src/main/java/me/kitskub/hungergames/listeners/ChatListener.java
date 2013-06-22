package me.kitskub.hungergames.listeners;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.api.Game;

import java.util.HashSet;
import me.kitskub.hungergames.games.User;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;


public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerChat(PlayerChatEvent event) {
		Game chatterGame = User.get(event.getPlayer()).getGameInEntry().getGame();
		for (Player p : new HashSet<Player>(event.getRecipients())) {
			Game receipientGame = User.get(p).getGameInEntry().getGame();
			if (receipientGame != null && Config.ISOLATE_PLAYER_CHAT.getBoolean(receipientGame.getSetup())) {
				if (chatterGame != null) {
					if (chatterGame.compareTo(receipientGame) == 0 
						&& event.getPlayer().getLocation().getWorld() == p.getLocation().getWorld()) {
						float distanceRequired = Config.CHAT_DISTANCE.getInt(receipientGame.getSetup());
						if (distanceRequired != 0 && event.getPlayer().getLocation().distance(p.getLocation()) >= distanceRequired) {
							if (HungerGames.hasPermission(event.getPlayer(), Perm.ADMIN_CHAT)) {
								if (event.getMessage().startsWith("hg ")) {
									event.setMessage(event.getMessage().substring(3));
									return;
								}
								if (event.getMessage().startsWith("hg")) {
									event.setMessage(event.getMessage().substring(2));
									return;
								}
							}
							//Logging.debug("Cancelling chat because too far.");
							event.getRecipients().remove(p);
						}
					}
					else {
						//Logging.debug("Cancelling chat because games are not the same or different worlds.");
						event.getRecipients().remove(p);
					}
				}
				else {
					if (HungerGames.hasPermission(event.getPlayer(), Perm.ADMIN_CHAT)) {
						if (event.getMessage().startsWith("hg ")) {
							event.setMessage(event.getMessage().substring(3));
							return;
						}
						if (event.getMessage().startsWith("hg")) {
							event.setMessage(event.getMessage().substring(2));
							return;
						}
					}
					//Logging.debug("Cancelling chat because chatter was not in a game.");
					event.getRecipients().remove(p);
				}
			}
		}
	}
}
