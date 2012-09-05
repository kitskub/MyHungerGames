package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.HungerGamesBukkit;
import com.randude14.hungergames.core.LocalPlayer;
import com.randude14.hungergames.core.LocalWorld;
import com.randude14.hungergames.core.ServerInterface;
import com.randude14.hungergames.core.spout.SpoutPlayer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BukkitServerInterface extends ServerInterface {
	public Server server;

	public BukkitServerInterface(Server server) {
		this.server = server;
	}

	@Override
	public int scheduleAsyncRepeatingTask(Runnable task, long delay, long period) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask((HungerGamesBukkit) HungerGames.getPlugin(), task, delay, period);
	}

	@Override
	public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
		return server.getScheduler().scheduleSyncRepeatingTask((HungerGamesBukkit) HungerGames.getPlugin(), task, delay, period);
	}

	@Override
	public void cancelTask(int id) {
		server.getScheduler().cancelTask(id);
	}

	@Override
	public LocalWorld getWorld(String name) {
		return BukkitUtil.getLocalWorld(server.getWorld(name));
	}
	
	@Override
	public List<LocalWorld> getWorlds() {
		List<World> worlds = server.getWorlds();
		List<LocalWorld> ret = new ArrayList<LocalWorld>(worlds.size());

		for (World world : worlds) {
		ret.add(BukkitUtil.getLocalWorld(world));
		}

		return ret;
	}

	@Override
	public List<LocalPlayer> getOnlinePlayers() {
		ArrayList<LocalPlayer> players = new ArrayList<LocalPlayer>();
		for (Player p : server.getOnlinePlayers()) {
			players.add(new BukkitPlayer(this, p));
		}
		return players;
	}
}
