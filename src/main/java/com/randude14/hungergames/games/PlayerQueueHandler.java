package com.randude14.hungergames.games;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults;
import com.randude14.hungergames.HungerGamesBukkit;
import com.randude14.hungergames.api.event.GameEndEvent;
import com.randude14.hungergames.api.Game.GameState;
import com.randude14.hungergames.utils.ChatUtils;
import com.randude14.hungergames.utils.EquatableWeakReference;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerQueueHandler implements Listener, Runnable {
	private static final Queue<String> queuedPlayers = new LinkedList<String>();
	private static final Queue<WeakReference<HungerGame>> queuedGames = new LinkedList<WeakReference<HungerGame>>();
	private static boolean enabled = false;

	public PlayerQueueHandler() {
		enabled = true;
	}

	public static void addPlayer(Player player) {
		if (!enabled) return;
		if (!HungerGamesBukkit.hasPermission(player, Defaults.Perm.USER_AUTO_JOIN_ALLOWED)) return;
		queuedPlayers.offer(player.getName());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		if (!enabled) return;
		if (!Config.getAutoJoinAllowed(event.getGame().getSetup())) return;
		Bukkit.getScheduler().scheduleAsyncDelayedTask(HungerGamesBukkit.getInstance(), this, 20 * 10);
		queuedGames.offer(new EquatableWeakReference<HungerGame>(event.getGame()));
	}
	
	public void run() {
		WeakReference<HungerGame> game = null;
		while ((game = queuedGames.poll()) != null && game.get() == null) {}
		if (game.get().getState() != GameState.STOPPED) return;
		for (int i = 0; i < Math.min(game.get().getSize(), queuedPlayers.size()); i++) {
			Player p = Bukkit.getPlayer(queuedPlayers.poll());
			if (p == null) continue;
			ChatUtils.send(p, "You have been selected to join the game %s", game.get().getName());
			game.get().join(p);
		}
	}

}
