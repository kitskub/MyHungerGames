package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.core.LocalPlayer;
import com.randude14.hungergames.core.LocalWorld;
import com.randude14.hungergames.core.ServerInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spout.api.Engine;
import org.spout.api.geo.World;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.Server;
import org.spout.api.entity.Player;

public class SpoutServerInterface extends ServerInterface {
	public Engine game;

	public SpoutServerInterface(Engine game) {
		this.game = game;
	}

	@Override
	public int scheduleAsyncRepeatingTask(Runnable task, long delay, long period) {
		return game.getScheduler().scheduleAsyncRepeatingTask(HungerGames.getPlugin(), task, delay * 50, period * 50, TaskPriority.NORMAL);
	}

	@Override
	public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
		return game.getScheduler().scheduleSyncRepeatingTask(HungerGames.getPlugin(), task, delay, period, TaskPriority.NORMAL);
	}

	@Override
	public void cancelTask(int id) {
		game.getScheduler().cancelTask(id);
	}

	@Override
	public LocalWorld getWorld(String name) {
		return SpoutUtil.getLocalWorld(game.getWorld(name));
	}
    
	@Override
	public List<LocalWorld> getWorlds() {
		Collection<World> worlds = game.getWorlds();
		List<LocalWorld> ret = new ArrayList<LocalWorld>(worlds.size());

		for (World world : worlds) {
		ret.add(SpoutUtil.getLocalWorld(world));
		}

		return ret;
	}

	@Override
	public List<LocalPlayer> getOnlinePlayers() {
		ArrayList<LocalPlayer> players = new ArrayList<LocalPlayer>();
		for (Player p : ((Server) game).getOnlinePlayers()) {
			players.add(new SpoutPlayer(this, p));
		}
		return players;
	}
}
