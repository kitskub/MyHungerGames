package com.randude14.hungergames.listeners;

import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyListener implements Listener, Runnable {
	private static Map<Location, HungerGame> joinSigns = new HashMap<Location, HungerGame>();

	public static boolean addJoinSign(Location location, String str) {
		if (location == null) return false;
		Block block = location.getBlock();
		if (!(block.getState() instanceof Sign)) return false;
		HungerGame game = GameManager.getGame(str);
		if (game == null) return false;
		joinSigns.put(location, game);
		Sign sign = (Sign) block.getState();
		sign.setLine(0, "[MyHungerGames]");
		sign.setLine(1, "Click the sign");
		sign.setLine(2, "to join");
		sign.setLine(3, str);
		return true;
	}
	
	@EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
	public static void playerClickedBlock(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		HungerGame game = joinSigns.get(event.getClickedBlock().getLocation());
		if (game == null) return;
		game.join(event.getPlayer());
	}
	
	@EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
	public static void onBlockBreak(BlockBreakEvent event) {
		joinSigns.remove(event.getBlock().getLocation());
	}

	public void run() {
		for (Location l : joinSigns.keySet()) {
			HungerGame game = joinSigns.get(l);
			if (game != null && game.getState() == HungerGame.GameState.DELETED) {
				joinSigns.remove(l);
				return;
			}
		}
	}

}
